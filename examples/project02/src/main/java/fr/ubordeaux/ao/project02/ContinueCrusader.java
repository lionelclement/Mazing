package fr.ubordeaux.ao.project02;

import java.util.function.Consumer;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.ICharacter;

// Implémentation d'une Functional Interface qui passe du mode 
// ATTACK au mode RUN
public class ContinueCrusader implements Consumer<ICharacter<?>> {

    @Override
    public void accept(ICharacter<?> character) {
        if (((MyCrusader)character).getCurrentMode() == Crusader.Mode.ATTACK){
            ((MyCrusader)character).setCurrentMode(Crusader.Mode.RUN);
        }
    }

}
