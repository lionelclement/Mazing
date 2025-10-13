package fr.ubordeaux.ao.project1.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fr.ubordeaux.ao.mazing.api.CharacterMode;
import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.ICharacter;
import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.Spider;
import fr.ubordeaux.ao.project1.gameloop.AnimationListener;
import fr.ubordeaux.ao.project1.model.Cell;
import fr.ubordeaux.ao.project1.model.Direction;
import fr.ubordeaux.ao.project1.model.Maze;
import fr.ubordeaux.ao.project1.model.MazingModel;
import fr.ubordeaux.ao.project1.model.ModelListener;

public class MazingView {

	private static final int WALL_WEST = 108;
	private static final int WALL_SOUTH = 109;
	private static final int WALL_NORTH = 110;
	private static final int WALL_EAST = 111;

	private static final int WALL_HALF_WEST = 172;
	private static final int WALL_HALF_NORTH = 174;

	private static final int WALL_NW_CORNER = 138;
	private static final int WALL_NE_CORNER = 139;
	private static final int WALL_SW_CORNER = 136;
	private static final int WALL_SE_CORNER = 137;

	private static final int NONE = -1;
	public static final int CELL_WIDTH = 2;
	public static final int CELL_HEIGHT = 2;
	public static final int CELL_DEPTH = 1;

	private static final int GROUND_1 = 72;
	private static final int GROUND_2 = 12;

	private static final int COLUMN = 400;

	private IWindowGame window;
	private Map<UUID, ICharacter<?>> characterById;
	private int originX;
	private int originY;
	private AnimationListener animationListener;
	private static int SCROLL_SPEED = 1;

	public MazingView(IWindowGame window) {
		this.window = window;
		characterById = new HashMap<>();
		init();
	}

	// Le game loop s'abonne ici
	public void attachAnimationListener(AnimationListener listener) {
		this.animationListener = listener;
	}

	// La vue écoute le modèle
	public void attachModelListener(MazingModel model) {
		model.attachModelListener(new ModelListener() {
			@Override
			public void onModeChanged(UUID id, CharacterMode mode) {
				ICharacter<?> crusader = characterById.get(id);
				if (crusader != null) {
					crusader.setMode(mode.name());
				}
			}

			@Override
			public void onDirectionChanged(UUID id, Direction direction) {
				ICharacter<?> character = characterById.get(id);
				if (character != null) {
					character.setDirection(direction.name());
				}
			}

			@Override
			public void onMazeChanged(Maze maze) {
				window.clear();
				generateMaze(maze);
			}

			@Override
			public void updatePosition(UUID id, float x, float y, float z) {
				ICharacter<?> character = characterById.get(id);
				if (character != null) {
					character.setPosition(x, y, z);
				}

			}

		});
	}

	private void generateMaze(Maze maze) {

		Cell begin = maze.getBegin();
		Cell end = maze.getEnd();

		window.add(519, begin.getX() * CELL_WIDTH + CELL_WIDTH / 2, begin.getY() * CELL_HEIGHT + CELL_HEIGHT / 2, begin.getZ() * CELL_DEPTH + CELL_DEPTH / 2);
		//window.add(519, end.getX()+1, end.getY()+1, end.getZ());

		Cell [][][] cells = maze.getGrid();
		int depth = CELL_DEPTH * cells.length; // z dimension
		int height = CELL_HEIGHT * cells[0].length; // y dimension
		int width = CELL_WIDTH * cells[0][0].length; // x dimension

		int[][][] grounds = new int[depth][height][width];
		int[][][] corners = new int[depth][height][width];
		int[][][] walls = new int[depth][height][width];
		int[][][] walls_west = new int[depth][height][width];
		int[][][] grid2 = new int[depth][height][width];
		int[][][] walls_north = new int[depth][height][width];
		int[][][] grid4 = new int[depth][height][width];
		int[][][] columns = new int[depth][height][width];

		// On parcourt chaque cellule
		for (int iz = 0; iz < cells.length; iz++) {
			for (int iy = 0; iy < cells[0].length; iy++) {
				for (int ix = 0; ix < cells[0][0].length; ix++) {

					Cell cell = cells[iz][iy][ix];

					// Calcul des bornes de la cellule dans la grille
					int zStart = iz * CELL_DEPTH;
					int zEnd = zStart + CELL_DEPTH;
					int yStart = iy * CELL_HEIGHT;
					int yEnd = yStart + CELL_HEIGHT;
					int xStart = ix * CELL_WIDTH;
					int xEnd = xStart + CELL_WIDTH;

					// Remplir toutes les cellules avec NONE
					for (int z = zStart; z < zEnd; z++) {
						for (int y = yStart; y < yEnd; y++) {
							for (int x = xStart; x < xEnd; x++) {
								//if (z == 0) {
									grounds[z][y][x] = NONE;
								//}
								corners[z][y][x] = NONE;
								walls[z][y][x] = NONE;
								walls_west[z][y][x] = NONE;
								grid2[z][y][x] = NONE;
								walls_north[z][y][x] = NONE;
								grid4[z][y][x] = NONE;
								columns[z][y][x] = NONE;
							}
						}
					}

					handleGrounds(grounds, ix, iy, iz, xStart, xEnd, yStart, yEnd, zStart, zEnd,
							cells[0][0].length - 1, cells[0].length - 1, cells.length - 1);

					handleCorners(corners, ix, iy, iz, xStart, xEnd, yStart, yEnd, zStart, zEnd,
							cells[0][0].length - 1, cells[0].length - 1, cells.length - 1);

					handleBorders(walls, ix, iy, iz, xStart, xEnd, yStart, yEnd, zStart, zEnd,
							cells[0][0].length - 1, cells[0].length - 1, cells.length - 1);

					// --- Murs internes selon les chemins de la cellule ---
					if (ix != xStart && !cell.isPath(Direction.WEST)) {
						for (int z = zStart; z < zEnd; z++) {
							for (int y = yStart; y < yEnd; y++) {
								walls_west[z][y][xStart] = WALL_HALF_WEST;
							}
							columns[0][2][2] = COLUMN;
						}
					}
					if (iy != yStart && !cell.isPath(Direction.NORTH)) {
						for (int z = zStart; z < zEnd; z++) {
							for (int x = xStart; x < xEnd; x++) {
								walls_north[z][yStart][x] = WALL_HALF_NORTH;
							}
						}
					}
				}
			}
		}

		window.add(grounds);
		window.add(corners);
		window.add(walls);
		window.add(walls_west);
		window.add(grid2);
		window.add(walls_north);
		window.add(grid4);
		window.add(columns);
	}

	private void handleGrounds(int[][][] grounds, int ix, int iy, int iz,
			int xStart, int xEnd,
			int yStart, int yEnd,
			int zStart, int zEnd,
			int maxX, int maxY, int maxZ) {
		for (int z = zStart; z < zEnd; z++) {
			for (int y = yStart; y < yEnd; y++) {
				for (int x = xStart; x < xEnd; x++) {
					if ((ix + iy) % 2 == 0)
					grounds[z][y][x] = GROUND_1;
					else
					grounds[z][y][x] = GROUND_1;
				}
			}
		}
	}

	private void handleCorners(int[][][] walls, int ix, int iy, int iz,
			int xStart, int xEnd,
			int yStart, int yEnd,
			int zStart, int zEnd,
			int maxX, int maxY, int maxZ) {
		// --- NW corner ---
		if (ix == 0 && iy == 0) {
			for (int z = zStart; z < zEnd; z++) {
				walls[z][yStart][xStart] = WALL_NW_CORNER;
				for (int y = yStart + 1; y < yEnd; y++)
					walls[z][y][xStart] = WALL_WEST;
				for (int x = xStart + 1; x < xEnd; x++)
					walls[z][yStart][x] = WALL_NORTH;
			}
			return;
		}

		// --- NE corner ---
		if (ix == maxX && iy == 0) {
			for (int z = zStart; z < zEnd; z++) {
				walls[z][yStart][xEnd - 1] = WALL_NE_CORNER;
				for (int y = yStart + 1; y < yEnd; y++)
					walls[z][y][xEnd - 1] = WALL_EAST;
				for (int x = xStart; x < xEnd - 1; x++)
					walls[z][yStart][x] = WALL_NORTH;
			}
			return;
		}

		// --- SW corner ---
		if (ix == 0 && iy == maxY) {
			for (int z = zStart; z < zEnd; z++) {
				walls[z][yEnd - 1][xStart] = WALL_SW_CORNER;
				for (int y = yStart; y < yEnd - 1; y++)
					walls[z][y][xStart] = WALL_WEST;
				for (int x = xStart + 1; x < xEnd; x++)
					walls[z][yEnd - 1][x] = WALL_SOUTH;
			}
			return;
		}

		// --- SE corner ---
		if (ix == maxX && iy == maxY) {
			for (int z = zStart; z < zEnd; z++) {
				walls[z][yEnd - 1][xEnd - 1] = WALL_SE_CORNER;
				for (int y = yStart; y < yEnd - 1; y++)
					walls[z][y][xEnd - 1] = WALL_EAST;
				for (int x = xStart; x < xEnd - 1; x++)
					walls[z][yEnd - 1][x] = WALL_SOUTH;
			}
			return;
		}

	}

	private void handleBorders(int[][][] walls, int ix, int iy, int iz,
			int xStart, int xEnd,
			int yStart, int yEnd,
			int zStart, int zEnd,
			int maxX, int maxY, int maxZ) {

		// --- Bord Ouest ---
		if (ix == 0 && iy > 0 && iy < maxY) { // pas sur un coin
			for (int z = zStart; z < zEnd; z++)
				for (int y = yStart; y < yEnd; y++)
					walls[z][y][xStart] = WALL_WEST;
			return;
		}

		// --- Bord Est ---
		if (ix == maxX && iy > 0 && iy < maxY) { // pas sur un coin
			for (int z = zStart; z < zEnd; z++)
				for (int y = yStart; y < yEnd; y++)
					walls[z][y][xEnd - 1] = WALL_EAST;
			return;
		}

		// --- Bord Nord ---
		if (iy == 0 && ix > 0 && ix < maxX) { // pas sur un coin
			for (int z = zStart; z < zEnd; z++)
				for (int x = xStart; x < xEnd; x++)
					walls[z][yStart][x] = WALL_NORTH;
			return;
		}

		// --- Bord Sud ---
		if (iy == maxY && ix > 0 && ix < maxX) { // pas sur un coin
			for (int z = zStart; z < zEnd; z++)
				for (int x = xStart; x < xEnd; x++)
					walls[z][yEnd - 1][x] = WALL_SOUTH;
			return;
		}
	}

	private void init() {
		window.setTitle("Mon jeu à moi");
		window.setSize(1800, 1169);
		window.setResizable(true);
		// window.setBackground(Color.WHITE);
		window.setTileSize(130, 120);

		//window.drawTile(Color.BLUE, 0, 0, 0);
		//int[][][] grid = {{{12}}};
		//window.add(grid);

		//window.fillTilesBackground(12, -240, -240, 480, 480);
		// window.fillCheckerboardBackground(-12, -12, 24, 24);
		window.setFPS(40);

		window.setVisible(true);

	}

	private void onMid(ICharacter<?> e) {
		// TODO Auto-generated method stub
	}

	private void onEnd(ICharacter<?> e) {
		if (animationListener != null) {
			animationListener.updateMode(e);
		}
	}

	private void onBegin(ICharacter<?> e) {
	}

	// La vue lit la position dans le modèle et se met à jour
	private void onTick(UUID id, ICharacter<?> e) {
		// System.err.printf("onTick %s:%s\n", id, e);
		if (animationListener != null) {
			animationListener.updatePosition(e);
			animationListener.checkCollision(e);
		}
		// ICharacter<?> crusader = characterById.get(id);
		// if (crusader != null)
		// crusader.setPosition(model.getX(id), model.getY(id), model.getZ(id));
	}

	// Déplacements du plateau
	public void scrollRight() {
		originX += SCROLL_SPEED;
		window.scroll(originX, originY);
	}

	public void scrollLeft() {
		originX -= SCROLL_SPEED;
		window.scroll(originX, originY);
	}

	public void scrollUp() {
		originY -= SCROLL_SPEED;
		window.scroll(originX, originY);
	}

	public void scrollDown() {
		originY += SCROLL_SPEED;
		window.scroll(originX, originY);
	}

	public void createCrusader(UUID id) {
		ICharacter<?> crusader = new Crusader(id);
		characterById.put(id, crusader);
		crusader.setTickAnimationTrigger(e -> onTick(id, e));
		crusader.setBeginAnimationTrigger(e -> onBegin(e));
		crusader.setEndAnimationTrigger(e -> onEnd(e));
		crusader.setMidAnimationTrigger(e -> onMid(e));
		window.add(crusader);
	}

	public void createSpiders(Set<UUID> spiderIds) {
		System.err.println(spiderIds);
		for (UUID id : spiderIds) {
			ICharacter<?> spider = new Spider(id);
			characterById.put(id, spider);
			spider.setTickAnimationTrigger(e -> onTick(id, e));
			spider.setBeginAnimationTrigger(e -> onBegin(e));
			spider.setEndAnimationTrigger(e -> onEnd(e));
			spider.setMidAnimationTrigger(e -> onMid(e));
			window.add(spider);
		}
	}

}
