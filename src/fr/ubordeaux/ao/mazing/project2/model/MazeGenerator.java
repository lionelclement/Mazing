package fr.ubordeaux.ao.mazing.project2.model;

import java.util.*;

public class MazeGenerator {
    private final int width, height;
    private final Cell[][] grid;
    private final Random rand = new Random();

    public MazeGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        grid = new Cell[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = new Cell(x, y);
            }
        }
    }

    public Cell[][] generate() {
        Stack<Cell> stack = new Stack<>();
        Cell start = grid[0][0];
        start.visited = true;
        stack.push(start);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            List<Direction> neighbors = unvisitedNeighbors(current);

            if (!neighbors.isEmpty()) {
                Direction dir = neighbors.get(rand.nextInt(neighbors.size()));
                Cell next = getNeighbor(current, dir);

                // casser le mur entre current et next
                current.walls.put(dir, false);
                next.walls.put(opposite(dir), false);

                next.visited = true;
                stack.push(next);
            } else {
                stack.pop();
            }
        }
        return grid;
    }

    private List<Direction> unvisitedNeighbors(Cell c) {
        List<Direction> dirs = new ArrayList<>();
        for (Direction d : Direction.values()) {
            Cell n = getNeighbor(c, d);
            if (n != null && !n.visited)
                dirs.add(d);
        }
        return dirs;
    }

    private Cell getNeighbor(Cell c, Direction d) {
        int nx = c.x, ny = c.y;
        switch (d) {
            case UP -> ny--;
            case DOWN -> ny++;
            case LEFT -> nx--;
            case RIGHT -> nx++;
        }
        if (nx < 0 || nx >= width || ny < 0 || ny >= height)
            return null;
        return grid[ny][nx];
    }

    private Direction opposite(Direction d) {
        return switch (d) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }
}
