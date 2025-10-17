package fr.ubordeaux.ao.project1.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.JFrame;

import fr.ubordeaux.ao.mazing.api.ICharacterMode;
import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.ICharacter;
import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.Spider;
import fr.ubordeaux.ao.project1.gameloop.AnimationListener;
import fr.ubordeaux.ao.project1.model.Cell;
import fr.ubordeaux.ao.project1.model.Direction3D;
import fr.ubordeaux.ao.project1.model.Maze;
import fr.ubordeaux.ao.project1.model.MazingModel;
import fr.ubordeaux.ao.project1.model.ModelListener;

public class MazingView {

    private static final int NONE = -1;

    // Dimensions
    public static final int CELL_WIDTH = 3;
    public static final int CELL_HEIGHT = 3;
    public static final int CELL_DEPTH = 1;

    // Tiles
    private static final int GROUND_STONE = 72;
    private static final int GROUND_WOOD = 25;
    private static final int GROUND_TRAPDOOR = 20;
    private static final int STAIRS = 308;

    private static final int WALL_WEST = 108, WALL_SOUTH = 109, WALL_NORTH = 110, WALL_EAST = 111;
    private static final int WALL_HALF_WEST = 172, WALL_HALF_NORTH = 174;
    private static final int WALL_NW_CORNER = 138, WALL_NE_CORNER = 139, WALL_SW_CORNER = 136, WALL_SE_CORNER = 137;

    private IWindowGame window;
    private Map<UUID, ICharacter<?>> characterById = new HashMap<>();
    private AnimationListener animationListener;
    private int originX, originY;
    private static final int SCROLL_SPEED = 1;

    public MazingView(IWindowGame window) {
        this.window = window;
        init();
    }

    public void attachAnimationListener(AnimationListener listener) {
        this.animationListener = listener;
    }

    public void attachModelListener(MazingModel model) {
        model.attachModelListener(new ModelListener() {
            @Override
            public void onModeChanged(UUID id, ICharacterMode mode) {
                ICharacter<?> c = characterById.get(id);
                if (c != null)
                    c.setMode(mode);
            }

            @Override
            public void onDirectionChanged(UUID id, Direction direction) {
                ICharacter<?> c = characterById.get(id);
                if (c != null)
                    c.setDirection(direction);
            }

            @Override
            public void onMazeChanged(Maze maze, float level) {
                window.clear();
                drawMaze(maze, level);
            }

            @Override
            public void updatePosition(UUID id, float x, float y, float z) {
                ICharacter<?> c = characterById.get(id);
                if (c != null)
                    c.setPosition(x, y, z);
            }
        });
    }

    private void drawMaze(Maze maze, float level) {
        Cell begin = maze.getBegin();
        if (level == 0)
            window.add(519, begin.getX() + CELL_WIDTH / 2, begin.getY() + CELL_HEIGHT / 2, begin.getZ());

        Cell[][][] cells = maze.getGrid();
        int cellCountZ = cells.length, cellCountY = cells[0].length, cellCountX = cells[0][0].length;
        int depth = CELL_DEPTH * cellCountZ, height = CELL_HEIGHT * cellCountY, width = CELL_WIDTH * cellCountX;

        int[][][] grounds = new int[depth][height][width];
        //int[][][] transparentGrounds = new int[depth][height][width];
        int[][][] corners = new int[depth][height][width];
        int[][][] walls = new int[depth][height][width];
        int[][][] wallsWest = new int[depth][height][width];
        int[][][] wallsNorth = new int[depth][height][width];
        int[][][] stairs = new int[depth][height][width];

        fillVolumeWithNone(grounds, corners, walls, wallsWest, wallsNorth, stairs);

        for (int iz = 0; iz < cellCountZ; iz++) {
            for (int iy = 0; iy < cellCountY; iy++) {
                for (int ix = 0; ix < cellCountX; ix++) {

                    Cell cell = cells[iz][iy][ix];
                    int xStart = ix * CELL_WIDTH, xEnd = xStart + CELL_WIDTH;
                    int yStart = iy * CELL_HEIGHT, yEnd = yStart + CELL_HEIGHT;
                    int zStart = iz * CELL_DEPTH, zEnd = zStart + CELL_DEPTH;

                    int[][][] groundLayer = layerForLevel(iz, level, grounds, null);
                    int[][][] stairLayer = layerForLevel(iz, level, stairs, null);
                    int[][][] cornerLayer = layerForLevel(iz, level, corners, null);
                    int[][][] wallLayer = layerForLevel(iz, level, walls, null);
                    int[][][] wallWestLayer = layerForLevel(iz, level, wallsWest, null);
                    int[][][] wallNorthLayer = layerForLevel(iz, level, wallsNorth, null);

                    if (groundLayer != null)
                        handleGrounds(groundLayer, iz, xStart, xEnd, yStart, yEnd);
                    if (cell.isPath(Direction3D.DOWN) && groundLayer != null)
                        placeTileAtCenter(groundLayer, iz, xStart, xEnd, yStart, yEnd, GROUND_TRAPDOOR);
                    if (cell.isPath(Direction3D.UP) && stairLayer != null)
                        placeTileAtCenter(stairLayer, iz, xStart, xEnd, yStart, yEnd, STAIRS);
                    if (cornerLayer != null)
                        handleCorners(cornerLayer, ix, iy, iz, xStart, xEnd, yStart, yEnd, zStart, zEnd, cellCountX - 1,
                                cellCountY - 1);
                    if (wallLayer != null)
                        handleBorders(wallLayer, ix, iy, iz, xStart, xEnd, yStart, yEnd, zStart, zEnd, cellCountX - 1,
                                cellCountY - 1);

                    // demi-murs
                    if (iy != 0 && !cell.isPath(Direction3D.NORTH) && wallNorthLayer != null)
                        for (int x = xStart; x < xEnd; x++)
                            wallNorthLayer[zStart][yStart][x] = WALL_HALF_NORTH;
                    if (ix != 0 && !cell.isPath(Direction3D.WEST) && wallWestLayer != null)
                        for (int y = yStart; y < yEnd; y++)
                            wallWestLayer[zStart][y][xStart] = WALL_HALF_WEST;
                }
            }
        }

        window.add(grounds);
        //window.add(transparentGrounds, 0.5f);
        window.add(stairs);
        window.add(corners);
        window.add(walls);
        window.add(wallsWest);
        window.add(wallsNorth);
    }

    private void fillVolumeWithNone(int[][][]... volumes) {
        for (int[][][] vol : volumes)
            for (int z = 0; z < vol.length; z++)
                for (int y = 0; y < vol[z].length; y++)
                    Arrays.fill(vol[z][y], NONE);
    }

    private int[][][] layerForLevel(int iz, float level, int[][][] opaque, int[][][] transparent) {
        if (iz <= level)
            return opaque;
        else if (transparent != null && iz == level + 1)
            return transparent;
        return null;
    }

    private void handleGrounds(int[][][] layer, int iz, int xStart, int xEnd, int yStart, int yEnd) {
        int tile = (iz == 0) ? GROUND_STONE : GROUND_WOOD;
        for (int y = yStart; y < yEnd; y++)
            for (int x = xStart; x < xEnd; x++)
                layer[iz][y][x] = tile;
    }

    private void placeTileAtCenter(int[][][] layer, int iz, int xStart, int xEnd, int yStart, int yEnd, int tile) {
        int xMid = xStart + (xEnd - xStart) / 2;
        int yMid = yStart + (yEnd - yStart) / 2;
        layer[iz][yMid][xMid] = tile;
    }

    private void handleCorners(int[][][] corners, int ix, int iy, int iz, int xStart, int xEnd, int yStart, int yEnd,
            int zStart, int zEnd, int maxX, int maxY) {
        // NW corner
        if (ix == 0 && iy == 0) {
            for (int z = zStart; z < zEnd; z++) {
                corners[z][yStart][xStart] = WALL_NW_CORNER;
                for (int y = yStart + 1; y < yEnd; y++)
                    corners[z][y][xStart] = WALL_WEST;
                for (int x = xStart + 1; x < xEnd; x++)
                    corners[z][yStart][x] = WALL_NORTH;
            }
            return;
        }
        // NE corner
        if (ix == maxX && iy == 0) {
            for (int z = zStart; z < zEnd; z++) {
                corners[z][yStart][xEnd - 1] = WALL_NE_CORNER;
                for (int y = yStart + 1; y < yEnd; y++)
                    corners[z][y][xEnd - 1] = WALL_EAST;
                for (int x = xStart; x < xEnd - 1; x++)
                    corners[z][yStart][x] = WALL_NORTH;
            }
            return;
        }
        // SW corner
        if (ix == 0 && iy == maxY) {
            for (int z = zStart; z < zEnd; z++) {
                corners[z][yEnd - 1][xStart] = WALL_SW_CORNER;
                for (int y = yStart; y < yEnd - 1; y++)
                    corners[z][y][xStart] = WALL_WEST;
                for (int x = xStart + 1; x < xEnd; x++)
                    corners[z][yEnd - 1][x] = WALL_SOUTH;
            }
            return;
        }
        // SE corner
        if (ix == maxX && iy == maxY) {
            for (int z = zStart; z < zEnd; z++) {
                corners[z][yEnd - 1][xEnd - 1] = WALL_SE_CORNER;
                for (int y = yStart; y < yEnd - 1; y++)
                    corners[z][y][xEnd - 1] = WALL_EAST;
                for (int x = xStart; x < xEnd - 1; x++)
                    corners[z][yEnd - 1][x] = WALL_SOUTH;
            }
        }
    }

    private void handleBorders(int[][][] walls, int ix, int iy, int iz, int xStart, int xEnd, int yStart, int yEnd,
            int zStart, int zEnd, int maxX, int maxY) {
        if (ix == 0 && iy > 0 && iy < maxY) // Ouest
            for (int z = zStart; z < zEnd; z++)
                for (int y = yStart; y < yEnd; y++)
                    walls[z][y][xStart] = WALL_WEST;
        if (ix == maxX && iy > 0 && iy < maxY) // Est
            for (int z = zStart; z < zEnd; z++)
                for (int y = yStart; y < yEnd; y++)
                    walls[z][y][xEnd - 1] = WALL_EAST;
        if (iy == 0 && ix > 0 && ix < maxX) // Nord
            for (int z = zStart; z < zEnd; z++)
                for (int x = xStart; x < xEnd; x++)
                    walls[z][yStart][x] = WALL_NORTH;
        if (iy == maxY && ix > 0 && ix < maxX) // Sud
            for (int z = zStart; z < zEnd; z++)
                for (int x = xStart; x < xEnd; x++)
                    walls[z][yEnd - 1][x] = WALL_SOUTH;
    }

    private void init() {
        ((JFrame)window).setTitle("Mon jeu à moi");
        ((JFrame)window).setSize(1800, 1169);
        ((JFrame)window).setResizable(true);
        window.setTileSize(130, 120);
        window.setFPS(40);
        window.setVisible(true);
    }

    private void onMid(ICharacter<?> e) {
    }

    private void onBegin(ICharacter<?> e) {
    }

    private void onEnd(ICharacter<?> e) {
        if (animationListener != null)
            animationListener.updateMode(e);
    }

    private void onTick(UUID id, ICharacter<?> e) {
        if (animationListener != null) {
            animationListener.updatePosition(e);
            animationListener.checkCollision(e);
        }
    }

    public void scroll(int dx, int dy) {
        originX += dx * SCROLL_SPEED;
        originY += dy * SCROLL_SPEED;
        window.scroll(originX, originY);
    }

    private void createCharacter(UUID id, ICharacter<? extends ICharacterMode> character) {
        characterById.put(id, character);
        character.setTickAnimationTrigger(e -> onTick(id, e));
        character.setBeginAnimationTrigger(this::onBegin);
        character.setEndAnimationTrigger(this::onEnd);
        character.setMidAnimationTrigger(this::onMid);
        window.add(character);
    }

    public void createCrusader(UUID id) {
        Crusader crusader = new Crusader(id);
        crusader.setFrameRate(Crusader.Mode.TURN_LEFT, 0.4f);
        crusader.setFrameRate(Crusader.Mode.TURN_RIGHT, 0.4f);
        crusader.setFrameRate(Crusader.Mode.WALK_LEFT, 0.3f);
        crusader.setFrameRate(Crusader.Mode.WALK_RIGHT, 0.3f);
        crusader.setFrameRate(Crusader.Mode.JUMP, 0.1f);
        createCharacter(id, crusader);
    }

    public void createSpiders(Set<UUID> spiderIds) {
        spiderIds.forEach(id -> createCharacter(id, new Spider(id)));
    }
}
