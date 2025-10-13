package fr.ubordeaux.ao.project1.gameloop;

import fr.ubordeaux.ao.mazing.api.ICharacter;

public interface AnimationListener {

    void checkCollision(ICharacter<?> character);

    void updatePosition(ICharacter<?> character);

    void updateMode(ICharacter<?> e);
}
