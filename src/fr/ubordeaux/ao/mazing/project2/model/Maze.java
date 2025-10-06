package fr.ubordeaux.ao.mazing.project2.model;

import java.util.*;

public final class Maze {
    private final int width, height, depth;
    private final Cell[][][] grid;
    private final Random rand = new Random();

    public Maze(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        grid = new Cell[depth][height][width];
        clear();
    }

    public void clear() {
        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    grid[z][y][x] = new Cell(x, y, z);
                    // On ajoute des murs à l'exterieur
                    for (Direction d : outerWalls(grid[z][y][x])) {
                        grid[z][y][x].putOnWalls(d, true);
                    }
                    // On ferme tous les chemins partout
                    for (Direction d : Direction.values()) {
                        grid[z][y][x].putOnPaths(d, false);
                    }
                }
            }
        }
    }

    public void generate() {
        Stack<Cell> stack = new Stack<>();
        Cell start = grid[0][0][0];
        Cell lastCell = start;
        start.setVisited(true);
        stack.push(start);

        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                grid[z][y][0].putOnWalls(Direction.WEST, true);
                grid[z][y][width - 1].putOnWalls(Direction.WEST, true);
            }
            for (int x = 0; x < width; x++) {
                grid[z][0][x].putOnWalls(Direction.NORTH, true);
                grid[z][height - 1][x].putOnWalls(Direction.NORTH, true);
            }
        }

        while (!stack.isEmpty()) {
            Cell current = stack.peek();

            List<Direction> neighbors = unvisitedNeighbors(current);

            if (!neighbors.isEmpty()) {
                // on tire au sort un chemin vers une nouvelle cellule
                Direction dirPath = neighbors.get(rand.nextInt(neighbors.size()));
                Cell next = getNeighbor(current, dirPath);

                // mettre une ouverture entre current et next
                // current.putOnPaths(dirPath, true);
                // next.putOnPaths(opposite(dirPath), true);

                // on tire au sort un chemin vers une nouvelle cellule
                // Direction dirDoor = neighbors.get(rand.nextInt(neighbors.size()));
                // Cell door = getPlanNeighbor(current, dirDoor);

                // if (door != null && dirDoor == dirPath) {

                // mettre une porte entre current et next
                // current.putOnDoors(dirDoor, true);
                // door.putOnDoors(opposite(dirDoor), true);
                // }

                next.setVisited(true);
                stack.push(next);
            } else {
                stack.pop();
            }
        }
        lastCell.setTreasure(true);
        System.err.println("Maze generated:");
        System.err.println(this);
    }

    private List<Direction> unvisitedNeighbors(Cell c) {
        List<Direction> dirs = new ArrayList<>();
        for (Direction d : Direction.values()) {
            Cell n = getNeighbor(c, d);
            if (n != null && !n.isVisited())
                dirs.add(d);
        }
        return dirs;
    }

    private Cell getNeighbor(Cell c, Direction d) {
        int nx = c.getX(), ny = c.getY(), nz = c.getZ();
        switch (d) {
            case NORTH -> ny--;
            case SOUTH -> ny++;
            case WEST -> nx--;
            case EAST -> nx++;
            case UP -> nz++;
            case DOWN -> nz--;
        }
        if (nx < 0 || nx >= width || ny < 0 || ny >= height || nz < 0 || nz >= depth)
            return null;
        return grid[nz][ny][nx];
    }

    private Cell getPlanNeighbor(Cell c, Direction d) {
        int nx = c.getX(), ny = c.getY();
        switch (d) {
            case NORTH -> ny--;
            case SOUTH -> ny++;
            case WEST -> nx--;
            case EAST -> nx++;
            default -> {
                return null;
            }
        }
        if (nx < 0 || nx >= width || ny < 0 || ny >= height)
            return null;
        return grid[c.getZ()][ny][nx];
    }

    private Direction opposite(Direction d) {
        return switch (d) {
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
        };
    }

    private List<Direction> outerWalls(Cell c) {
        List<Direction> result = new ArrayList<>();
        int nx = c.getX(), ny = c.getY(), nz = c.getZ();
        if (nx == 0) {
            result.add(Direction.WEST);
        }
        if (nx >= width - 1) {
            result.add(Direction.EAST);
        }
        if (ny == 0) {
            result.add(Direction.NORTH);
        }
        if (ny >= height - 1) {
            result.add(Direction.SOUTH);
        }
        if (nz == 0) {
            result.add(Direction.DOWN);
        }
        if (nz >= depth - 1) {
            result.add(Direction.UP);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (int z = 0; z < depth; z++) {
            result.append("Layer ").append(z).append(":\n");

            for (int y = 0; y < height; y++) {
                // Ligne du haut de chaque cellule (mur NORTH)
                for (int x = 0; x < width; x++) {
                    Cell c = grid[z][y][x];
                    result.append(c.isPath(Direction.NORTH) ? "+   " : "+___");
                }
                result.append("+\n");

                // Ligne du corps de chaque cellule (mur WEST / EAST)
                for (int x = 0; x < width; x++) {
                    Cell c = grid[z][y][x];
                    // mur OUEST à gauche, espace à l'intérieur
                    result.append(c.isPath(Direction.WEST) ? " " : "|");
                    // symbole au centre de la cellule
                    String symbol = "   ";
                    boolean up = c.isPath(Direction.UP);
                    boolean down = c.isPath(Direction.DOWN);
                    if (up && down)
                        symbol = " x ";
                    else if (up)
                        symbol = " ^ ";
                    else if (down)
                        symbol = " v ";
                    result.append(symbol); // espace intérieur de la cellule
                }
                // Mur EST de la dernière cellule
                Cell last = grid[z][y][width - 1];
                result.append(last.isPath(Direction.EAST) ? " " : "|");
                result.append("\n");
            }

            // Ligne du bas de la dernière rangée (mur SOUTH)
            for (int x = 0; x < width; x++) {
                Cell c = grid[z][height - 1][x];
                result.append(c.isPath(Direction.SOUTH) ? "+   " : "+___");
            }
            result.append("+\n\n");
        }

        return result.toString();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public Cell getCell(int x, int y, int z) {
        return grid[z][y][x];
    }

}
