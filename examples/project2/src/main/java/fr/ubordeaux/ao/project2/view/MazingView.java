package fr.ubordeaux.ao.project2.view;

import fr.ubordeaux.ao.mazing.api.view.GameWindow;
import fr.ubordeaux.ao.mazing.api.view.board.BoardGameComponent;
import fr.ubordeaux.ao.mazing.api.view.entity.EntityRenderer;
import fr.ubordeaux.ao.mazing.impl.model.board.BoardGame;
import fr.ubordeaux.ao.mazing.impl.model.entity.Crusader;
import fr.ubordeaux.ao.mazing.api.model.entity.Entity;
import fr.ubordeaux.ao.mazing.api.model.board.IBoardGame;
import fr.ubordeaux.ao.mazing.impl.model.entity.Spider;
import fr.ubordeaux.ao.mazing.impl.view.entity.CrusaderRenderer;
import fr.ubordeaux.ao.mazing.impl.view.entity.SpiderRenderer;
import fr.ubordeaux.ao.project2.controller.MazingController;
import fr.ubordeaux.ao.project2.model.Cell;
import fr.ubordeaux.ao.project2.model.Direction;
import fr.ubordeaux.ao.project2.model.Maze;
import fr.ubordeaux.ao.project2.model.MazingModel;
import fr.ubordeaux.ao.project2.model.Point;
import fr.ubordeaux.ao.project2.model.Mode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MazingView {

	// Constantes de tuiles (Tile IDs)

	private static final int TILE_DIRT = 12;
	private static final int TILE_FLOOR = 72;

	private static final int TILE_WALL_WEST = 108;
	private static final int TILE_WALL_SOUTH = 109;
	private static final int TILE_WALL_NORTH = 110;
	private static final int TILE_WALL_EAST = 111;

	private static final int TILE_HALF_WEST = 172;
	private static final int TILE_HALF_SOUTH = 173;
	private static final int TILE_HALF_NORTH = 174;
	private static final int TILE_HALF_EAST = 175;

	private static final int TILE_TRAPDOOR = 76;
	private static final int TILE_STAIRS_UP = 308;
	private static final int TILE_FLOOR_LEVEL = 24;

	private final GameWindow window;
	private final IBoardGame boardGame;
	private final BoardGameComponent boardGameComponent;
	private ViewListener viewListener;

	private final Set<Integer> eastBarrier = new HashSet<>(Arrays.asList(TILE_WALL_EAST, TILE_HALF_EAST));
	private final Set<Integer> southBarrier = new HashSet<>(Arrays.asList(TILE_WALL_SOUTH, TILE_HALF_SOUTH));
	private final Set<Integer> westBarrier = new HashSet<>(Arrays.asList(TILE_WALL_WEST, TILE_HALF_WEST));
	private final Set<Integer> northBarrier = new HashSet<>(Arrays.asList(TILE_WALL_NORTH, TILE_HALF_NORTH));
	private CharacterView crusader;
	private final CharacterView[] spiders = new CharacterView[3];
	private final Map<String, CharacterView> characters = new HashMap<>();

	public MazingView(ViewListener viewListener) {
		boardGame = new BoardGame(GameWindow.WIDTH, GameWindow.HEIGHT);
		boardGameComponent = new BoardGameComponent(boardGame);
		window = new GameWindow(boardGameComponent);
		this.viewListener = viewListener;
		init();
	}

	public void addKeyListener(MazingController controller) {
		window.addKeyListener(controller);
	}

	public void setVisible(boolean b) {
		window.setVisible(b);
	}

	public BoardGameComponent getBoardGame() {
		return window.getBoardGame();
	}

	public void bindToModel(MazingModel model) {

		// Listener du fr.ubordeaux.ao.project1.model
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
					boardGameComponent.addToSceneMap(walls);
				}

				case MAZE -> {
					Maze maze = (Maze) event.value;
					boardGameComponent.clearMap();
					generateMazeViewFromMazeModel(maze, (int) boardGame.getPlayerZ());
				}

				default -> {
					throw new UnsupportedOperationException("Not supported yet.");
				}
			}
		});
	}

	private void init() {

		boardGameComponent.fillTilesBackground(TILE_DIRT, 1.0f);
		Entity e = new Crusader();
		e.setPosition(1, 1, 0);
		boardGame.assignPlayer(e);
		EntityRenderer r = new CrusaderRenderer(e);
		r.setTickAnimationCallback(() -> {
			System.err.println("tickAnimationTrigger");
		});
		r.setBeginAnimationCallback(() -> {
			System.err.println("beginAnimationTrigger");
		});
		r.setEndAnimationCallback(() -> {
			System.err.println("endAnimationTrigger");
		});
		r.setLoopAnimationCallback(() -> {
			System.err.println("loopAnimationTrigger");
		});

		crusader = new CharacterView("crusader", r, boardGameComponent, viewListener,
				eastBarrier, westBarrier, northBarrier, southBarrier);

		characters.put(crusader.toString(), crusader);

		for (int i = 0; i < 3; i++) {
			Spider spider = new Spider();
			spider.setPosition(i + 1, 0, 0);
			spider.setSpeed(0.01f, 0, 0);
			SpiderRenderer spiderRenderer = new SpiderRenderer(spider);

			spiders[i] = new CharacterView(String.format("spider%d", i), spiderRenderer, boardGameComponent, viewListener,
					eastBarrier, westBarrier, northBarrier, southBarrier);
			characters.put(spiders[i].toString(), spiders[i]);
		}

	}

	private void addCell(int tileId, int baseX, int baseY, int baseZ, float alpha) {
		boardGameComponent.addToSceneMap(tileId, baseX, baseY, baseZ, alpha);
	}

	private void addLineCell(int tileId, int size, int baseX, int baseY, int baseZ, Direction dir, float alpha) {
		switch (dir) {
			case WEST -> {
				for (int k = 0; k < size; k++) {
					boardGameComponent.addToSceneMap(tileId, baseX, baseY + k, baseZ, alpha);
				}
			}

			case EAST -> {
				for (int k = 0; k < size; k++) {
					boardGameComponent.addToSceneMap(tileId, baseX + 2, baseY + k, baseZ, alpha);
				}
			}

			case NORTH -> {
				for (int k = 0; k < size; k++) {
					boardGameComponent.addToSceneMap(tileId, baseX + k, baseY, baseZ, alpha);
				}
			}

			case SOUTH -> {
				for (int k = 0; k < size; k++) {
					boardGameComponent.addToSceneMap(tileId, baseX + k, baseY + 2, baseZ, alpha);
				}
			}

			case UP -> boardGameComponent.addToSceneMap(tileId, baseX + 1, baseY + 1, baseZ, alpha);

			case DOWN -> boardGameComponent.addToSceneMap(tileId, baseX + 1, baseY + 1, baseZ, alpha);
		}
	}

	public void generateMazeViewFromMazeModel(Maze maze, int level) {
		int width = maze.getWidth();
		int height = maze.getHeight();
		int depth = maze.getDepth();

		boardGameComponent.clearMap();

		for (int x = 0; x < maze.getWidth() * 3; x++) {
			for (int y = 0; y < maze.getHeight() * 3; y++) {
				boardGameComponent.addToSceneMap(TILE_FLOOR, x, y, 0, 1.0f);
			}
		}

		for (int z = 0; z <= level + 1 && z < depth; z++) {
			float alpha;
			if (z <= level) {
				alpha = 1;
			} else {
				alpha = 0f;
			}
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Cell cell = maze.getCell(x, y, z);
					// On ajoute des murs à l'exterieur
					for (Direction d : Direction.values()) {
						if (cell.isWall(d)) {
							if (d == Direction.WEST) {
								addLineCell(TILE_WALL_WEST, 3, x * 3, y * 3, z, d, alpha);
							}
							if (d == Direction.EAST) {
								addLineCell(TILE_WALL_EAST, 3, x * 3, y * 3, z, d, alpha);
							}
							if (d == Direction.NORTH) {
								addLineCell(TILE_WALL_NORTH, 3, x * 3, y * 3, z, d, alpha);
							}
							if (d == Direction.SOUTH) {
								addLineCell(TILE_WALL_SOUTH, 3, x * 3, y * 3, z, d, alpha);
							}
						}
						if (!cell.isPath(d)) {
							if (x > 0 && d == Direction.WEST) {
								addLineCell(TILE_HALF_WEST, 3, x * 3, y * 3, z, d, alpha);
							}
							if (x < width - 1 && d == Direction.EAST) {
								addLineCell(TILE_HALF_EAST, 3, x * 3, y * 3, z, d, alpha);
							}
							if (y > 0 && d == Direction.NORTH) {
								addLineCell(TILE_HALF_NORTH, 3, x * 3, y * 3, z, d, alpha);
							}
							if (y < height - 1 && d == Direction.SOUTH) {
								addLineCell(TILE_HALF_SOUTH, 3, x * 3, y * 3, z, d, alpha);
							}
							if (z > 0 && d == Direction.DOWN) {
								addLineCell(TILE_FLOOR_LEVEL, 3, x * 3, y * 3, z, Direction.NORTH, alpha);
								addLineCell(TILE_FLOOR_LEVEL, 3, x * 3, y * 3 + 1, z, Direction.NORTH, alpha);
								addLineCell(TILE_FLOOR_LEVEL, 3, x * 3, y * 3 + 2, z, Direction.NORTH, alpha);
							}
						} else {
							if (z > 0 && d == Direction.DOWN) {
								addLineCell(TILE_FLOOR_LEVEL, 3, x * 3, y * 3, z, Direction.NORTH, alpha);
								addCell(TILE_FLOOR_LEVEL, x * 3, y * 3 + 1, z, alpha);
								addCell(TILE_TRAPDOOR, x * 3 + 1, y * 3 + 1, z, alpha);
								addCell(TILE_FLOOR_LEVEL, x * 3 + 2, y * 3 + 1, z, alpha);
								addLineCell(TILE_FLOOR_LEVEL, 3, x * 3, y * 3 + 2, z, Direction.NORTH, alpha);
							}
							if (z < depth && d == Direction.UP) {
								addLineCell(TILE_STAIRS_UP, 3, x * 3, y * 3, z, d, alpha);
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
