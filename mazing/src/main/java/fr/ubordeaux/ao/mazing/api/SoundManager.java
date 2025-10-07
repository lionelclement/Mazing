package fr.ubordeaux.ao.mazing.api;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire des sons du jeu. Permet de charger, lire, arrêter et régler le
 * volume des sons associés aux personnages et événements.
 */
public class SoundManager {

	private final Map<String, Clip> soundClips = new HashMap<>();
	private final Map<String, FloatControl> volumeControls = new HashMap<>();

	/**
	 * Charge un son depuis le classpath.
	 *
	 * @param name         clé pour identifier le son
	 * @param resourcePath chemin de la ressource, ex:
	 *                     "/kenney_impact-sounds/Audio/footstep_carpet_000.wav"
	 */
	public void loadSound(String name, String resourcePath) {
		try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
			if (is == null) {
				throw new RuntimeException("Resource not found: " + resourcePath);
			}

			try (BufferedInputStream bis = new BufferedInputStream(is);
					AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis)) {

				Clip clip = AudioSystem.getClip();
				clip.open(audioStream);

				soundClips.put(name, clip);

				if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
					FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					volumeControls.put(name, gainControl);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setVolume(String name, float volume) {
		FloatControl gainControl = volumeControls.get(name);
		if (gainControl != null) {
			if (volume <= 0f) {
				gainControl.setValue(gainControl.getMinimum()); // silence
			} else {
				float dB = (float) (20.0 * Math.log10(volume));
				if (dB < gainControl.getMinimum()) {
					dB = gainControl.getMinimum();
				}
				if (dB > gainControl.getMaximum()) {
					dB = gainControl.getMaximum();
				}
				gainControl.setValue(dB);
			}
		}
	}

	/**
	 * Joue le son correspondant à la clé. Si le son est déjà en train de jouer, il
	 * redémarre.
	 *
	 * @param name clé du son
	 */
	public void play(String name) {
		Clip clip = soundClips.get(name);
		if (clip != null) {
			if (clip.isRunning()) {
				clip.stop();
			}
			clip.setFramePosition(0);
			clip.start();
		}
	}

	/**
	 * Arrête le son correspondant à la clé.
	 *
	 * @param name clé du son
	 */
	public void stop(String name) {
		Clip clip = soundClips.get(name);
		if (clip != null && clip.isRunning()) {
			clip.stop();
		}
	}

	/**
	 * Libère les ressources audio.
	 */
	public void closeAll() {
		for (Clip clip : soundClips.values()) {
			clip.stop();
			clip.close();
		}
		soundClips.clear();
		volumeControls.clear();
	}
}
