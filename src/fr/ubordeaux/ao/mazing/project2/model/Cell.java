package fr.ubordeaux.ao.mazing.project2.model;

import java.util.HashMap;
import java.util.Map;

public class Cell {
    public final int x, y, z;
    public boolean visited = false;
    public final Map<Direction, Boolean> paths = new HashMap<>();
    public final Map<Direction, Boolean> walls = new HashMap<>();

    public Cell(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cell{");
        sb.append("x=").append(x);
        sb.append(", y=").append(y);
        sb.append(", z=").append(z);
        sb.append(", visited=").append(visited);
        sb.append('}');
        return sb.toString();
    }

    public boolean isWall(Direction d) {
        Boolean b = walls.get(d);
        if (b != null)
            return b;
        return false;
    }

    public boolean isPath(Direction d) {
        Boolean b = paths.get(d);
        if (b != null)
            return b;
        return false;
    }

}
