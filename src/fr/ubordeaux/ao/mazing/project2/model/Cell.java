package fr.ubordeaux.ao.mazing.project2.model;

import java.util.HashMap;
import java.util.Map;

public class Cell {
    private final int x, y, z;
    private boolean visited = false;
    private final Map<Direction, Boolean> paths = new HashMap<>();
    private final Map<Direction, Boolean> walls = new HashMap<>();
    private final Map<Direction, Boolean> doors = new HashMap<>();
    private boolean treasure;

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

    public boolean isDoor(Direction d) {
        Boolean b = doors.get(d);
        if (b != null)
            return b;
        return false;
    }

    public void putOnPaths(Direction dir, boolean b) {
        paths.put(dir, b);
    }

    public void setVisited(boolean b) {
        this.visited = b;
    }

    public void setTreasure(boolean b) {
        this.treasure = b;
    }

    public void putOnWalls(Direction d, boolean b) {
        this.walls.put(d, b);
    }

    public void putOnDoors(Direction d, boolean b) {
        this.doors.put(d, b);
    }

    public boolean isVisited() {
        return visited;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}
