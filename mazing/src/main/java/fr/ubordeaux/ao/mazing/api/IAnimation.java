package fr.ubordeaux.ao.mazing.api;

import java.awt.image.BufferedImage;

/**
 * Interface représentant une animation d'un personnage.
 *
 * @param <S> type de l'état du personnage, étendant CharacterMode
 */
public interface IAnimation<S extends CharacterMode> {

	/**
	 * Met à jour l'animation du personnage à chaque tick.
	 *
	 * @param owner le personnage possédant cette animation
	 */
	public void tick(AbstractCharacter<? extends CharacterMode> owner);

	/**
	 * Retourne l'image courante de l'animation.
	 *
	 * @return l'image actuelle (frame) de l'animation
	 */
	public BufferedImage getCurrentFrame();

	/**
	 * Retourne l'image de l'ombre associée à la frame courante.
	 *
	 * @return l'image de l'ombre pour la frame actuelle
	 */
	public BufferedImage getCurrentShadow();

	/**
	 * Réinitialise l'animation au début.
	 */
	public void reset();

	/**
	 * Force l'animation à afficher sa dernière frame.
	 */
	public void lastFrame();
}
