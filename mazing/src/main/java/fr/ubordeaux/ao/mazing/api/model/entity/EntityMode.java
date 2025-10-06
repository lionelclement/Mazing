package fr.ubordeaux.ao.mazing.api.model.entity;

/**
 * Représente un mode ou un état d'un personnage dans le jeu.
 * 
 * Un mode peut correspondre à une animation spécifique (ex : marche, course,
 * attaque)
 * et contient des informations sur le nombre de frames et le comportement de
 * boucle.
 * 
 */
public interface EntityMode {

    /**
     * Retourne le nombre de frames dans l'animation de ce mode.
     * 
     * @return le nombre de frames
     */
    int getNumFrames();

    /**
     * Indique si l'animation de ce mode est en boucle.
     * 
     * @return true si l'animation est cyclique, false sinon
     */
    boolean isLoop();

    /**
     * Retourne l'index ordinal du mode.
     * <p>
     * Cela peut servir pour indexer les tableaux d'animations ou pour trier les
     * modes.
     * </p>
     * 
     * @return l'ordinal du mode
     */
    int ordinal();

}
