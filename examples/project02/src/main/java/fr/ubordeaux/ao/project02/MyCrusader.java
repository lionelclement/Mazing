package fr.ubordeaux.ao.project02;

import fr.ubordeaux.ao.mazing.api.Crusader;

// Classe qui étend fr.ubordeaux.ao.mazing.api.Crusader
// pour connaître le mode courant
public class MyCrusader extends Crusader {

    private Mode currentMode;

    public Mode getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(Mode mode){
        super.setMode(mode);
        this.currentMode = mode;
    }

}
