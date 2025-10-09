package fr.ubordeaux.ao.mazing.api;

import java.util.function.Consumer;

/**
 * Interface représentant un personnage dans le jeu.
 * 
 * @param <S> Type représentant le mode du personnage.
 */
public interface ICharacter<S extends CharacterMode> {

    void setPosition(float x, float y, float z);

    void setDirection(Direction targetDirection);

    void setDirection(String targetDirection);

    void setMode(S mode);

    void setMode(String mode);

    void setLoopAnimationTrigger(Runnable callback);

    void setBeginAnimationTrigger(Runnable callback);

    void setMidAnimationTrigger(Runnable callback);

    void setEndAnimationTrigger(Runnable callback);

    void setTickAnimationTrigger(Consumer<ICharacter<?>> callback);

}
