package fr.ubordeaux.ao.mazing.api.entity;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Représente une animation pour un personnage.
 * 
 * 
 * Gère les frames d'images et d'ombres, la progression de l'animation,
 * les callbacks liés aux différentes étapes de l'animation, et le bouclage.
 *
 */
public class Animation {

	/** Tableau des images de l'animation */
	private final BufferedImage[] images;

	/** Tableau des ombres associées aux images */
	private final BufferedImage[] shadows;

	/** Nombre total de frames */
	private final int frameNumber;

	/** Index approximatif de la frame médiane (milieu de l'animation) */
	private final int frameNumberMid;

	/** Indique si l'animation doit boucler */
	private final boolean loop;

	/** Indique si l'animation est gelée (finie et non bouclée) */
	private boolean frozen;

	/** Index de la frame courante */
	private int frameIndex;

	/**
	 * Constructeur d'une animation.
	 *
	 * @param frames       tableaux des images de l'animation
	 * @param shadows      tableaux des images d'ombre correspondantes
	 * @param frameDelayMs délai entre les frames (non utilisé directement ici)
	 * @param loop         indique si l'animation doit boucler
	 */
	public Animation(BufferedImage[] frames, BufferedImage[] shadows, int frameDelayMs, boolean loop) {
		this.images = frames;
		this.shadows = shadows;
		this.frameNumber = frames.length;
		this.frameNumberMid = frames.length / 2;
		this.loop = loop;
		this.frozen = false;
		Random rand = new Random();
		this.frameIndex = rand.nextInt(frames.length);
	}

	/**
	 * Avance l'animation d'une frame et déclenche les callbacks associés.
	 *
	 * <p>
	 * Les callbacks possibles : begin, mid, tick, loop, end.
	 * </p>
	 *
	 * @param owner personnage propriétaire de l'animation
	 */
	public void tick(IAnimationCallback owner) {
		owner.runTickCallback(frameIndex);
		if (!frozen) {
			if (frameIndex == 0) {
				owner.runBeginCallback();
			}
			if (frameIndex == frameNumberMid) {
				owner.runMidCallback();
			}
			frameIndex++;
		}
		if (frameIndex >= frameNumber) {
			if (loop) {
				frameIndex = 0;
				owner.runLoopCallback();
			} else {
				frameIndex--;
				frozen = true;
				owner.runEndCallback();
			}
		}
	}

	/**
	 * Retourne l'image courante de l'animation.
	 *
	 * @return image actuelle
	 */
	public BufferedImage getCurrentFrame() {
		return images[frameIndex];
	}

	/**
	 * Retourne l'image d'ombre courante de l'animation.
	 *
	 * @return image d'ombre actuelle
	 */
	public BufferedImage getCurrentShadow() {
		return shadows[frameIndex];
	}

	/**
	 * Réinitialise l'animation au début et la dégelée si elle était gelée.
	 */
	public void reset() {
		frameIndex = 0;
		frozen = false;
	}

	/**
	 * Force l'animation à sa dernière frame et la gèle.
	 */
	public void lastFrame() {
		frameIndex = images.length - 1;
		frozen = true;
	}

}
