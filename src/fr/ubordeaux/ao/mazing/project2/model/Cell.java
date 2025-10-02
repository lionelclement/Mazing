package fr.ubordeaux.ao.mazing.project2.model;

import java.util.HashMap;
import java.util.Map;

public class Cell {
    public final int x, y;
    public boolean visited = false; // utile pour génération
    public final Map<Direction, Boolean> walls = new HashMap<>();

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        // initialement, tous les murs sont présents
        for (Direction d : Direction.values()) {
            walls.put(d, true);
        }
    }
}

enum Direction {
    UP, DOWN, LEFT, RIGHT
}
