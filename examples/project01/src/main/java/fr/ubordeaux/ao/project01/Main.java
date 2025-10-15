package fr.ubordeaux.ao.project01;

import java.awt.Color;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.ICharacterMode;
import fr.ubordeaux.ao.mazing.api.Spider;
import fr.ubordeaux.ao.mazing.api.WindowGame;

public class Main {

	static WindowGame window;

	public static void main(String[] args) {

		window = new WindowGame();
		init();

	}

	private static void init() {
		window.setTitle("Mon jeu à moi");
		//window.setSize(1800, 1169);
		//window.setTileSize(64, 64);
		//window.fillCheckerboardBackground(-12, -12, 48, 48);
		//window.drawTile(Color.RED, 0, 0, 0);
		window.setFPS(65);
		
		  int[][][] items = { {
		  { -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, 520, -1, -1, 540 },
		  { -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1 },
		  { -1, -1, -1, -1, -1, -1, -1 },
		  } };
		  
		  window.add(items);
		 
		MyCrusader crusader = new MyCrusader();
		//crusader.setScale(2.0f);
		//crusader.setPosition(0, 0, 0);
		crusader.setFrameRate(Crusader.Mode.RUN, 0.8f);
		crusader.setFrameRate(Crusader.Mode.ATTACK, 0.3f);
		window.add(crusader);
		crusader.setMode(Crusader.Mode.RUN);
		window.setVisible(true);

		crusader.setTickAnimationTrigger(new RoundCrusader());
		crusader.setEndAnimationTrigger(new ContinueCrusader());


		// Spider spider = new Spider();
		// spider.setPosition(6, 6, 0);
		// spider.setScale(3.5f);
		// window.add(spider);

	}

}
