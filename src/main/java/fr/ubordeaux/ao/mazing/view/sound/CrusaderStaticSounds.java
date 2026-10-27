package fr.ubordeaux.ao.mazing.view.sound;

import java.util.Random;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/**
 * Plays synthesized sound effects for stationary crusader animations.
 * Sounds are buffered once and do not require external audio files.
 */
public final class CrusaderStaticSounds implements AutoCloseable {

    private static final int SAMPLE_RATE = 22_050;
    private static final int JUMP_TAKEOFF = 0;
    private static final int JUMP_LANDING = 1;
    private static final int IDLE_BREATH = 2;
    private static final int SHIELD_BLOCK = 3;

    private final Clip[] clips = new Clip[4];

    /** Creates and loads the four sound effects. */
    public CrusaderStaticSounds() {
        AudioFormat format = new AudioFormat(
                SAMPLE_RATE, 16, 1, true, false);

        try {
            for (int index = 0; index < clips.length; index++) {
                byte[] samples = synthesize(index);
                Clip clip = AudioSystem.getClip();
                clip.open(format, samples, 0, samples.length);
                clips[index] = clip;
            }
        } catch (LineUnavailableException exception) {
            close();
            throw new IllegalStateException(
                    "Cannot initialize crusader stationary sounds", exception);
        }
    }

    /** Plays a rising air movement at takeoff. */
    public void playJumpTakeoff() {
        play(JUMP_TAKEOFF);
    }

    /** Plays a low impact when the crusader lands. */
    public void playJumpLanding() {
        play(JUMP_LANDING);
    }

    /** Plays a quiet breath during the idle animation. */
    public void playIdleBreath() {
        play(IDLE_BREATH);
    }

    /** Plays a metallic shield movement during blocking. */
    public void playShieldBlock() {
        play(SHIELD_BLOCK);
    }

    private void play(int index) {
        Clip clip = clips[index];
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    /** Releases the buffered audio clips. */
    @Override
    public void close() {
        for (Clip clip : clips) {
            if (clip != null) {
                clip.close();
            }
        }
    }

    private static byte[] synthesize(int sound) {
        double duration = switch (sound) {
            case JUMP_TAKEOFF -> 0.28;
            case JUMP_LANDING -> 0.25;
            case IDLE_BREATH -> 0.43;
            case SHIELD_BLOCK -> 0.36;
            default -> throw new IllegalArgumentException("Unknown sound");
        };

        int sampleCount = (int) (SAMPLE_RATE * duration);
        byte[] data = new byte[sampleCount * 2];
        Random random = new Random(57_221L + sound);
        double smoothedNoise = 0.0;

        for (int index = 0; index < sampleCount; index++) {
            double time = index / (double) SAMPLE_RATE;
            double progress = index / (double) sampleCount;
            double noise = 2.0 * random.nextDouble() - 1.0;
            smoothedNoise = 0.94 * smoothedNoise + 0.06 * noise;

            double value = switch (sound) {
                case JUMP_TAKEOFF -> {
                    double envelope = Math.min(1.0, time * 50.0)
                            * Math.pow(1.0 - progress, 1.6);
                    double risingTone = Math.sin(2.0 * Math.PI
                            * (180.0 * time + 850.0 * time * progress * 0.5));
                    yield 0.36 * envelope * (0.65 * noise
                            + 0.35 * risingTone);
                }
                case JUMP_LANDING -> {
                    double envelope = Math.min(1.0, time * 600.0)
                            * Math.exp(-20.0 * time);
                    double impact = Math.sin(2.0 * Math.PI
                            * (100.0 * time - 65.0 * time * time));
                    yield 0.57 * envelope * (0.6 * impact
                            + 0.4 * noise);
                }
                case IDLE_BREATH -> {
                    double envelope = Math.pow(
                            Math.sin(Math.PI * progress), 2.0);
                    yield 0.15 * envelope
                            * (0.7 * smoothedNoise + 0.3 * noise);
                }
                case SHIELD_BLOCK -> {
                    double envelope = Math.min(1.0, time * 450.0)
                            * Math.exp(-11.0 * time);
                    double resonance = 0.5 * Math.sin(
                            2.0 * Math.PI * 690.0 * time)
                            + 0.32 * Math.sin(
                            2.0 * Math.PI * 1087.0 * time)
                            + 0.2 * Math.sin(
                            2.0 * Math.PI * 1573.0 * time);
                    yield 0.48 * envelope * (resonance + 0.22 * noise);
                }
                default -> 0.0;
            };

            value = Math.max(-1.0, Math.min(1.0, value));
            short sample = (short) Math.round(value * Short.MAX_VALUE);
            data[index * 2] = (byte) sample;
            data[index * 2 + 1] = (byte) (sample >>> 8);
        }
        return data;
    }
}
