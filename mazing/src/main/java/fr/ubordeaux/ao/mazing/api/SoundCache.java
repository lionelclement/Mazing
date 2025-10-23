package fr.ubordeaux.ao.mazing.api;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache statique des Clips pour Crusader, Spider, etc.
 * Permet limite de sons simultanés.
 */
public class SoundCache {

    private static final Map<String, Clip> cache = new HashMap<>();
    private static final int MAX_SIMULTANEOUS_SOUNDS = 10;
    private static int currentPlaying = 0;

    /**
     * Charge un clip si absent dans le cache.
     */
    public static void loadIfAbsent(String name, String path, float volume) {
        if (!cache.containsKey(name)) {
            // Charger les données brutes
            SoundManager.getInstance().loadSound(name, path, volume);
            // Créer un Clip et le stocker dans le cache
            Clip clip = SoundManager.getInstance().createClip(name);
            if (clip != null) {
                cache.put(name, clip);
            }
        }
    }

    /**
     * Joue un Clip depuis le cache, limite de saturation.
     */
    public static void playCached(String name) {
        Clip clip = cache.get(name);
        if (clip == null) {
            System.err.println("Sound not in cache: " + name);
            return;
        }

        synchronized (SoundCache.class) {
            if (currentPlaying >= MAX_SIMULTANEOUS_SOUNDS) return;
            currentPlaying++;
        }

        new Thread(() -> {
            try {
                clip.stop();
                clip.setFramePosition(0);
                clip.start();
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        synchronized (SoundCache.class) {
                            currentPlaying--;
                        }
                    }
                });
            } catch (Exception e) {
                System.err.println("Error playing cached sound " + name + ": " + e.getMessage());
                synchronized (SoundCache.class) {
                    currentPlaying--;
                }
            }
        }).start();
    }
}
