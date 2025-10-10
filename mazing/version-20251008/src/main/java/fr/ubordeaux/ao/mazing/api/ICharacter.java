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

    void setLoopAnimationTrigger(Consumer<ICharacter<?>> callback);

    void setBeginAnimationTrigger(Consumer<ICharacter<?>> callback);

    void setMidAnimationTrigger(Consumer<ICharacter<?>> callback);

    void setEndAnimationTrigger(Consumer<ICharacter<?>> callback);

    void setTickAnimationTrigger(Consumer<ICharacter<?>> callback);

    void setScale(float scale);

    void setOffset(int x, int y);

}
