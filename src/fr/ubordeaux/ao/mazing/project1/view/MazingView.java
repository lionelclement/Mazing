package fr.ubordeaux.ao.mazing.project1.view;

import java.util.Set;

import fr.ubordeaux.ao.mazing.api.*;
import fr.ubordeaux.ao.mazing.project1.controller.MazingController;
import fr.ubordeaux.ao.mazing.project1.model.MazingModel;
import fr.ubordeaux.ao.mazing.project1.model.Point;

public class MazingView {

	private Window window;
	private IBoardGame boardGame;

	public MazingView() {
		window = new Window();
		boardGame = window.getBoardGame();
		boardGame.fillTilesBackground(72, 1.0f);
		boardGame.addToSceneMap(540, 2, 2, 0, 1.0f);

		Crusader crusader = new Crusader();
		crusader.setTickAnimationTrigger(() -> {

			int x = Math.round(crusader.getX());
			int y = Math.round(crusader.getY());
			int z = Math.round(crusader.getZ());

			System.err.println("tickAnimationTrigger");
			Set<IRenderable3D> set = boardGame.getSceneObjectsFromPoint(x, y, z);
			System.err.println(set);
			if (set == null || set.isEmpty())
				return;
			if (set.contains(540)) {
				boardGame.addToSceneMap(27, (int) crusader.getX(), (int) crusader.getY(), (int) crusader.getZ(), 1);
				crusader.changeMode("ATTACK");
				crusader.setSpeed(0, 0, 0);
				// boardGame.removeMap(540);
				// boardGame.de
			}
		});

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
