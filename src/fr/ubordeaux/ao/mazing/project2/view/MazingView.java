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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MazingView {

	private final Window window;
	private final IBoardGame boardGame;

	private ViewListener viewListener;
	private boolean gothit;

	Set<Integer> eastBarrier = new HashSet<>(Arrays.asList(111, 175));
	Set<Integer> southBarrier = new HashSet<>(Arrays.asList(109, 173));

	Set<Integer> westBarrier = new HashSet<>(Arrays.asList(108, 172));
	Set<Integer> northBarrier = new HashSet<>(Arrays.asList(110, 174));

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
				case PLAYER_POSITIONZ -> {
					Float pos = (Float) event.data;
					boardGame.updatePlayerPosition(boardGame.getPlayerX(), boardGame.getPlayerY(), pos);
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
		crusader.setPosition(1, 1, 0);
		boardGame.assignPlayer(crusader);
		crusader.setTickAnimationTrigger(() -> {

			Set<Integer> set = new HashSet<>();

			int cx = (int) crusader.getX();
			int cy = (int) crusader.getY();
			int cz = (int) crusader.getZ();

			// boucle sur x-1 à x+1 et y-1 à y+1
			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					Set<Integer> objects = boardGame.getSceneObjectsFromPoint(cx + dx, cy + dy, cz);
					if (objects != null) {
						set.addAll(objects);
					}
				}
			}
			if (set.contains(308)) {
				viewListener.onViewEvent(
						new ViewEvent(ViewEvent.Type.PLAYER_ON_STEPS, true));
			} else {
				viewListener.onViewEvent(
						new ViewEvent(ViewEvent.Type.PLAYER_ON_STEPS, false));
			}

			if (set.contains(76)) {
				viewListener.onViewEvent(
						new ViewEvent(ViewEvent.Type.PLAYER_ON_TRAPDOOR, true));
			} else {
				viewListener.onViewEvent(
						new ViewEvent(ViewEvent.Type.PLAYER_ON_TRAPDOOR, false));
			}

			set = boardGame.getSceneObjectsFromPoint((int) crusader.getX(),
					(int) crusader.getY(),
					(int) crusader.getZ());
			if (set != null && !set.isEmpty()) {

				// System.err.println(set);

				Set<Integer> copy;

				// South barrier
				copy = new HashSet<>(set);
				copy.retainAll(southBarrier);
				if (!copy.isEmpty()) {
					if (crusader.getSpeedY() > 0 && crusader.getY() % 1 < 0.1f) {
						// replace
						crusader.setPosition(crusader.getX(), crusader.getY() - 0.2f, crusader.getZ());
						// send to Model
						viewListener.onViewEvent(
								new ViewEvent(ViewEvent.Type.PLAYER_GOTHIT, null));
					}
				}

				// North barrier
				copy = new HashSet<>(set);
				copy.retainAll(northBarrier);
				if (!copy.isEmpty()) {
					if (crusader.getSpeedY() < 0 && crusader.getY() % 1 < 0.1f) {
						// replace
						crusader.setPosition(crusader.getX(), crusader.getY() + 0.1f, crusader.getZ());
						// send to Model
						viewListener.onViewEvent(
								new ViewEvent(ViewEvent.Type.PLAYER_GOTHIT, null));
					}
				}

				// East barrier
				copy = new HashSet<>(set);
				copy.retainAll(eastBarrier);
				if (!copy.isEmpty()) {
					if (crusader.getSpeedX() > 0 && crusader.getX() % 1 < 0.1f) {
						// replace
						crusader.setPosition(crusader.getX() - 0.2f, crusader.getY(), crusader.getZ());
						// send to Model
						viewListener.onViewEvent(
								new ViewEvent(ViewEvent.Type.PLAYER_GOTHIT, null));
					}
				}

				// West barrier
				copy = new HashSet<>(set);
				copy.retainAll(westBarrier);
				if (!copy.isEmpty()) {
					if (crusader.getSpeedX() < 0 && crusader.getX() % 1 < 0.1f) {
						// replace
						crusader.setPosition(crusader.getX() + 0.1f, crusader.getY(), crusader.getZ());
						// send to Model
						viewListener.onViewEvent(
								new ViewEvent(ViewEvent.Type.PLAYER_GOTHIT, null));

					}
				}
			}
			// }
		});

		crusader.setBeginAnimationTrigger(() ->

		{
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
							if (d == Direction.EAST) {
								addLineCell(111, x * 3, y * 3, z, d, alpha);
							}
							if (d == Direction.NORTH) {
								addLineCell(110, x * 3, y * 3, z, d, alpha);
							}
							if (d == Direction.SOUTH) {
								addLineCell(109, x * 3, y * 3, z, d, alpha);
							}
						}
						if (!cell.isPath(d)) {
							if (x > 0 && d == Direction.WEST) {
								addLineCell(172, x * 3, y * 3, z, d, alpha);
							}
							if (x < width - 1 && d == Direction.EAST) {
								addLineCell(175, x * 3, y * 3, z, d, alpha);
							}
							if (y > 0 && d == Direction.NORTH) {
								addLineCell(174, x * 3, y * 3, z, d, alpha);
							}
							if (y < height - 1 && d == Direction.SOUTH) {
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
								addCell(76, x * 3 + 1, y * 3 + 1, z, alpha);
								addCell(24, x * 3 + 2, y * 3 + 1, z, alpha);
								addLineCell(24, x * 3, y * 3 + 2, z, Direction.NORTH, alpha);
							}
							if (z < depth && d == Direction.UP) {
								addLineCell(308, x * 3, y * 3, z, d, alpha);
							}
						}
					}
				}
			}
		}
	}

}
