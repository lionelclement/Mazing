package fr.ubordeaux.ao.project1.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class Maze {
    private final int width, height, depth;
    private Cell[][][] grid;
    private final Random rand = new Random();
    private Cell begin;
    private Cell end;

    public Maze(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.grid = null;
        clear();
    }

    public void clear() {
        grid = new Cell[depth][height][width];
        for (int z = 0; z < depth; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    grid[z][y][x] = new Cell(x, y, z);
                    // On ajoute des murs à l'exterieur
                    for (Direction3D d : outerWalls(grid[z][y][x])) {
                        grid[z][y][x].putOnWalls(d, true);
                    }
                    // On ferme tous les chemins partout
                    for (Direction3D d : Direction3D.values()) {
                        grid[z][y][x].putOnPaths(d, false);
                    }
                }
            }
        }
    }

    public void generate() {
        clear();

        Stack<Cell> stack = new Stack<>();
        begin = grid[0][0][0];
        begin.setVisited(true);
        stack.push(begin);

        // Génération du labyrinthe avec DFS
        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            List<Direction3D> neighbors = unvisitedNeighbors(current);

            if (!neighbors.isEmpty()) {
                Direction3D dir = neighbors.get(rand.nextInt(neighbors.size()));
                Cell next = getNeighbor(current, dir);

                current.putOnPaths(dir, true);
                next.putOnPaths(opposite(dir), true);

                next.setVisited(true);
                stack.push(next);
            } else {
                stack.pop();
            }
        }

        System.err.println("Maze generated:");
        System.err.println(this);
        System.err.println("Start: " + begin + ", End: " + end);

    }

    private List<Direction3D> unvisitedNeighbors(Cell c) {
        List<Direction3D> dirs = new ArrayList<>();
        for (Direction3D d : Direction3D.values()) {
            Cell n = getNeighbor(c, d);
            if (n != null && !n.isVisited())
                dirs.add(d);
        }
        return dirs;
    }

    private Cell getNeighbor(Cell c, Direction3D d) {
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

    private Direction3D opposite(Direction3D d) {
        return switch (d) {
            case EAST -> Direction3D.WEST;
            case WEST -> Direction3D.EAST;
            case NORTH -> Direction3D.SOUTH;
            case SOUTH -> Direction3D.NORTH;
            case UP -> Direction3D.DOWN;
            case DOWN -> Direction3D.UP;

        };
    }

    private List<Direction3D> outerWalls(Cell c) {
        List<Direction3D> result = new ArrayList<>();
        int nx = c.getX(), ny = c.getY(), nz = c.getZ();
        if (nx == 0) {
            result.add(Direction3D.WEST);
        }
        if (nx >= width - 1) {
            result.add(Direction3D.EAST);
        }
        if (ny == 0) {
            result.add(Direction3D.NORTH);
        }
        if (ny >= height - 1) {
            result.add(Direction3D.SOUTH);
        }
        if (nz == 0) {
            result.add(Direction3D.DOWN);
        }
        if (nz >= depth - 1) {
            result.add(Direction3D.UP);
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
                    result.append(c.isPath(Direction3D.NORTH) ? "+   " : "+___");
                }
                result.append("+\n");

                // Ligne du corps de chaque cellule (mur WEST / EAST)
                for (int x = 0; x < width; x++) {
                    Cell c = grid[z][y][x];

                    // mur OUEST à gauche, espace à l'intérieur
                    result.append(c.isPath(Direction3D.WEST) ? " " : "|");
                    // symbole au centre de la cellule
                    String symbol = "   ";
                    if (c == begin){
                        symbol = " $ ";
                    }
                    else{
                    boolean up = c.isPath(Direction3D.UP);
                    boolean down = c.isPath(Direction3D.DOWN);
                    if (up && down)
                        symbol = " x ";
                    else if (up)
                        symbol = " ^ ";
                    else if (down)
                        symbol = " v ";
                    }
                    result.append(symbol); // espace intérieur de la cellule
                }
                // Mur EST de la dernière cellule
                Cell last = grid[z][y][width - 1];
                result.append(last.isPath(Direction3D.EAST) ? " " : "|");
                result.append("\n");
            }

            // Ligne du bas de la dernière rangée (mur SOUTH)
            for (int x = 0; x < width; x++) {
                Cell c = grid[z][height - 1][x];
                result.append(c.isPath(Direction3D.SOUTH) ? "+   " : "+___");
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
    public boolean isObstacleAt(float x, float y, float z, Direction3D direction) {
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

        // Tolérance pour éviter que le personnage soit bloqué avant d’atteindre la
        // frontière d’une cellule
        final float edgeTolerance = 1.0f - wallWidth;

        // On autorise le mouvement tant qu’on n’a pas atteint le bord de la cellule
        switch (direction) {
            case EAST:
                if (x % 1 < edgeTolerance)
                    return false;
                break;
            case WEST:
                if (x % 1 > wallWidth)
                    return false;
                break;
            case SOUTH:
                if (y % 1 < edgeTolerance)
                    return false;
                break;
            case NORTH:
                if (y % 1 > wallHeight)
                    return false;
                break;
            case UP:
                if (z % 1 < 1.0f - wallDepth)
                    return false;
                break;
            case DOWN:
                if (z % 1 > wallDepth)
                    return false;
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
    public boolean canMove(float cellX, float cellY, float cellZ, String direction) {
        if (isObstacleAt(cellX, cellY, cellZ, Direction3D.valueOf(direction)))
            return false;
        return true;
        // Cell next = getNeighbor(grid[(int) cellZ][(int) cellY][(int) cellX],
        // direction);
        // return next != null; // on peut aller dedans
    }

    public List<Direction3D> shortestPath(int startX, int startY, int startZ,
            int endX, int endY, int endZ) {

        class Node {
            int x, y, z;
            List<Direction3D> path;

            Node(int x, int y, int z, List<Direction3D> path) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.path = path;
            }
        }

        boolean[][][] visited = new boolean[depth][height][width];
        Queue<Node> queue = new LinkedList<>();

        queue.add(new Node(startX, startY, startZ, new ArrayList<>()));
        visited[startZ][startY][startX] = true;

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Si on est arrivé à la destination → on renvoie le chemin
            if (current.x == endX && current.y == endY && current.z == endZ) {
                return current.path;
            }

            Cell currentCell = grid[current.z][current.y][current.x];

            // Explorer toutes les CellDirections possibles
            for (Direction3D dir : Direction3D.values()) {
                if (!currentCell.isPath(dir))
                    continue; // pas de passage dans cette direction

                Cell next = getNeighbor(currentCell, dir);
                if (next == null)
                    continue; // hors limites

                if (!visited[next.getZ()][next.getY()][next.getX()]) {
                    visited[next.getZ()][next.getY()][next.getX()] = true;

                    List<Direction3D> newPath = new ArrayList<>(current.path);
                    newPath.add(dir);

                    queue.add(new Node(next.getX(), next.getY(), next.getZ(), newPath));
                }
            }
        }

        // Aucun chemin trouvé
        return Collections.emptyList();
    }

    public Cell[][][] getGrid() {
        return grid;
    }

    public Cell getBegin() {
        return begin;
    }

    public Cell getEnd() {
        return end;
    }

}