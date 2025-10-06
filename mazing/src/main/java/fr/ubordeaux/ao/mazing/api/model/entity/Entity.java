package fr.ubordeaux.ao.mazing.api.model.entity;

import fr.ubordeaux.ao.mazing.api.Direction;

/**
 * Interface représentant un personnage dans le jeu.
 */
public interface Entity {

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

    float getX();

    float getY();

    float getZ();

    /**
     * Définit la vitesse du personnage selon chaque axe.
     * 
     * @param xSpeed Vitesse selon X.
     * @param ySpeed Vitesse selon Y.
     * @param zSpeed Vitesse selon Z.
     */
    void setSpeed(float xSpeed, float ySpeed, float zSpeed);


    /**
     * Retourne la vitesse du personnage sur l'axe X.
     *
     * @return vitesse actuelle sur X
     */
    float getSpeedX();

    /**
     * Retourne la vitesse du personnage sur l'axe Y.
     *
     * @return vitesse actuelle sur Y
     */
    float getSpeedY();

    /**
     * Retourne la vitesse du personnage sur l'axe Z.
     *
     * @return vitesse actuelle sur Z
     */
    float getSpeedZ();

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
    EntityMode getCurrentMode();

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
    void changeMode(EntityMode mode);

    /**
     * Change le mode du personnage en utilisant une chaîne de caractères.
     * 
     * @param mode Nom du mode à appliquer (ex. "IDLE", "WALK", etc).
     */
    void changeMode(String mode);

    /**
     * Exécute le callback de tick d'animation
     *
     * @param frameIndex the current animation frame
     */
    default void onTick(int frameIndex){}

    /**
     * Exécute le callback de début d'animation
     */
    default void onBeginAnimation(){}

    /**
     * Exécute le callback de milieu d'animation
     */
    default void onMiddleAnimation(){}

    /**
     * Exécute le callback de fin d'animation
     */
    default void onEndAnimation(){}

    /**
     * Exécute le callback de boucle d'animation
     */
    default void onLoopAnimation(){}

}
