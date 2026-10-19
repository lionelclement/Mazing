package fr.ubordeaux.ao.mazing.view;

public enum Orientation {
	NORTH,
	NORTH_EAST,
	NORTH_WEST,
	SOUTH,
	SOUTH_EAST,
	SOUTH_WEST,
	EAST,
	WEST;

	public Orientation asSimple() {
		return switch (this) {
			case NORTH, NORTH_EAST, NORTH_WEST -> NORTH;
			case SOUTH, SOUTH_EAST, SOUTH_WEST -> SOUTH;
			case EAST -> EAST;
			case WEST -> WEST;
		};
	}
}
