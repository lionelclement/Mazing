package fr.ubordeaux.ao.project00.view;

import fr.ubordeaux.ao.mazing.api.IWindowGame;

import java.awt.Color;

public class MazingView {

	private IWindowGame windowGame;

	public MazingView(IWindowGame window) {
		this.windowGame = window;
		init();
	}

	private void init() {

		// Dessine des cubes avec alternance de couleur
		for (int i = 0; i <= 3; i++) {
			for (int j = 0; j <= 3; j++) {
				for (int k = -5; k <= -2; k++) {
					Color color;
					// Alternance entre deux couleurs
					if ((i + j + k) % 2 == 0) {
						color = Color.BLUE;
					} else {
						color = Color.CYAN;
					}
					windowGame.addCubeBackground(color, i, j, k);
				}
			}
		}

		// Affiche le windowGame
		windowGame.setVisible(true);


	}

}
