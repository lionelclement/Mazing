package fr.ubordeaux.ao.mazing.view;

import fr.ubordeaux.ao.mazing.controller.MazingController;
import fr.ubordeaux.ao.mazing.model.MazingModel;
import fr.ubordeaux.ao.mazing.view.api.IBoardGame;
import fr.ubordeaux.ao.mazing.view.api.Window;

public class MazingView {

	private Window window;
	private IBoardGame boardGame;

	public MazingView() {
		window = new Window();
		boardGame = window.getBoardGame();
	}

	public void refresh(MazingModel model) {
		System.out.println("Window method 'refresh'");

		// Ajuster la vitesse
		float speedX = model.getPlayerSpeedX();
		float speedY = model.getPlayerSpeedY();
		float speedZ = model.getPlayerSpeedZ();
		boardGame.setPlayerSpeed(speedX, speedY, speedZ);

		// Faire que le personnage marche si la vitesse est suffisante
		if (Math.sqrt(speedX * speedX + speedY * speedY + speedZ * speedZ) > 0.01f) {
			boardGame.changePlayerMode("WALK");
		} else {
			boardGame.changePlayerMode("IDLE");
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
