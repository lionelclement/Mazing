package fr.ubordeaux.ao.project1.view;

import fr.ubordeaux.ao.mazing.api.ICharacter;

public interface AnimationListener {

    void checkCollision(ICharacter<?> character);

    void updatePosition(ICharacter<?> character);
}
