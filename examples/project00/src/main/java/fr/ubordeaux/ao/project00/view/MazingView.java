package fr.ubordeaux.ao.project00.view;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.Spider;

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
				for (int k = 0; k <= 3; k++) {
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

		windowGame.add(12, 0, 0, 0);
		windowGame.add(12, 5, 2, 0);

		Crusader crusader = new Crusader();
		windowGame.add(crusader);

		Spider spider = new Spider();
		spider.setPosition(5, 2, 0);
		windowGame.add(spider);


		windowGame.setVisible(true);


	}

}
