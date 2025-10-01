package fr.ubordeaux.ao.mazing.view;

import fr.ubordeaux.ao.mazing.controller.MazingController;
import fr.ubordeaux.ao.mazing.model.MazingModel;
import fr.ubordeaux.ao.mazing.view.api.IBoardGame;
import fr.ubordeaux.ao.mazing.view.api.Window;

public class MazingView {

	private Window window;

	public MazingView() {
		window = new Window();
	}

	public void refresh(MazingModel model) {
		System.out.println("Window method 'refresh'");
		IBoardGame boardGame = window.getBoardGame();

		float speedX = model.getPlayerSpeedX();
		float speedY = model.getPlayerSpeedY();
		float speedZ = model.getPlayerSpeedZ();
		boardGame.setPlayerSpeed(speedX, speedY, speedZ);
		if (Math.sqrt(speedX * speedX + speedY * speedY + speedZ * speedZ) > 0.01f) {
			boardGame.changePlayerMode("WALK");
		}

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
}
