package fr.ubordeaux.ao.project00.view;

import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.ICharacter;
import fr.ubordeaux.ao.mazing.api.Crusader;

public class MazingView {

	private IWindowGame windowGame;
	private ICharacter<?> crusader;

	public MazingView(IWindowGame window) {
		this.windowGame = window;
		init();
	}
	
	private void init() {
		// Ajoute un nouveau personnage
		crusader = new Crusader();

		// L'insère dans le windowGame
	   	windowGame.add(crusader);
    	
		// Affiche le windowGame
		windowGame.setVisible(true);
		
	}
}
