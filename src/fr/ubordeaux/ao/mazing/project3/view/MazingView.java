package fr.ubordeaux.ao.mazing.project3.view;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.IBoardGame;
import fr.ubordeaux.ao.mazing.api.Window;
import fr.ubordeaux.ao.mazing.project3.controller.MazingController;
import fr.ubordeaux.ao.mazing.project3.model.MazingModel;

public class MazingView {

	private Window window;
	private IBoardGame boardGame;
	Crusader crusader;

	public MazingView() {
		window = new Window();
		boardGame = window.getBoardGame();

		crusader = new Crusader();
		boardGame.assignPlayer(crusader);
		boardGame.addToSceneMap(crusader);

	}

	public void refresh(MazingModel model) {
	}

	public void addKeyListener(MazingController controller) {
		window.addKeyListener(controller);
	}

	public void setVisible(boolean b) {
		window.setVisible(b);
	}

	public IBoardGame getBoardGame() {
		return window.getBoardGame();
	}

	// J'ajoute une fonction envoyée par le contrôleur à cette vue (à chaque fois
	// que le crusader s'anime)
	public void setPlayerLoopTrigger(Runnable fct) {
		crusader.setTickAnimationTrigger(fct);
	}

}
