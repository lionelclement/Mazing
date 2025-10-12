package fr.ubordeaux.ao.project1.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Maze {
    private final int width, height, depth;
    private Cell[][][] grid;
    private final Random rand = new Random();

    public Maze(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.grid = null;
        clear();
    }

    public Cell[][][] clear() {
        grid = new Cell[depth][height][width];
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
        return grid;
    }

    public Cell[][][] generate() {
        clear();
        Stack<Cell> stack = new Stack<>();
        Cell start = grid[0][0][0];
        // Cell lastCell = start;
        start.setVisited(true);
        stack.push(start);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();

            List<Direction> neighbors = unvisitedNeighbors(current);

            if (!neighbors.isEmpty()) {
                // on tire au sort un chemin vers une nouvelle cellule
                Direction dir = neighbors.get(rand.nextInt(neighbors.size()));
                Cell next = getNeighbor(current, dir);

                // mettre une porte entre current et next
                current.putOnPaths(dir, true);
                next.putOnPaths(opposite(dir), true);

                next.setVisited(true);
                stack.push(next);
            } else {
                stack.pop();
            }
        }
        // System.err.println("Maze generated:");
        // System.err.println(this);
        return grid;
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
            default -> {
            }

        }
        if (nx < 0 || nx >= width || ny < 0 || ny >= height || nz < 0 || nz >= depth)
            return null;
        return grid[nz][ny][nx];
    }

    private Direction opposite(Direction d) {
        return switch (d) {
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case NORTHEAST -> Direction.SOUTHWEST;
            case NORTHWEST -> Direction.SOUTHEAST;
            case SOUTHEAST -> Direction.NORTHWEST;
            case SOUTHWEST -> Direction.NORTHEAST;

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

/**
 * Détermine s’il y a un obstacle à la position donnée dans la direction donnée.
 * Retourne true si un mur ou la limite du labyrinthe bloque le passage.
 */
public boolean isObstacleAt(float x, float y, float z, Direction direction) {
    // Marges de sécurité autour des murs
    final float wallWidth = 0.20f;
    final float wallHeight = 0.20f;
    final float wallDepth = 0.20f;

    // Vérifie les limites du labyrinthe (en dehors = obstacle)
    if (x <= wallWidth || x >= width - wallWidth ||
        y <= wallHeight || y >= height - wallHeight ||
        z < 0 || z >= depth) {
        return true;
    }

    // Tolérance pour éviter que le personnage soit bloqué avant d’atteindre la frontière d’une cellule
    final float edgeTolerance = 1.0f - wallWidth;

    // On autorise le mouvement tant qu’on n’a pas atteint le bord de la cellule
    switch (direction) {
        case EAST:
            if (x % 1 < edgeTolerance) return false;
            break;
        case WEST:
            if (x % 1 > wallWidth) return false;
            break;
        case SOUTH:
            if (y % 1 < edgeTolerance) return false;
            break;
        case NORTH:
            if (y % 1 > wallHeight) return false;
            break;
        case UP:
            if (z % 1 < 1.0f - wallDepth) return false;
            break;
        case DOWN:
            if (z % 1 > wallDepth) return false;
            break;
        default:
            break;
    }

    // Calcul de la cellule actuelle (centrée sur les coordonnées entières)
    int cellX = Math.max(0, Math.min(width - 1, (int) Math.floor(x)));
    int cellY = Math.max(0, Math.min(height - 1, (int) Math.floor(y)));
    int cellZ = Math.max(0, Math.min(depth - 1, (int) Math.floor(z)));

    Cell cell = grid[cellZ][cellY][cellX];

    // Si aucun chemin n'est ouvert dans cette direction, c'est un obstacle
    return !cell.isPath(direction);
}

    /**
     * Vérifie si un personnage peut se déplacer dans une direction donnée.
     * Retourne false s’il y a un mur ou que le déplacement sortirait du labyrinthe.
     */
    public boolean canMove(float cellX, float cellY, float cellZ, Direction direction) {
        if (isObstacleAt(cellX, cellY, cellZ, direction))
            return false;
        return true;
        // Cell next = getNeighbor(grid[(int) cellZ][(int) cellY][(int) cellX],
        // direction);
        // return next != null; // on peut aller dedans
    }

}