package fr.ubordeaux.ao.mazing.api;

/**
 * Interface représentant un personnage dans le jeu.
 * 
 * @param <S> Type représentant le mode du personnage.
 */
public interface Character<S extends CharacterMode> extends InternCharacter<S> {

    /**
     * Définit la position du personnage.
     * 
     * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @param z Coordonnée Z (hauteur).
     */
    void setPosition(float x, float y, float z);

    /**
     * Définit la coordonnée X du personnage.
     * 
     * @param x Nouvelle coordonnée X.
     */
    void setX(float x);

    /**
     * Définit la coordonnée Y du personnage.
     * 
     * @param y Nouvelle coordonnée Y.
     */
    void setY(float y);

    /**
     * Définit la coordonnée Z du personnage.
     * 
     * @param z Nouvelle coordonnée Z.
     */
    void setZ(float z);

    /**
     * Définit la vitesse du personnage selon chaque axe.
     * 
     * @param xSpeed Vitesse selon X.
     * @param ySpeed Vitesse selon Y.
     * @param zSpeed Vitesse selon Z.
     */
    void setSpeed(float xSpeed, float ySpeed, float zSpeed);

    /**
     * Fait marcher le personnage.
     */
    void walk();

    /**
     * Fait courir le personnage.
     */
    void run();

    /**
     * Arrête le personnage.
     */
    void stop();

    /**
     * Retourne la direction actuelle du personnage.
     * 
     * @return La direction actuelle.
     */
    Direction getCurrentDirection();

    /**
     * Définit la direction du personnage.
     * 
     * @param direction La nouvelle direction.
     */
    void setDirection(Direction direction);

    /**
     * Retourne le mode actuel du personnage.
     * 
     * @return Le mode actuel.
     */
    S getCurrentMode();

    /**
     * Change la direction du personnage vers une direction cible.
     * 
     * @param targetDirection La direction cible.
     */
    void changeDirection(Direction targetDirection);

    /**
     * Change la direction du personnage en utilisant une chaîne de caractères.
     * 
     * @param targetDirection Nom de la direction cible (ex: "NORTH").
     */
    void changeDirection(String targetDirection);

    /**
     * Change le mode du personnage.
     * 
     * @param mode Nouveau mode du personnage.
     */
    void changeMode(S mode);

    /**
     * Change le mode du personnage en utilisant une chaîne de caractères.
     * 
     * @param mode Nom du mode à appliquer (ex. "IDLE", "WALK", etc).
     */
    void changeMode(String mode);

    /**
     * Définit une action à exécuter à chaque boucle d'animation.
     * 
     * @param callback Fonction à exécuter.
     */
    void setLoopAnimationTrigger(Runnable callback);

    /**
     * Définit une action à exécuter au début de l'animation.
     * 
     * @param callback Fonction à exécuter.
     */
    void setBeginAnimationTrigger(Runnable callback);

    /**
     * Définit une action à exécuter au milieu de l'animation.
     * 
     * @param callback Fonction à exécuter.
     */
    void setMidAnimationTrigger(Runnable callback);

    /**
     * Définit une action à exécuter à la fin de l'animation.
     * 
     * @param callback Fonction à exécuter.
     */
    void setEndAnimationTrigger(Runnable callback);

    /**
     * Définit une action à exécuter à chaque tick de l'animation.
     * 
     * @param callback Fonction à exécuter.
     */
    void setTickAnimationTrigger(Runnable callback);
}
