package fr.ubordeaux.ao.mazing.api;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Interface générique représentant un personnage ou une entité animée dans le jeu.
 *
 * 
 * Cette interface définit le comportement commun à tout personnage affichable
 * dans le moteur isométrique (par exemple : {@link Crusader}, {@link Spider}, etc.).
 * 
 *
 * 
 * Elle permet de :
 * 
 *   -positionner le personnage dans l’espace 3D (x, y, z)
 *   -changer son orientation (direction)
 *   -modifier son état ou animation (mode)
 *   -définir des callbacks déclenchés à différents moments des animations
 *   -ajuster la taille (scale) ou le décalage (offset) visuel
 * 
 * 
 *
 * @param <S> Type représentant les modes possibles du personnage
 *            (par ex. {@link Crusader.Mode} ou {@link Spider.Mode})
 */
public interface ICharacter<S extends CharacterMode> {

    // =========================
    // 📍 POSITION ET DIRECTION
    // =========================

    /**
     * Définit la position du personnage dans l’espace du jeu.
     *
     * @param x position horizontale (axe des colonnes)
     * @param y position verticale (axe des lignes)
     * @param z hauteur (niveau, pour effets de saut ou décalage vertical)
     */
    void setPosition(float x, float y, float z);

    /**
     * Définit la direction vers laquelle le personnage est orienté.
     *
     * @param targetDirection direction cible (ex. {@link Direction#NORTH})
     */
    void setDirection(Direction targetDirection);

    /**
     * Définit la direction à partir d’un nom de direction.
     * 
     * Exemple : "SOUTH", "NORTHWEST", "EAST"...
     * 
     *
     * @param targetDirection nom de la direction (insensible à la casse)
     */
    void setDirection(String targetDirection);


    // =========================
    // 🎬 MODES / ANIMATIONS
    // =========================

    /**
     * Définit le mode actuel du personnage.
     * 
     * Le mode détermine l’animation à jouer (ex. {@code WALK}, {@code RUN}, {@code ATTACK}, etc.).
     * 
     *
     * @param mode mode du personnage
     */
    void setMode(S mode);

    /**
     * Définit le mode actuel à partir de son nom.
     * 
     * Exemple : "IDLE", "WALK", "RUN", "JUMP", "DEATH"...
     * 
     *
     * @param mode nom du mode (insensible à la casse)
     */
    void setMode(String mode);


    /**
     * Définit la vitesse d'animation
     * 
     *
     * @param mode
     * @param speed vitesse
     */
    void setSpeed(S mode, float speed);


    // =========================
    // ⏱️ CALLBACKS D’ANIMATION
    // =========================

    /**
     * Déclencheur appelé à la fin de chaque boucle complète d’animation.
     * 
     * Utile pour répéter des comportements périodiques (par ex. marche, vol, nage...).
     * 
     *
     * @param callback fonction appelée à chaque fin de boucle d’animation
     */
    void setLoopAnimationTrigger(Consumer<ICharacter<?>> callback);

    /**
     * Déclencheur appelé au début de l’animation.
     * 
     * Peut servir à initialiser un effet ou un son.
     * 
     *
     * @param callback fonction appelée au début de l’animation
     */
    void setBeginAnimationTrigger(Consumer<ICharacter<?>> callback);

    /**
     * Déclencheur appelé au milieu de l’animation.
     * 
     * Souvent utilisé pour synchroniser un effet (par exemple : impact d’une attaque).
     * 
     *
     * @param callback fonction appelée à la moitié de l’animation
     */
    void setMidAnimationTrigger(Consumer<ICharacter<?>> callback);

    /**
     * Déclencheur appelé à la fin de l’animation.
     * 
     * Peut servir à remettre le personnage à l’état {@code IDLE}, ou enchaîner une autre animation.
     * 
     *
     * @param callback fonction appelée à la fin de l’animation
     */
    void setEndAnimationTrigger(Consumer<ICharacter<?>> callback);

    /**
     * Déclencheur appelé à chaque "tick" d’animation (frame ou mise à jour du jeu).
     * 
     * Idéal pour mettre à jour dynamiquement la position, la physique ou d’autres paramètres.
     * 
     *
     * @param callback fonction appelée à chaque tick d’animation
     */
    void setTickAnimationTrigger(Consumer<ICharacter<?>> callback);


    // =========================
    // 🧩 APPARENCE VISUELLE
    // =========================

    /**
     * Définit un facteur d’échelle pour le personnage.
     * 
     * Exemple : 1.0 = taille normale, 2.0 = deux fois plus grand.
     * 
     *
     * @param scale facteur d’échelle (> 0)
     */
    void setScale(float scale);

    UUID getId();

}
