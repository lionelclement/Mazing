package fr.ubordeaux.ao.project0.view;

import fr.ubordeaux.ao.mazing.api.ICharacter;

public interface AnimationListener {
    void onTick(ICharacter<?> character);
}
