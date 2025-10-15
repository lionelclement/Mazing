package fr.ubordeaux.ao.project01;

import fr.ubordeaux.ao.mazing.api.Crusader;

public class MyCrusader extends Crusader {

    private Mode mode;

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode){
        super.setMode(mode);
        this.mode = mode;
    }

}
