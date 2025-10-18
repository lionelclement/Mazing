package fr.ubordeaux.ao.project02;

import java.util.function.Predicate;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.ICharacter;
import fr.ubordeaux.ao.mazing.api.WindowGame;

// Implémentation d'une Functional Interface qui passe du mode 
// ATTACK au mode RUN
public class ContinueCrusader implements Predicate<ICharacter<?>> {

    private WindowGame windowGame;

    public ContinueCrusader(WindowGame windowGame) {
        this.windowGame = windowGame;
    }

    @Override
    public boolean test(ICharacter<?> character) {
        if (((MyCrusader) character).getCurrentMode() == Crusader.Mode.RUN) {
            windowGame.playShortSound("weapons/parry", 50);
        }

        if (((MyCrusader) character).getCurrentMode() == Crusader.Mode.ATTACK) {
            windowGame.playSound("tools/hammer_big");

            ((MyCrusader) character).setCurrentMode(Crusader.Mode.RUN);
            return false;
        }
        return true;
    }
}
