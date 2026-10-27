package fr.ubordeaux.ao.mazing.view.sound;

import java.util.Random;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/**
 * Synthesizes short combat sound effects for the crusader.
 * Audio samples are generated once and played without blocking rendering.
 * A recorded WAV sound can replace each synthesized effect later.
 */
public final class CrusaderCombatSounds implements AutoCloseable {

    private static final int SAMPLE_RATE = 22_050;
    private static final int ATTACK = 0;
    private static final int DEATH_CRY = 1;
    private static final int DEATH_EXHALE = 2;
    private static final int HIT = 3;
    private static final int SOUND_COUNT = 4;

    private final Clip[] clips = new Clip[SOUND_COUNT];

    /** Creates and buffers all four sound effects. */
    public CrusaderCombatSounds() {
        AudioFormat format = new AudioFormat(
                SAMPLE_RATE, 16, 1, true, false);

        try {
            for (int index = 0; index < SOUND_COUNT; index++) {
                byte[] samples = synthesize(index);
                Clip clip = AudioSystem.getClip();
                clip.open(format, samples, 0, samples.length);
                clips[index] = clip;
            }
        } catch (LineUnavailableException exception) {
            close();
            throw new IllegalStateException(
                    "Cannot initialize crusader combat sounds", exception);
        }
    }

    /** Plays the attack impact. */
    public void playAttack() {
        play(ATTACK);
    }

    /** Plays the high-pitched beginning of the death animation. */
    public void playDeathCry() {
        play(DEATH_CRY);
    }

    /** Plays the exhalation at the end of the death animation. */
    public void playDeathExhale() {
        play(DEATH_EXHALE);
    }

    /** Plays a short vocal-like hit grunt. */
    public void playHit() {
        play(HIT);
    }

    private void play(int index) {
        Clip clip = clips[index];
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    /** Releases all audio resources. */
    @Override
    public void close() {
        for (Clip clip : clips) {
            if (clip != null) {
                clip.close();
            }
        }
    }

    private static byte[] synthesize(int effect) {
        double duration = switch (effect) {
            case ATTACK -> 0.24;
            case DEATH_CRY -> 0.28;
            case DEATH_EXHALE -> 0.53;
            case HIT -> 0.28;
            default -> throw new IllegalArgumentException("Unknown sound effect");
        };

        int sampleCount = (int) (SAMPLE_RATE * duration);
        byte[] pcm = new byte[sampleCount * 2];
        Random random = new Random(7_919L + effect);
        double filteredNoise = 0.0;

        for (int i = 0; i < sampleCount; i++) {
            double t = i / (double) SAMPLE_RATE;
            double progress = i / (double) sampleCount;
            double noise = 2.0 * random.nextDouble() - 1.0;
            filteredNoise = 0.92 * filteredNoise + 0.08 * noise;

            double value = switch (effect) {
                case ATTACK -> {
                    double envelope = Math.min(1.0, t * 900.0)
                            * Math.exp(-19.0 * t);
                    double lowImpact = Math.sin(2.0 * Math.PI * 110.0 * t);
                    double metal = Math.sin(2.0 * Math.PI
                            * (950.0 * t - 1400.0 * t * t));
                    yield envelope * (0.73 * noise
                            + 0.33 * lowImpact + 0.23 * metal);
                }
                case DEATH_CRY -> {
                    double envelope = Math.min(1.0, t * 75.0)
                            * Math.pow(1.0 - progress, 1.3);
                    double fallingPitch = Math.sin(2.0 * Math.PI
                            * (880.0 * t - 620.0 * t * t));
                    yield 0.63 * envelope
                            * (0.83 * fallingPitch + 0.17 * noise);
                }
                case DEATH_EXHALE -> {
                    double envelope = Math.min(1.0, t * 25.0)
                            * Math.pow(1.0 - progress, 1.7);
                    double breath = noise - filteredNoise;
                    yield 0.48 * envelope * breath;
                }
                case HIT -> {
                    double envelope = Math.min(1.0, t * 65.0)
                            * Math.pow(1.0 - progress, 2.1);
                    double grunt = Math.sin(2.0 * Math.PI
                            * (160.0 * t - 90.0 * t * t));
                    double vocal = 0.34 * Math.sin(2.0 * Math.PI * 520.0 * t)
                            + 0.18 * Math.sin(2.0 * Math.PI * 1050.0 * t);
                    double breath = (t < 0.055 || t > 0.18) ? noise : 0.0;
                    yield 0.62 * envelope
                            * (0.58 * grunt + vocal + 0.28 * breath);
                }
                default -> 0.0;
            };

            value = Math.max(-1.0, Math.min(1.0, value));
            short sample = (short) Math.round(value * Short.MAX_VALUE);
            pcm[i * 2] = (byte) sample;
            pcm[i * 2 + 1] = (byte) (sample >>> 8);
        }
        return pcm;
    }
}
