package fr.ubordeaux.ao.mazing.project1.view;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.IBoardGame;
import fr.ubordeaux.ao.mazing.api.Window;
import fr.ubordeaux.ao.mazing.project1.controller.MazingController;
import fr.ubordeaux.ao.mazing.project1.model.MazingModel;
import fr.ubordeaux.ao.mazing.project1.model.Point;

public class MazingView {

	private Window window;
	private IBoardGame boardGame;

	public MazingView() {
		window = new Window();
		boardGame = window.getBoardGame();

		Crusader crusader = new Crusader();
		boardGame.assignPlayer(crusader);
		boardGame.addToSceneMap(crusader);
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

	public void bindToModel(MazingModel model) {
		model.setListener(event -> {
			switch (event.type) {
				case PLAYER_POS:
					Point pos = (Point) event.data;
					boardGame.updatePlayerPosition(
							(float) pos.getX(),
							(float) pos.getY(),
							(float) pos.getZ());
					break;

				case PLAYER_SPEED:
					Point speed = (Point) event.data;
					boardGame.updatePlayerSpeed(
							(float) speed.getX(),
							(float) speed.getY(),
							(float) speed.getZ());
					break;

				case PLAYER_MODE:
					String mode = (String) event.data;
					boardGame.updatePlayerMode(mode);
					break;
			}
		});
	}

}
