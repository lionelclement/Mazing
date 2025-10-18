package fr.ubordeaux.ao.project01.view;

import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.ICharacter;
import fr.ubordeaux.ao.mazing.api.Crusader;

import java.awt.Color;

public class MazingView {

	private IWindowGame windowGame;
	private ICharacter<?> crusader;

	public MazingView(IWindowGame window) {
		this.windowGame = window;
		init();
	}

	private void init() {

		// Dessine un damier noir et blanc (pour déboguer)
		windowGame.fillCheckerboardBackground(0, 0, 7, 7);

		// Dessine une tuile rouge
		windowGame.addTileBackground(Color.RED, 7, 7, 0);

		// Dessine des cubes avec alternance de couleur
		for (int i = 0; i <= 3; i++) {
			for (int j = 7; j <= 10; j++) {
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

		// Ajoute des objets (ici le sol 12 de la série 000)
		windowGame.fillArea(12, 6, -9, 0, 10, 10);

		// Ajoute un objet (le sol 20 de la série 000)
		windowGame.add(20, 6, 6, 0);

		// Ajoute un décor
		windowGame.add(new int[][][] {
				{
						// -1: NONE
						// 138: série 100, ligne 20, colonne 7 (coin sup. gauche)
						// etc.
						{ -1, -1, -1, -1, -1, -1, -1 },
						{ -1, 138, 110, 110, 110, 139, -1 },
						{ -1, 108, -1, -1, -1, -1, -1 },
						{ -1, 108, -1, -1, -1, -1, -1 },
						{ -1, 108, -1, -1, -1, -1, -1 },
						{ -1, 136, -1, -1, -1, -1, -1 },
						{ -1, -1, -1, -1, -1, -1, -1 },
				},
				{
						{ -1, -1, -1, -1, -1, -1, -1 },
						{ -1, 138, 110, 110, 110, 139, -1 },
						{ -1, 108, -1, -1, -1, -1, -1 },
						{ -1, 108, -1, -1, -1, -1, -1 },
						{ -1, 108, -1, -1, -1, -1, -1 },
						{ -1, 136, -1, -1, -1, -1, -1 },
						{ -1, -1, -1, -1, -1, -1, -1 },
				},
		});
		windowGame.add(new int[][][] {
				{
						{ -1, -1, -1, -1, -1, -1, -1 },
						{ -1, 72, 72, 72, 72, 72, -1 },
						{ -1, 72, 72, 72, 72, 72, -1 },
						{ -1, 72, 72, 72, 72, 72, -1 },
						{ -1, 72, 72, 72, 72, 72, -1 },
						{ -1, 72, 72, 72, 72, 72, -1 },
						{ -1, -1, -1, -1, -1, -1, -1 },
				} });
		windowGame.add(new int[][][] { {
				{ -1, -1, -1, -1, -1, -1, -1 },
				{ -1, -1, -1, -1, -1, -1, -1 },
				{ -1, -1, -1, -1, -1, -1, -1 },
				{ -1, -1, -1, 520, -1, -1, -1 },
				{ -1, -1, 544, -1, -1, -1, -1 },
				{ -1, -1, -1, -1, -1, -1, -1 },
				{ -1, -1, -1, -1, -1, -1, -1 },
		} });

		// Ajoute un décor avec transparence
		windowGame.add(new int[][][] {
				{
						{ -1, -1, -1, -1, -1, -1, -1 },
						{ -1, -1, -1, -1, -1, -1, -1 },
						{ -1, -1, -1, -1, -1, 111, -1 },
						{ -1, -1, -1, -1, -1, 111, -1 },
						{ -1, -1, -1, -1, -1, 111, -1 },
						{ -1, -1, 109, 113, 109, 137, -1 },
						{ -1, -1, -1, -1, -1, -1, -1 },
				},
		},
				// 1f: opaque
				// 0f: transparent
				0.5f);

		// Ajoute un nouveau personnage
		crusader = new Crusader();
		// Le positionne en x=3, y=4, z=0
		crusader.setPosition(3, 4, 0);

		// L'ajoute au windowGame
		windowGame.add(crusader);

		// Affiche le windowGame
		windowGame.setVisible(true);

		// Décale la fenêtre d'affichage de x, y
		int x = 0, y = 0;
		System.out.println("Utilisez les touches l, r, u, d (puis Entrée) pour bouger :");
		while (true) {
			try {
				char c = (char) System.in.read();
				System.in.skip(System.in.available()); // vide le buffer

				switch (c) {
					case 'l' -> x--;
					case 'r' -> x++;
					case 'u' -> y--;
					case 'd' -> y++;
				}
				windowGame.scroll(x, y);
			} catch (Exception e) {
			}
		}

	}

}
