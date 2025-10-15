package fr.ubordeaux.ao.project01;

import java.util.function.Consumer;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.ICharacter;

public class ContinueCrusader implements Consumer<ICharacter<?>> {

    @Override
    public void accept(ICharacter<?> character) {
        if (((MyCrusader)character).getMode() == Crusader.Mode.ATTACK){
            ((MyCrusader)character).setMode(Crusader.Mode.RUN);
        }
    }

}
