package fr.ubordeaux.ao.mazing.project2.view;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.IBoardGame;
import fr.ubordeaux.ao.mazing.api.Window;
import fr.ubordeaux.ao.mazing.project2.controller.MazingController;
import fr.ubordeaux.ao.mazing.project2.model.Cell;
import fr.ubordeaux.ao.mazing.project2.model.Direction;
import fr.ubordeaux.ao.mazing.project2.model.Maze;
import fr.ubordeaux.ao.mazing.project2.model.MazingModel;
import fr.ubordeaux.ao.mazing.project2.model.Point;

public class MazingView {

	private final Window window;
	private final IBoardGame boardGame;

	private ViewListener viewListener;
	private boolean gothit;

	public MazingView() {
		window = new Window();
		boardGame = window.getBoardGame();
		init();
	}

	public void setViewListener(ViewListener listener) {
		this.viewListener = listener;
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
		model.setListener((event) -> {
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

				case WALLS -> {
					int[][][] walls = (int[][][]) event.data;
					boardGame.addToSceneMap(walls);
				}

				case MAZE -> {
					Maze maze = (Maze) event.data;
					boardGame.clearMap();
					generateMazeViewFromMazeModel(maze, (int) boardGame.getPlayerZ());
				}

				default -> {
				}
			}
		});
	}

	private void init() {
		Crusader crusader = new Crusader();
		boardGame.assignPlayer(crusader);
		crusader.setTickAnimationTrigger(() -> {
			System.err.println("VIEW -> MODEL");
			// Si on avance, on envoie au modèle "PLAYER_MOVE" avec la position
			if (viewListener != null) {
				viewListener.onViewEvent(
						new ViewEvent(ViewEvent.Type.PLAYER_POSITION,
								new Point(crusader.getX() / 3, crusader.getY() / 3, crusader.getZ())));
			}
		});

		crusader.setBeginAnimationTrigger(() -> {
			// Si on est GOTHIT, ons'arrète
			if (crusader.getCurrentMode().name().equals("GOTHIT")) {
				crusader.setSpeed(0, 0, 0);
				gothit = true;
			}
		});

		crusader.setEndAnimationTrigger(() -> {
			// Si on est GOTHIT, ons'arrète
			if (gothit) {
				gothit = false;
				crusader.setSpeed(0, 0, 0);
				crusader.changeMode("IDLE");
			}
		});

		boardGame.addToSceneMap(crusader);
		boardGame.fillTilesBackground(12, 1.0f);
	}

	private void addCell(int tileId, int baseX, int baseY, int baseZ, float alpha) {
		boardGame.addToSceneMap(tileId, baseX, baseY, baseZ, alpha);
	}

	private void addLineCell(int tileId, int baseX, int baseY, int baseZ, Direction dir, float alpha) {
		switch (dir) {
			case WEST -> {
				for (int k = 0; k < 3; k++) {
					boardGame.addToSceneMap(tileId, baseX, baseY + k, baseZ, alpha);
				}
			}

			case EAST -> {
				for (int k = 0; k < 3; k++) {
					boardGame.addToSceneMap(tileId, baseX + 2, baseY + k, baseZ, alpha);
				}
			}

			case NORTH -> {
				for (int k = 0; k < 3; k++) {
					boardGame.addToSceneMap(tileId, baseX + k, baseY, baseZ, alpha);
				}
			}

			case SOUTH -> {
				for (int k = 0; k < 3; k++) {
					boardGame.addToSceneMap(tileId, baseX + k, baseY + 2, baseZ, alpha);
				}
			}

			case UP -> boardGame.addToSceneMap(tileId, baseX + 1, baseY + 1, baseZ, alpha);

			case DOWN -> boardGame.addToSceneMap(tileId, baseX + 1, baseY + 1, baseZ, alpha);
		}
	}

	private void generateMazeViewFromMazeModel(Maze maze, int level) {
		int width = maze.getWidth();
		int height = maze.getHeight();
		int depth = maze.getDepth();

		boardGame.clearMap();

		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				boardGame.addToSceneMap(72, x, y, 0, 1.0f);
			}
		}

		for (int z = 0; z <= level + 1 && z < depth; z++) {
			float alpha;
			if (z <= level) {
				alpha = 1;
			} else {
				alpha = 0f;
			}
			for (int y = 0; y < width; y++) {
				for (int x = 0; x < height; x++) {
					Cell cell = maze.getCell(x, y, z);
					// On ajoute des murs à l'exterieur
					for (Direction d : Direction.values()) {
						if (cell.isWall(d)) {
							if (d == Direction.WEST) {
								addLineCell(108, x * 3, y * 3, z, d, alpha);
							}
							if (d == Direction.NORTH) {
								addLineCell(110, x * 3, y * 3, z, d, alpha);
							}
						}
						if (!cell.isPath(d)) {
							if (x > 0 && d == Direction.WEST) {
								addLineCell(172, x * 3, y * 3, z, d, alpha);
							}
							if (x < width && d == Direction.EAST) {
								addLineCell(175, x * 3, y * 3, z, d, alpha);
							}
							if (y > 0 && d == Direction.NORTH) {
								addLineCell(174, x * 3, y * 3, z, d, alpha);
							}
							if (y < height && d == Direction.SOUTH) {
								addLineCell(173, x * 3, y * 3, z, d, alpha);
							}
							if (z > 0 && d == Direction.DOWN) {
								addLineCell(24, x * 3, y * 3, z, Direction.NORTH, alpha);
								addLineCell(24, x * 3, y * 3 + 1, z, Direction.NORTH, alpha);
								addLineCell(24, x * 3, y * 3 + 2, z, Direction.NORTH, alpha);
							}
						} else {
							if (z > 0 && d == Direction.DOWN) {
								addLineCell(24, x * 3, y * 3, z, Direction.NORTH, alpha);
								addCell(24, x * 3, y * 3 + 1, z, alpha);
								addCell(24, x * 3 + 2, y * 3 + 1, z, alpha);
								addLineCell(24, x * 3, y * 3 + 2, z, Direction.NORTH, alpha);
							}
							if (z < depth && d == Direction.UP) {
								addLineCell(308, x * 3, y * 3, z, d, alpha);
							}
							// if (z < depth && d == Direction.UP) {
							// addWallForCell(20, x * 3, y * 3, z, d);
							// }
						}
					}
				}
			}
		}
	}

}
