package fr.ubordeaux.ao.project1.view;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.IBoardGame;
import fr.ubordeaux.ao.mazing.api.Window;
import fr.ubordeaux.ao.project1.controller.MazingController;
import fr.ubordeaux.ao.project1.model.MazingModel;
import fr.ubordeaux.ao.project1.model.Point;

import java.util.Random;
import java.util.Set;

public class MazingView {

	private Window window;
	private IBoardGame boardGame;
	private boolean flag;

	public MazingView() {
		window = new Window();
		boardGame = window.getBoardGame();
		boardGame.fillTilesBackground(72, 1.0f);
		boardGame.addToSceneMap(540, 2, 2, 0, 1.0f);

		Crusader crusader = new Crusader();
		crusader.setEndAnimationTrigger(() -> {
			boardGame.clearMap();
			crusader.setSpeed(0, 0, 0);
			Random rand = new Random();
			int random = 500 + rand.nextInt(59);
			boardGame.addToSceneMap(random, rand.nextInt(6), rand.nextInt(6), 0, 1.0f);
			flag = false;
			crusader.changeMode("IDLE");
		});

		crusader.setTickAnimationTrigger(() -> {
			if (!flag) {
				int x = Math.round(crusader.getX());
				int y = Math.round(crusader.getY());
				int z = Math.round(crusader.getZ());

				Set<Integer> set = boardGame.getSceneObjectsFromPoint(x, y, z);
				if (set == null || set.isEmpty())
					return;
				if (!set.isEmpty()) {
					flag = true;
					crusader.setSpeed(0, 0, 0);
					crusader.changeMode("ATTACK");
				}
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
				case PLAYER_POS -> {
					Point pos = (Point) event.data;
					boardGame.updatePlayerPosition(
							(float) pos.getX(),
							(float) pos.getY(),
							(float) pos.getZ());
				}

				case PLAYER_SPEED -> {
					Point speed = (Point) event.data;
					boardGame.updatePlayerSpeed(
							(float) speed.getX(),
							(float) speed.getY(),
							(float) speed.getZ());
				}

				case PLAYER_MODE -> {
					String mode = (String) event.data;
					boardGame.updatePlayerMode(mode);
				}
			}
		});
	}

}
