package fr.ubordeaux.ao.project1.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fr.ubordeaux.ao.mazing.api.CharacterMode;
import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.ICharacter;
import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.Spider;
import fr.ubordeaux.ao.project1.model.Cell;
import fr.ubordeaux.ao.project1.model.MazingModel;
import fr.ubordeaux.ao.project1.model.ModelListener;

public class MazingView <E extends Enum<E>> {

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
	private static final int CELL_WIDTH = 3;
	private static final int CELL_HEIGHT = 3;
	private static final int CELL_DEPTH = 1;

	private IWindowGame window;
	private Map<UUID, ICharacter<?>> characterById;
	private MazingModel model;
	private int originX;
	private int originY;
	private AnimationListener animationListener;
	private static int SCROLL_SPEED = 1;

	public MazingView(IWindowGame window, MazingModel model) {
		this.window = window;
		this.model = model;
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
					character.setDirection(direction);
				}
			}

			@Override
			public void onMazeChanged(Cell[][][] cells) {
				System.out.println(cells);
				window.clear();
				generateMaze(cells);
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

	private void generateMaze(Cell[][][] cells) {
		int depth = CELL_DEPTH * cells.length; // z dimension
		int height = CELL_HEIGHT * cells[0].length; // y dimension
		int width = CELL_WIDTH * cells[0][0].length; // x dimension

		int[][][] corners = new int[depth][height][width];
		int[][][] walls = new int[depth][height][width];
		int[][][] grid1 = new int[depth][height][width];
		int[][][] grid2 = new int[depth][height][width];
		int[][][] grid3 = new int[depth][height][width];
		int[][][] grid4 = new int[depth][height][width];

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
								corners[z][y][x] = NONE;
								walls[z][y][x] = NONE;
								grid1[z][y][x] = NONE;
								grid2[z][y][x] = NONE;
								grid3[z][y][x] = NONE;
								grid4[z][y][x] = NONE;
							}
						}
					}

					handleCorners(corners, ix, iy, xStart, xEnd, yStart, yEnd, zStart,
							cells[0][0].length - 1, cells[0].length - 1);

					handleBorders(walls, ix, iy, xStart, xEnd, yStart, yEnd, zStart, zEnd,
							cells[0][0].length - 1, cells[0].length - 1);

					// --- Murs internes selon les chemins de la cellule ---
					if (ix != xStart && !cell.isPath(fr.ubordeaux.ao.project1.model.Direction.WEST)) {
						for (int z = zStart; z < zEnd; z++) {
							for (int y = yStart; y < yEnd; y++) {
								grid1[z][y][xStart] = WALL_HALF_WEST;
							}
						}
					}
					if (iy != yStart && !cell.isPath(fr.ubordeaux.ao.project1.model.Direction.NORTH)) {
						for (int z = zStart; z < zEnd; z++) {
							for (int x = xStart; x < xEnd; x++) {
								grid3[z][yStart][x] = WALL_HALF_NORTH;
							}
						}
					}
				}
			}
		}

		window.add(corners);
		window.add(walls);
		window.add(grid1);
		window.add(grid2);
		window.add(grid3);
		window.add(grid4);
	}

	private boolean handleCorners(int[][][] walls, int ix, int iy,
			int xStart, int xEnd,
			int yStart, int yEnd,
			int zStart,
			int maxX, int maxY) {
		// --- NW corner ---
		if (ix == 0 && iy == 0) {
			walls[zStart][yStart][xStart] = WALL_NW_CORNER;
			for (int y = yStart + 1; y < yEnd; y++)
				walls[zStart][y][xStart] = WALL_WEST;
			for (int x = xStart + 1; x < xEnd; x++)
				walls[zStart][yStart][x] = WALL_NORTH;
			return true;
		}

		// --- NE corner ---
		if (ix == maxX && iy == 0) {
			walls[zStart][yStart][xEnd - 1] = WALL_NE_CORNER;
			for (int y = yStart + 1; y < yEnd; y++)
				walls[zStart][y][xEnd - 1] = WALL_EAST;
			for (int x = xStart; x < xEnd - 1; x++)
				walls[zStart][yStart][x] = WALL_NORTH;
			return true;
		}

		// --- SW corner ---
		if (ix == 0 && iy == maxY) {
			walls[zStart][yEnd - 1][xStart] = WALL_SW_CORNER;
			for (int y = yStart; y < yEnd - 1; y++)
				walls[zStart][y][xStart] = WALL_WEST;
			for (int x = xStart + 1; x < xEnd; x++)
				walls[zStart][yEnd - 1][x] = WALL_SOUTH;
			return true;
		}

		// --- SE corner ---
		if (ix == maxX && iy == maxY) {
			walls[zStart][yEnd - 1][xEnd - 1] = WALL_SE_CORNER;
			for (int y = yStart; y < yEnd - 1; y++)
				walls[zStart][y][xEnd - 1] = WALL_EAST;
			for (int x = xStart; x < xEnd - 1; x++)
				walls[zStart][yEnd - 1][x] = WALL_SOUTH;
			return true;
		}

		return false; // pas un coin
	}

private boolean handleBorders(int[][][] walls, int ix, int iy,
		int xStart, int xEnd,
		int yStart, int yEnd,
		int zStart, int zEnd,
		int maxX, int maxY) {

	// --- Bord Ouest ---
	if (ix == 0 && iy > 0 && iy < maxY) { // pas sur un coin
		for (int z = zStart; z < zEnd; z++)
			for (int y = yStart; y < yEnd; y++)
				walls[z][y][xStart] = WALL_WEST;
		return true;
	}

	// --- Bord Est ---
	if (ix == maxX && iy > 0 && iy < maxY) { // pas sur un coin
		for (int z = zStart; z < zEnd; z++)
			for (int y = yStart; y < yEnd; y++)
				walls[z][y][xEnd - 1] = WALL_EAST;
		return true;
	}

	// --- Bord Nord ---
	if (iy == 0 && ix > 0 && ix < maxX) { // pas sur un coin
		for (int z = zStart; z < zEnd; z++)
			for (int x = xStart; x < xEnd; x++)
				walls[z][yStart][x] = WALL_NORTH;
		return true;
	}

	// --- Bord Sud ---
	if (iy == maxY && ix > 0 && ix < maxX) { // pas sur un coin
		for (int z = zStart; z < zEnd; z++)
			for (int x = xStart; x < xEnd; x++)
				walls[z][yEnd - 1][x] = WALL_SOUTH;
		return true;
	}

	return false;
}

private void init() {
		window.setTitle("Mon jeu à moi");
		window.setSize(1800, 1169);
		window.setResizable(true);
		// window.setBackground(Color.WHITE);
		window.setTileSize(130, 120);

		window.fillTilesBackground(12, -240, -240, 480, 480);
		// window.fillCheckerboardBackground(-12, -12, 24, 24);
		window.setFPS(40);

		window.setVisible(true);

	}

	private void onMid(ICharacter<?> e) {
		// TODO Auto-generated method stub
	}

	private void onEnd(ICharacter<?> e) {
		// TODO Auto-generated method stub
	}

	private void onBegin(ICharacter<?> e) {
		// TODO Auto-generated method stub
	}

	// La vue lit la position dans le modèle et se met à jour
	private void onTick(UUID id, ICharacter<?> e) {
		//System.err.printf("onTick %s:%s\n", id, e);
		if (animationListener != null) {
			animationListener.updatePosition(e);
			//animationListener.checkCollision(e);
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
		// crusader.setScale(2.8f);
		// crusader.setOffset(0, 6);
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
			// crusader.setScale(2.8f);
			// crusader.setOffset(0, 6);
			window.add(spider);
		}
	}

}
