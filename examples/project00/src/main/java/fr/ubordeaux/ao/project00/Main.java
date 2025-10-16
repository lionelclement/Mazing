package fr.ubordeaux.ao.project00;

import fr.ubordeaux.ao.mazing.api.ICharacter;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.WindowGame;

public class Main {
    public static void main(String[] args) {

        IWindowGame window = new WindowGame();
        window.setFPS(65);
		

        ICharacter<?> crusader;

        // Ajoute un nouveau personnage
        crusader = new Crusader();

        // L'insère dans le windowGame
        window.add(crusader);

        // Affiche le windowGame
        window.setVisible(true);

    }
}
