package fr.ubordeaux.ao.project02;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.WindowGame;

public class Main {

	static WindowGame windowGame;

	public static void main(String[] args) {

		windowGame = new WindowGame();
		init();

	}

	private static void init() {
		
		windowGame.setTitle("Mon jeu à moi");
		// Définit la vitesse de rafraichissement des animations à 65 frames per second 
		windowGame.setFPS(65);
		
		// Matrice z, y, x d'objets à mettre dans windowGame
		// Voir le tableau 500 des images 
		// (mazing/src/main/resources/Dungeon-Pack/Isometric/objects/all_500.png)
		// 520 : tableau 500, 3e ligne, 1e colonne
		// 542 : tableau 500, 5e ligne, 3e colonne
		
		int[][][] items = { {
		  { -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, 520, -1, -1, 542 },
		  { -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1 },
		  } };
		  
		  windowGame.add(items);
		
		// Un nouveau personnage
		MyCrusader crusader = new MyCrusader();

		// définit la vitesse d'animation du mode Crusader.Mode.RUN
		crusader.setFrameRate(Crusader.Mode.RUN, 0.8f);
		
		// définit la vitesse d'animation du mode Crusader.Mode.ATTACK
		crusader.setFrameRate(Crusader.Mode.ATTACK, 0.2f);
		
		// Ajoute le personnage à la scène
		windowGame.add(crusader);
		
		// Définit le mode courant du personnage
		crusader.setCurrentMode(Crusader.Mode.RUN);

		// Définit la méthode envoyée lors des "ticks" des animations
		crusader.setTickAnimationTrigger(new RoundCrusader());

		// Définit la méthode envoyée au début des animations
		crusader.setEndAnimationTrigger(new ContinueCrusader(windowGame));

		// Définit la méthode envoyée au début des animations
		crusader.setBeginAnimationTrigger(c -> {
			if (c instanceof Crusader) {
				if (crusader.getCurrentMode() == Crusader.Mode.ATTACK){
					windowGame.playSound("weapons/slash1");
					return false;
				}
			}
			return true;
		});


		// Affiche la windowGame
		windowGame.setVisible(true);




	}

}
