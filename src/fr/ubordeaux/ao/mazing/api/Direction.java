package fr.ubordeaux.ao.mazing.api;

public enum Direction {
    SOUTHEAST(0, 135),
    EAST(1, 90),
    NORTHEAST(2, 45),
    NORTH(3, 0),
    NORTHWEST(4, 315), // ou -45
    WEST(5, 270),
    SOUTHWEST(6, 225), // ou -135
    SOUTH(7, 180);

    private final int value; // index ordinal
    private final int angle; // angle en degrés

    private Direction(int value, int angle) {
        this.value = value;
        this.angle = angle;
    }

    public int getValue() {
        return value;
    }

    public int getAngle() {
        return angle;
    }
}
