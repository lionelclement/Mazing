package fr.ubordeaux.ao.mazing.view;

/**
 * World-space facing or tile side: EAST = +X, NORTH = +Y.
 * The PNG naming conventions are translated by the sprite registry and
 * must not leak into the public API.
 */
public enum Orientation {
	/**
	 * Provides the north, value.
	 */
	NORTH,
	/**
	 * Provides the north east, value.
	 */
	NORTH_EAST,
	/**
	 * Provides the north west, value.
	 */
	NORTH_WEST,
	/**
	 * Provides the south, value.
	 */
	SOUTH,
	/**
	 * Provides the south east, value.
	 */
	SOUTH_EAST,
	/**
	 * Provides the south west, value.
	 */
	SOUTH_WEST,
	/**
	 * Provides the east, value.
	 */
	EAST,
	/**
	 * Provides the west; value.
	 */
	WEST;

	/**
	 * Performs the asSimple operation.
	 * @return the resulting value
	 */
	public Orientation asSimple() {
		return switch (this) {
			case NORTH, NORTH_EAST, NORTH_WEST -> NORTH;
			case SOUTH, SOUTH_EAST, SOUTH_WEST -> SOUTH;
			case EAST -> EAST;
			case WEST -> WEST;
		};
	}
}
