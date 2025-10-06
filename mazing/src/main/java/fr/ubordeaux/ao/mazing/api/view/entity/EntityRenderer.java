package fr.ubordeaux.ao.mazing.api.view.entity;

import fr.ubordeaux.ao.mazing.api.model.entity.Entity;
import fr.ubordeaux.ao.mazing.api.model.entity.EntityMode;
import fr.ubordeaux.ao.mazing.api.view.IRenderable;

public interface EntityRenderer extends IRenderable {

    Entity getEntity();

    /**
     * Retourne l'animation courante selon l'état de l'entité.
     * Cet état est récupéré grâce à {@link Entity#getCurrentMode()}.
     *
     * @return l'animation correspondant à l'état spécifié
     */
    Animation getCurrentAnimation();

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
    default void onTick(int frameIndex) {
    }

    /**
     * Exécute le callback de début d'animation
     */
    default void onBeginAnimation() {
    }

    /**
     * Exécute le callback de milieu d'animation
     */
    default void onMiddleAnimation() {
    }

    /**
     * Exécute le callback de fin d'animation
     */
    default void onEndAnimation() {
    }

    /**
     * Exécute le callback de boucle d'animation
     */
    default void onLoopAnimation() {
    }
}
