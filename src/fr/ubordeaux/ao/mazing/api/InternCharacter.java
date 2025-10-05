package fr.ubordeaux.ao.mazing.api;

/**
 * Interface représentant un personnage interne dans le jeu,
 * qui peut être rendu en 3D et possède des animations.
 *
 * @param <S> type de l'état du personnage, étendant CharacterMode
 */
public interface InternCharacter<S extends CharacterMode> extends IRenderable {

    /**
     * Retourne l'animation courante pour un état donné.
     *
     * @param state l'état du personnage pour lequel récupérer l'animation
     * @return l'animation correspondant à l'état spécifié
     */
    Animation<S> getCurrentAnimation(int state);

    /**
     * Retourne la vitesse du personnage sur l'axe X.
     *
     * @return vitesse actuelle sur X
     */
    public float getSpeedX();

    /**
     * Retourne la vitesse du personnage sur l'axe Y.
     *
     * @return vitesse actuelle sur Y
     */
    public float getSpeedY();

    /**
     * Retourne la vitesse du personnage sur l'axe Z.
     *
     * @return vitesse actuelle sur Z
     */
    public float getSpeedZ();
}
