package fr.ubordeaux.ao.mazing.api;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * SoundManager handles game sounds.
 * Supports preloading sounds, lazy loading, overlapping playback, short clips, and volume control.
 */
public class SoundManager {

    private static final String BASEPATH = "/sounds/";

    private final Map<String, byte[]> soundData = new HashMap<>();
    private final Map<String, AudioFormat> soundFormats = new HashMap<>();
    private final Map<String, FloatControl> volumeControls = new HashMap<>();
    private static SoundManager instance;

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    // -----------------------
    // Public API
    // -----------------------

    /**
     * Preload a sound from the classpath and store it in memory.
     */
    public void loadSound(String key, String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) throw new RuntimeException("Resource not found: " + resourcePath);

            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
            AudioFormat format = ais.getFormat();
            byte[] data = ais.readAllBytes();

            soundData.put(key, data);
            soundFormats.put(key, format);

            // Create a first clip to enable volume control
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControls.put(key, (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN));
            }
            clip.close();

        } catch (Exception e) {
            System.err.println("Error loading sound " + key + ": " + e.getMessage());
        }
    }

    /**
     * Play a sound by key. Will preload from classpath if necessary.
     */
    public void play(String key) {
        try {
            if (!soundData.containsKey(key)) {
                loadSound(key, BASEPATH + key + ".wav");
            }
            playClipCopy(key);
        } catch (Exception e) {
            System.err.println("Error playing sound " + key + ": " + e.getMessage());
        }
    }

    /**
     * Play the first N milliseconds of a sound.
     */
    public void playShortSound(String key, int milliseconds) {
        try {
            if (!soundData.containsKey(key)) {
                loadSound(key, BASEPATH + key + ".wav");
            }

            byte[] data = soundData.get(key);
            AudioFormat format = soundFormats.get(key);

            int frameSize = format.getFrameSize();
            float frameRate = format.getFrameRate();
            long framesToPlay = Math.min(data.length / frameSize,
                    (long) (milliseconds / 1000.0 * frameRate));

            int bytesToPlay = (int) (framesToPlay * frameSize);

            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, bytesToPlay);
            clip.start();
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) clip.close();
            });

        } catch (Exception e) {
            System.err.println("Error playing short sound " + key + ": " + e.getMessage());
        }
    }

    /**
     * Set volume of a preloaded sound.
     * @param key logical key
     * @param volume 0.0 = silent, 1.0 = full
     */
    public void setVolume(String key, float volume) {
        FloatControl gainControl = volumeControls.get(key);
        if (gainControl == null) return;

        if (volume <= 0f) gainControl.setValue(gainControl.getMinimum());
        else {
            float dB = (float) (20.0 * Math.log10(volume));
            if (dB < gainControl.getMinimum()) dB = gainControl.getMinimum();
            if (dB > gainControl.getMaximum()) dB = gainControl.getMaximum();
            gainControl.setValue(dB);
        }
    }

    /**
     * Clears all cached sounds from memory.
     */
    public void clearCache() {
        soundData.clear();
        soundFormats.clear();
        volumeControls.clear();
        System.out.println("Sound cache cleared.");
    }

    // -----------------------
    // Internal
    // -----------------------

    /**
     * Create a new Clip from cached byte[] and play it.
     * Allows overlapping playback.
     */
    private void playClipCopy(String key) throws LineUnavailableException {
        byte[] data = soundData.get(key);
        AudioFormat format = soundFormats.get(key);
        if (data == null || format == null) return;

        Clip clip = AudioSystem.getClip();
        clip.open(format, data, 0, data.length);
        clip.start();
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) clip.close();
        });
    }
}
