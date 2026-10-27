package fr.ubordeaux.ao.mazing.view.sound;

import java.util.Random;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/**
 * Plays a short, synthesized footstep sound effect.
 *
 * The audio is generated once and kept in memory.
 */
public final class FootstepSound implements AutoCloseable {

    private static final int SAMPLE_RATE = 22050;
    private static final int DURATION_MILLIS = 140;
    private static final int SIMULTANEOUS_SOUNDS = 2;

    private final Clip[] clips = new Clip[SIMULTANEOUS_SOUNDS];
    private int nextClipIndex;

    /**
     * Creates and prepares the footstep audio clips.
     */
    public FootstepSound() {
        byte[] samples = generateFootstepSamples();

        AudioFormat format = new AudioFormat(
                SAMPLE_RATE, 16, 1, true, false);

        try {
            for (int clipIndex = 0;
                    clipIndex < clips.length;
                    clipIndex++) {

                Clip clip = AudioSystem.getClip();
                clip.open(format, samples, 0, samples.length);
                clips[clipIndex] = clip;
            }
        } catch (LineUnavailableException exception) {
            close();
            throw new IllegalStateException(
                    "Cannot initialize footstep audio", exception);
        }
    }

    /**
     * Plays one footstep without blocking the animation.
     */
    public void play() {
        Clip clip = clips[nextClipIndex];

        nextClipIndex = (nextClipIndex + 1) % clips.length;

        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    /**
     * Releases the audio resources.
     */
    @Override
    public void close() {
        for (Clip clip : clips) {
            if (clip != null) {
                clip.close();
            }
        }
    }

    private static byte[] generateFootstepSamples() {
        int sampleCount =
                SAMPLE_RATE * DURATION_MILLIS / 1000;

        byte[] samples = new byte[sampleCount * 2];
        Random random = new Random(12345L);

        for (int sampleIndex = 0;
                sampleIndex < sampleCount;
                sampleIndex++) {

            double progress =
                    sampleIndex / (double) sampleCount;

            double timeSeconds =
                    sampleIndex / (double) SAMPLE_RATE;

            double attack = Math.min(1.0, progress * 65.0);
            double decay = Math.pow(1.0 - progress, 5);

            double noise =
                    2.0 * random.nextDouble() - 1.0;

            double lowFrequencySound = Math.sin(
                    2.0 * Math.PI
                    * (75.0 * timeSeconds
                    - 25.0 * timeSeconds * timeSeconds));

            double amplitude =
                    (0.6 * noise + 0.4 * lowFrequencySound)
                    * attack * decay * 0.5;

            short sample = (short) Math.round(
                    amplitude * Short.MAX_VALUE);

            samples[sampleIndex * 2] =
                    (byte) (sample & 0xff);

            samples[sampleIndex * 2 + 1] =
                    (byte) ((sample >>> 8) & 0xff);
        }

        return samples;
    }
}
