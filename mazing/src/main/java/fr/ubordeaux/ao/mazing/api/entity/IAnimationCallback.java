package fr.ubordeaux.ao.mazing.api.entity;

public interface IAnimationCallback {

    /**
     * Définit une action à exécuter à chaque boucle d'animation.
     *
     * @param callback Fonction à exécuter.
     */
    void setLoopAnimationCallback(Runnable callback);

    /**
     * Définit une action à exécuter au début de l'animation.
     *
     * @param callback Fonction à exécuter.
     */
    void setBeginAnimationCallback(Runnable callback);

    /**
     * Définit une action à exécuter au milieu de l'animation.
     *
     * @param callback Fonction à exécuter.
     */
    void setMidAnimationCallback(Runnable callback);

    /**
     * Définit une action à exécuter à la fin de l'animation.
     *
     * @param callback Fonction à exécuter.
     */
    void setEndAnimationCallback(Runnable callback);

    /**
     * Définit une action à exécuter à chaque tick de l'animation.
     *
     * @param callback Fonction à exécuter.
     */
    void setTickAnimationCallback(Runnable callback);

    /**
     * Exécute le callback de tick d'animation
     *
     * @param frameIndex the current animation frame
     */
    void runTickCallback(int frameIndex);

    /**
     * Exécute le callback de début d'animation
     */
    void runBeginCallback();

    /**
     * Exécute le callback de milieu d'animation
     */
    void runMidCallback();

    /**
     * Exécute le callback de fin d'animation
     */
    void runEndCallback();

    /**
     * Exécute le callback de boucle d'animation
     */
    void runLoopCallback();
}
