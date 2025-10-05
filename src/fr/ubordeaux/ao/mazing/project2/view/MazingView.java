package fr.ubordeaux.ao.mazing.project2.view;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Character;
import fr.ubordeaux.ao.mazing.api.IBoardGame;
import fr.ubordeaux.ao.mazing.api.Spider;
import fr.ubordeaux.ao.mazing.api.Window;
import fr.ubordeaux.ao.mazing.project2.controller.MazingController;
import fr.ubordeaux.ao.mazing.project2.model.Cell;
import fr.ubordeaux.ao.mazing.project2.model.Direction;
import fr.ubordeaux.ao.mazing.project2.model.Maze;
import fr.ubordeaux.ao.mazing.project2.model.MazingModel;
import fr.ubordeaux.ao.mazing.project2.model.Point;
import fr.ubordeaux.ao.mazing.project2.model.Mode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MazingView {

	private final Window window;
	private final IBoardGame boardGame;
	private ViewListener viewListener;

	private final Set<Integer> eastBarrier = new HashSet<>(Arrays.asList(111, 175));
	private final Set<Integer> southBarrier = new HashSet<>(Arrays.asList(109, 173));
	private final Set<Integer> westBarrier = new HashSet<>(Arrays.asList(108, 172));
	private final Set<Integer> northBarrier = new HashSet<>(Arrays.asList(110, 174));
	private CharacterView crusader;
	private final CharacterView[] spiders = new CharacterView[3];
	private final Map<String, CharacterView> characters = new HashMap<>();

	public MazingView(ViewListener viewListener) {
		window = new Window();
		boardGame = window.getBoardGame();
		this.viewListener = viewListener;
		init();
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

		// Listener du model
		model.setListener((event) -> {

			System.err.printf("View receive notification: %s\n", event.type);

			switch (event.type) {

				case PLAYER_POSITIONZ -> {
					Float pos = (Float) event.value;
					boardGame.updatePlayerPosition(boardGame.getPlayerX(), boardGame.getPlayerY(), pos);
				}

				case PLAYER_SPEED -> {
					Point speed = (Point) event.value;
					boardGame.updatePlayerSpeed(
							(float) speed.getX(),
							(float) speed.getY(),
							(float) speed.getZ());
				}

				case CHANGE_MODE -> {
					String attribute = (String) event.attribute;
					Mode value = (Mode) event.value;
					CharacterView character = characters.get(attribute);
					if (character != null) {
						character.updateMode(value.name());
					}
				}

				case WALLS -> {
					int[][][] walls = (int[][][]) event.value;
					boardGame.addToSceneMap(walls);
				}

				case MAZE -> {
					Maze maze = (Maze) event.value;
					boardGame.clearMap();
					generateMazeViewFromMazeModel(maze, (int) boardGame.getPlayerZ());
				}

				default -> {
					throw new UnsupportedOperationException("Not supported yet.");
				}
			}
		});
	}

	private void init() {

		Character c = new Crusader();
		c.setPosition(1, 1, 0);
		boardGame.assignPlayer(c);
		c.setTickAnimationTrigger(() -> {
			System.err.println("tickAnimationTrigger");
		});
		c.setBeginAnimationTrigger(() -> {
			System.err.println("beginAnimationTrigger");
		});
		c.setEndAnimationTrigger(() -> {
			System.err.println("endAnimationTrigger");
		});
		c.setLoopAnimationTrigger(() -> {
			System.err.println("loopAnimationTrigger");
		});

		crusader = new CharacterView("crusader", c, boardGame, viewListener,
				eastBarrier, westBarrier, northBarrier, southBarrier);

		characters.put(crusader.toString(), crusader);

		for (int i = 0; i < 3; i++) {
			Spider spider = new Spider();
			spider.setPosition(i + 1, 0, 0);
			spider.setSpeed(0.01f, 0, 0);

			spiders[i] = new CharacterView(String.format("spider%d", i), spider, boardGame, viewListener,
					eastBarrier, westBarrier, northBarrier, southBarrier);
			characters.put(spiders[i].toString(), spiders[i]);
		}

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

	public void generateMazeViewFromMazeModel(Maze maze, int level) {
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

	public CharacterView[] getSpiders() {
		return spiders;
	}

}
