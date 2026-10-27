package fr.ubordeaux.ao.mazing.view.sprite;

import fr.ubordeaux.ao.mazing.view.texture.TextureAtlas;
import fr.ubordeaux.ao.mazing.view.texture.TextureAtlases;

import java.util.HashMap;

public final class Sprites {

	private static final HashMap<String, Sprite> registry = new HashMap<>();
	public static final java.util.Map<String, Sprite> REGISTRY = java.util.Collections.unmodifiableMap(registry);

	public static final Sprite BARREL = registerAngle("barrel", 0, 0, 1, 0, 2, 0, 3, 0);
	public static final Sprite BARRELS_STACKED = registerAngle("barrels_stacked", 4, 0, 5, 0, 6, 0, 7, 0);
	public static final Sprite BARRELS = registerAngle("barrels", 8, 0, 9, 0, 10, 0, 11, 0);
	public static final Sprite BRIDGE_BROKEN = registerAngle("bridge_broken", 12, 0, 13, 0, 14, 0, 15, 0);
	public static final Sprite BRIDGE = registerAngle("bridge", 0, 1, 1, 1, 2, 1, 3, 1);
	public static final Sprite CHAIR = registerAngle("chair", 4, 1, 5, 1, 6, 1, 7, 1);
	public static final Sprite CHEST_CLOSED = registerAngle("chest_closed", 8, 1, 9, 1, 10, 1, 11, 1);
	public static final Sprite CHEST_OPEN = registerAngle("chest_open", 12, 1, 13, 1, 14, 1, 15, 1);
	public static final Sprite DIRT_TILES = registerAngle("dirt_tiles", 0, 2, 1, 2, 2, 2, 3, 2);
	public static final Sprite DIRT = registerAngle("dirt", 4, 2, 5, 2, 6, 2, 7, 2);
	public static final Sprite PLANKS_BROKEN = registerAngle("planks_broken", 8, 2, 9, 2, 10, 2, 11, 2);
	public static final Sprite PLANKS_HOLE = registerAngle("planks_hole", 12, 2, 13, 2, 14, 2, 15, 2);
	public static final Sprite PLANKS = registerAngle("planks", 0, 3, 1, 3, 2, 3, 3, 3);
	public static final Sprite STAIRS_AGED = registerAngle("stairs_aged", 4, 3, 5, 3, 6, 3, 7, 3);
	public static final Sprite STAIRS_CORNER = registerAngle("stairs_corner", 8, 3, 9, 3, 10, 3, 11, 3);
	public static final Sprite STAIRS_SPIRAL = registerAngle("stairs_spiral", 12, 3, 13, 3, 14, 3, 15, 3);
	public static final Sprite STAIRS = registerAngle("stairs", 0, 4, 1, 4, 2, 4, 3, 4);
	public static final Sprite STONE_COLUMN_WOOD = registerAngle("stone_column_wood", 4, 4, 5, 4, 6, 4, 7, 4);
	public static final Sprite STONE_COLUMN = registerAngle("stone_column", 8, 4, 9, 4, 10, 4, 11, 4);
	public static final Sprite STONE_CORNER = registerAngle("stone_corner", 12, 4, 13, 4, 14, 4, 15, 4);
	public static final Sprite STONE_INSET = registerAngle("stone_inset", 0, 5, 1, 5, 2, 5, 3, 5);
	public static final Sprite STONE_LEFT = registerAngle("stone_left", 4, 5, 5, 5, 6, 5, 7, 5);
	public static final Sprite STONE_MISSING_TILES = registerAngle("stone_missing_tiles", 8, 5, 9, 5, 10, 5, 11, 5);
	public static final Sprite STONE_RIGHT = registerAngle("stone_right", 12, 5, 13, 5, 14, 5, 15, 5);
	public static final Sprite STONE_SIDE_UNEVEN = registerAngle("stone_side_uneven", 0, 6, 1, 6, 2, 6, 3, 6);
	public static final Sprite STONE_SIDE = registerAngle("stone_side", 4, 6, 5, 6, 6, 6, 7, 6);
	public static final Sprite STONE_STEP = registerAngle("stone_step", 8, 6, 9, 6, 10, 6, 11, 6);
	public static final Sprite STONE_STEPS = registerAngle("stone_steps", 12, 6, 13, 6, 14, 6, 15, 6);
	public static final Sprite STONE_TILE = registerAngle("stone_tile", 0, 7, 1, 7, 2, 7, 3, 7);
	public static final Sprite STONE_UNEVEN = registerAngle("stone_uneven", 4, 7, 5, 7, 6, 7, 7, 7);
	public static final Sprite STONE_WALL_AGED_LEFT = registerAngle("stone_wall_aged_left", 8, 7, 9, 7, 10, 7, 11, 7);
	public static final Sprite STONE_WALL_AGED_RIGHT = registerAngle("stone_wall_aged_right", 12, 7, 13, 7, 14, 7, 15, 7);
	public static final Sprite STONE_WALL_AGED = registerAngle("stone_wall_aged", 0, 8, 1, 8, 2, 8, 3, 8);
	public static final Sprite STONE_WALL_ARCHWAYS = registerAngle("stone_wall_archways", 4, 8, 5, 8, 6, 8, 7, 8);
	public static final Sprite STONE_WALL_BROKEN_LEFT = registerAngle("stone_wall_broken_left", 8, 8, 9, 8, 10, 8, 11, 8);
	public static final Sprite STONE_WALL_BROKEN_RIGHT = registerAngle("stone_wall_broken_right", 12, 8, 13, 8, 14, 8, 15, 8);
	public static final Sprite STONE_WALL_BROKEN = registerAngle("stone_wall_broken", 0, 9, 1, 9, 2, 9, 3, 9);
	public static final Sprite STONE_WALL_COLUMN_IN = registerAngle("stone_wall_column_in", 4, 9, 5, 9, 6, 9, 7, 9);
	public static final Sprite STONE_WALL_COLUMN = registerAngle("stone_wall_column", 8, 9, 9, 9, 10, 9, 11, 9);
	public static final Sprite STONE_WALL_CORNER = registerAngle("stone_wall_corner", 12, 9, 13, 9, 14, 9, 15, 9);
	public static final Sprite STONE_WALL_DOOR_BARS = registerAngle("stone_wall_door_bars", 0, 10, 1, 10, 2, 10, 3, 10);
	public static final Sprite STONE_WALL_DOOR_CLOSED = registerAngle("stone_wall_door_closed", 4, 10, 5, 10, 6, 10, 7, 10);
	public static final Sprite STONE_WALL_DOOR_OPEN = registerAngle("stone_wall_door_open", 8, 10, 9, 10, 10, 10, 11, 10);
	public static final Sprite STONE_WALL_DOOR = registerAngle("stone_wall_door", 12, 10, 13, 10, 14, 10, 15, 10);
	public static final Sprite STONE_WALL_GATE_BARS = registerAngle("stone_wall_gate_bars", 0, 11, 1, 11, 2, 110, 3, 110);
	public static final Sprite STONE_WALL_GATE_CLOSED = registerAngle("stone_wall_gate_closed", 4, 11, 5, 11, 6, 11, 7, 11);
	public static final Sprite STONE_WALL_GATE_OPEN = registerAngle("stone_wall_gate_open", 8, 11, 9, 11, 10, 11, 11, 11);
	public static final Sprite STONE_WALL_GATE = registerAngle("stone_wall_gate", 12, 10, 13, 11, 14, 11, 15, 11);
	public static final Sprite STONE_WALL_HALF = registerAngle("stone_wall_half", 0, 12, 1, 12, 2, 12, 3, 12);
	public static final Sprite STONE_WALL_HOLE = registerAngle("stone_wall_hole", 4, 12, 5, 12, 6, 12, 7, 12);
	public static final Sprite STONE_WALL_ROUND_BROKEN = registerAngle("stone_wall_round_broken", 8, 12, 9, 12, 10, 12, 11, 12);
	public static final Sprite STONE_WALL_ROUND_WINDOW = registerAngle("stone_wall_round_window", 12, 12, 13, 12, 14, 12, 15, 12);
	public static final Sprite STONE_WALL_ROUND = registerAngle("stone_wall_round", 0, 13, 1, 13, 2, 13, 3, 13);
	public static final Sprite STONE_WALL_STRUCTURE = registerAngle("stone_wall_structure", 4, 13, 5, 13, 6, 13, 7, 13);
	public static final Sprite STONE_WALL_TOP = registerAngle("stone_wall_top", 8, 13, 9, 13, 10, 13, 11, 13);
	public static final Sprite STONE_WALL_WINDOW_BARS = registerAngle("stone_wall_window_bars", 12, 13, 13, 13, 14, 13, 15, 13);
	public static final Sprite STONE_WALL_WINDOW = registerAngle("stone_wall_window", 0, 14, 1, 14, 2, 14, 3, 14);
	public static final Sprite STONE_WALL = registerAngle("stone_wall", 4, 14, 5, 14, 6, 14, 7, 14);
	public static final Sprite STONE = registerAngle("stone", 8, 14, 9, 14, 10, 14, 11, 14);
	public static final Sprite TABLE_CHAIRS_BROKEN = registerAngle("table_chairs_broken", 12, 14, 13, 14, 14, 14, 15, 14);
	public static final Sprite TABLE_ROUND_CHAIRS = registerAngle("table_round_chairs", 0, 15, 1, 15, 2, 15, 3, 15);
	public static final Sprite TABLE_ROUND_ITEMS_CHAIRS = registerAngle("table_round_items_chairs", 4, 15, 5, 15, 6, 15, 7, 15);
	public static final Sprite TABLE_ROUND = registerAngle("table_round", 8, 15, 9, 15, 10, 15, 11, 15);
	public static final Sprite TABLE_SHORT_CHAIRS = registerAngle("table_short_chairs", 12, 15, 13, 15, 14, 15, 15, 15);
	public static final Sprite TABLE_SHORT = registerAngle("table_short", 0, 16, 1, 16, 2, 16, 3, 16);
	public static final Sprite WOODEN_CRATE = registerAngle("wooden_crate", 4, 16, 5, 16, 6, 16, 7, 16);
	public static final Sprite WOODEN_CRATES = registerAngle("wooden_crates", 8, 16, 9, 16, 10, 16, 11, 16);
	public static final Sprite WOODEN_PILE = registerAngle("wooden_pile", 12, 16, 13, 16, 14, 16, 15, 16);
	public static final Sprite WOODEN_SUPPORT_BEAMS = registerAngle("wooden_support_beams", 0, 17, 1, 17, 2, 17, 3, 17);
	public static final Sprite WOODEN_SUPPORTS_BEAM = registerAngle("wooden_supports_beam", 4, 17, 5, 17, 6, 17, 7, 17);
	public static final Sprite WOODEN_SUPPORTS_BLOCK = registerAngle("wooden_supports_block", 8, 17, 9, 17, 10, 17, 11, 17);
	public static final Sprite WOODEN_SUPPORTS = registerAngle("wooden_supports", 12, 17, 13, 17, 14, 17, 15, 17);


	// private static Renderable registerENSW(String name, TextureAtlas atlas, int... indexes) {
	// 	Renderable r = new SimpleRenderable.Builder(TextureAtlases.ANGLE)
	// 			.east(0, 0).north(1, 0).south(2, 0).west(3, 0).build();
	// 	registry.put(name, r);
	// 	return r;
	// }

	private static Sprite registerENSW(String name, TextureAtlas atlas, int ex, int ey, int nx, int ny, int sx, int sy, int wx, int wy) {
		Sprite r = new OrientedSprite.Builder(atlas)
				.east(ex, ey).north(nx, ny).south(sx, sy).west(wx, wy).build();
		registry.put(name, r);
		return r;
	}

	private static Sprite registerAngle(String name, int ex, int ey, int nx, int ny, int sx, int sy, int wx, int wy) {
		return registerENSW(name, TextureAtlases.ANGLE, ex, ey, nx, ny, sx, sy, wx, wy);
	}
}
