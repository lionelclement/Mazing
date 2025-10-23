package fr.ubordeaux.ao.project05;

import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.WindowGame;
import fr.ubordeaux.ao.project05.view.MazingView;

public class Main {
    public static void main(String[] args) {
    	
    	IWindowGame window = new WindowGame();
    	new MazingView(window);
    	
    }

}
