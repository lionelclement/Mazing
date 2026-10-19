package fr.ubordeaux.ao.mazing.view.sprite;

import fr.ubordeaux.ao.mazing.view.texture.TextureAtlas;
import fr.ubordeaux.ao.mazing.view.texture.TextureAtlases;

import java.util.HashMap;

public final class IsometricSprites {

	public static final HashMap<String, Sprite> REGISTRY = new HashMap<>();

	public static final Sprite STONE_COLUMN_WOOD = registerENSW("stone_column_wood", TextureAtlases.ISO_CONSTRUCTIONS, 0, 0, 1, 0, 2, 0, 3, 0);
	public static final Sprite STONE_COLUMN = registerENSW("stone_column", TextureAtlases.ISO_CONSTRUCTIONS, 0, 1, 1, 1, 2, 1, 3, 1);
	public static final Sprite WOODEN_SUPPORT_BEAMS = registerENSW("wooden_support_beams", TextureAtlases.ISO_CONSTRUCTIONS, 0, 2, 1, 2, 2, 2, 3, 2);
	public static final Sprite WOODEN_SUPPORTS_BEAM = registerENSW("wooden_supports_beam", TextureAtlases.ISO_CONSTRUCTIONS, 0, 3, 1, 3, 2, 3, 3, 3);
	public static final Sprite WOODEN_SUPPORTS_BLOCK = registerENSW("wooden_supports_block", TextureAtlases.ISO_CONSTRUCTIONS, 0, 4, 1, 4, 2, 4, 3, 4);
	public static final Sprite WOODEN_SUPPORTS = registerENSW("wooden_supports", TextureAtlases.ISO_CONSTRUCTIONS, 0, 5, 1, 5, 2, 5, 3, 5);

	public static final Sprite BRIDGE_BROKEN = registerENSW("bridge_broken", TextureAtlases.ISO_GROUNDS, 0, 0, 1, 0, 2, 0, 3, 0);
	public static final Sprite BRIDGE = registerENSW("bridge", TextureAtlases.ISO_GROUNDS, 4, 0, 5, 0, 6, 0, 7, 0);
	public static final Sprite DIRT_TILES = registerENSW("dirt_tiles", TextureAtlases.ISO_GROUNDS, 0, 1, 1, 1, 2, 1, 3, 1);
	public static final Sprite DIRT = registerENSW("dirt", TextureAtlases.ISO_GROUNDS, 4, 1, 5, 1, 6, 1, 7, 1);
	public static final Sprite PLANKS_BROKEN = registerENSW("planks_broken", TextureAtlases.ISO_GROUNDS, 0, 2, 1, 2, 2, 2, 3, 2);
	public static final Sprite PLANKS_HOLE = registerENSW("planks_hole", TextureAtlases.ISO_GROUNDS, 4, 2, 5, 2, 6, 2, 7, 2);
	public static final Sprite PLANKS = registerENSW("planks", TextureAtlases.ISO_GROUNDS, 0, 3, 1, 3, 2, 3, 3, 3);
	public static final Sprite STONE_CORNER = registerENSW("stone_corner", TextureAtlases.ISO_GROUNDS, 4, 3, 5, 3, 6, 3, 7, 3);
	public static final Sprite STONE_INSET = registerENSW("stone_inset", TextureAtlases.ISO_GROUNDS, 0, 4, 1, 4, 2, 4, 3, 4);
	public static final Sprite STONE_LEFT = registerENSW("stone_left", TextureAtlases.ISO_GROUNDS, 4, 4, 5, 4, 6, 4, 7, 4);
	public static final Sprite STONE_MISSING_TILES = registerENSW("stone_missing_tiles", TextureAtlases.ISO_GROUNDS, 0, 5, 1, 5, 2, 5, 3, 5);
	public static final Sprite STONE_RIGHT = registerENSW("stone_right", TextureAtlases.ISO_GROUNDS, 4, 5, 5, 5, 6, 5, 7, 5);
	public static final Sprite STONE_SIDE_UNEVEN = registerENSW("stone_side_uneven", TextureAtlases.ISO_GROUNDS, 0, 6, 1, 6, 2, 6, 3, 6);
	public static final Sprite STONE_SIDE = registerENSW("stone_side", TextureAtlases.ISO_GROUNDS, 4, 6, 5, 6, 6, 6, 7, 6);
	public static final Sprite STONE_STEP = registerENSW("stone_step", TextureAtlases.ISO_GROUNDS, 0, 7, 1, 7, 2, 7, 3, 7);
	public static final Sprite STONE_STEPS = registerENSW("stone_steps", TextureAtlases.ISO_GROUNDS, 4, 7, 5, 7, 6, 7, 7, 7);
	public static final Sprite STONE_TILE = registerENSW("stone_tile", TextureAtlases.ISO_GROUNDS, 0, 8, 1, 8, 2, 8, 3, 8);
	public static final Sprite STONE_UNEVEN = registerENSW("stone_uneven", TextureAtlases.ISO_GROUNDS, 4, 8, 5, 8, 6, 8, 7, 8);
	public static final Sprite STONE = registerENSW("stone", TextureAtlases.ISO_GROUNDS, 0, 9, 1, 9, 2, 9, 3, 9);

	public static final Sprite BARREL = registerENSW("barrel", TextureAtlases.ISO_OBJECTS, 0, 0, 1, 0, 2, 0, 3, 0);
	public static final Sprite BARRELS_STACKED = registerENSW("barrels_stacked", TextureAtlases.ISO_OBJECTS, 4, 0, 5, 0, 6, 0, 0, 1);
	public static final Sprite BARRELS = registerENSW("barrels", TextureAtlases.ISO_OBJECTS, 1, 1, 2, 1, 3, 1, 4, 1);
	public static final Sprite CHAIR = registerENSW("chair", TextureAtlases.ISO_OBJECTS, 5, 1, 6, 1, 0, 2, 1, 2);
	public static final Sprite CHEST_CLOSED = registerENSW("chest_closed", TextureAtlases.ISO_OBJECTS, 2, 2, 3, 2, 4, 2, 5, 2);
	public static final Sprite CHEST_OPEN = registerENSW("chest_open", TextureAtlases.ISO_OBJECTS, 6, 2, 0, 3, 1, 3, 2, 3);
	public static final Sprite TABLE_CHAIRS_BROKEN = registerENSW("table_chairs_broken", TextureAtlases.ISO_OBJECTS, 3, 3, 4, 3, 5, 3, 6, 3);
	public static final Sprite TABLE_ROUND_CHAIRS = registerENSW("table_round_chairs", TextureAtlases.ISO_OBJECTS, 0, 4, 1, 4, 2, 4, 3, 4);
	public static final Sprite TABLE_ROUND_ITEMS_CHAIRS = registerENSW("table_round_items_chairs", TextureAtlases.ISO_OBJECTS, 4, 4, 5, 4, 6, 4, 0, 5);
	public static final Sprite TABLE_ROUND = registerENSW("table_round", TextureAtlases.ISO_OBJECTS, 1, 5, 2, 5, 3, 5, 4, 5);
	public static final Sprite TABLE_SHORT_CHAIRS = registerENSW("table_short_chairs", TextureAtlases.ISO_OBJECTS, 5, 5, 6, 5, 0, 6, 1, 6);
	public static final Sprite TABLE_SHORT = registerENSW("table_short", TextureAtlases.ISO_OBJECTS, 2, 6, 3, 6, 4, 6, 5, 6);
	public static final Sprite WOODEN_CRATE = registerENSW("wooden_crate", TextureAtlases.ISO_OBJECTS, 6, 6, 0, 7, 1, 7, 2, 7);
	public static final Sprite WOODEN_CRATES = registerENSW("wooden_crates", TextureAtlases.ISO_OBJECTS, 3, 7, 4, 7, 5, 7, 6, 7);
	public static final Sprite WOODEN_PILE = registerENSW("wooden_pile", TextureAtlases.ISO_OBJECTS, 0, 8, 1, 8, 2, 8, 3, 8);

	public static final Sprite STAIRS_AGED = registerENSW("stairs_aged", TextureAtlases.ISO_STAIRS, 0, 0, 1, 0, 2, 0, 3, 0);
	public static final Sprite STAIRS_CORNER = registerENSW("stairs_corner", TextureAtlases.ISO_STAIRS, 0, 1, 1, 1, 2, 1, 3, 1);
	public static final Sprite STAIRS_SPIRAL = registerENSW("stairs_spiral", TextureAtlases.ISO_STAIRS, 0, 2, 1, 2, 2, 2, 3, 2);
	public static final Sprite STAIRS = registerENSW("stairs", TextureAtlases.ISO_STAIRS, 0, 3, 1, 3, 2, 3, 3, 3);

	public static final Sprite STONE_WALL_AGED_LEFT = registerENSW("stone_wall_aged_left", TextureAtlases.ISO_WALLS, 0, 0, 1, 0, 2, 0, 3, 0);
	public static final Sprite STONE_WALL_AGED_RIGHT = registerENSW("stone_wall_aged_right", TextureAtlases.ISO_WALLS, 4, 0, 5, 0, 6, 0, 7, 0);
	public static final Sprite STONE_WALL_AGED = registerENSW("stone_wall_aged", TextureAtlases.ISO_WALLS, 8, 0, 9, 0, 0, 1, 1, 1);
	public static final Sprite STONE_WALL_ARCHWAYS = registerENSW("stone_wall_archways", TextureAtlases.ISO_WALLS, 2, 1, 3, 1, 4, 1, 5, 1);
	public static final Sprite STONE_WALL_BROKEN_LEFT = registerENSW("stone_wall_broken_left", TextureAtlases.ISO_WALLS, 6, 1, 7, 1, 8, 1, 9, 1);
	public static final Sprite STONE_WALL_BROKEN_RIGHT = registerENSW("stone_wall_broken_right", TextureAtlases.ISO_WALLS, 0, 2, 1, 2, 2, 2, 3, 2);
	public static final Sprite STONE_WALL_BROKEN = registerENSW("stone_wall_broken", TextureAtlases.ISO_WALLS, 4, 2, 5, 2, 6, 2, 7, 2);
	public static final Sprite STONE_WALL_COLUMN_IN = registerENSW("stone_wall_column_in", TextureAtlases.ISO_WALLS, 8, 2, 9, 2, 0, 3, 1, 3);
	public static final Sprite STONE_WALL_COLUMN = registerENSW("stone_wall_column", TextureAtlases.ISO_WALLS, 2, 3, 3, 3, 4, 3, 5, 3);
	public static final Sprite STONE_WALL_CORNER = registerENSW("stone_wall_corner", TextureAtlases.ISO_WALLS, 6, 3, 7, 3, 8, 3, 9, 3);
	public static final Sprite STONE_WALL_DOOR_BARS = registerENSW("stone_wall_door_bars", TextureAtlases.ISO_WALLS, 0, 4, 1, 4, 2, 4, 3, 4);
	public static final Sprite STONE_WALL_DOOR_CLOSED = registerENSW("stone_wall_door_closed", TextureAtlases.ISO_WALLS, 4, 4, 5, 4, 6, 4, 7, 4);
	public static final Sprite STONE_WALL_DOOR_OPEN = registerENSW("stone_wall_door_open", TextureAtlases.ISO_WALLS, 8, 4, 9, 4, 0, 5, 1, 5);
	public static final Sprite STONE_WALL_DOOR = registerENSW("stone_wall_door", TextureAtlases.ISO_WALLS, 2, 5, 3, 5, 4, 5, 5, 5);
	public static final Sprite STONE_WALL_GATE_BARS = registerENSW("stone_wall_gate_bars", TextureAtlases.ISO_WALLS, 6, 5, 7, 5, 8, 5, 9, 5);
	public static final Sprite STONE_WALL_GATE_CLOSED = registerENSW("stone_wall_gate_closed", TextureAtlases.ISO_WALLS, 0, 6, 1, 6, 2, 6, 3, 6);
	public static final Sprite STONE_WALL_GATE_OPEN = registerENSW("stone_wall_gate_open", TextureAtlases.ISO_WALLS, 4, 6, 5, 6, 6, 6, 7, 6);
	public static final Sprite STONE_WALL_GATE = registerENSW("stone_wall_gate", TextureAtlases.ISO_WALLS, 8, 6, 9, 6, 0, 7, 1, 7);
	public static final Sprite STONE_WALL_HALF = registerENSW("stone_wall_half", TextureAtlases.ISO_WALLS, 2, 7, 3, 7, 4, 7, 5, 7);
	public static final Sprite STONE_WALL_HOLE = registerENSW("stone_wall_hole", TextureAtlases.ISO_WALLS, 6, 7, 7, 7, 8, 7, 9, 7);
	public static final Sprite STONE_WALL_ROUND_BROKEN = registerENSW("stone_wall_round_broken", TextureAtlases.ISO_WALLS, 0, 8, 1, 8, 2, 8, 3, 8);
	public static final Sprite STONE_WALL_ROUND_WINDOW = registerENSW("stone_wall_round_window", TextureAtlases.ISO_WALLS, 4, 8, 5, 8, 6, 8, 7, 8);
	public static final Sprite STONE_WALL_ROUND = registerENSW("stone_wall_round", TextureAtlases.ISO_WALLS, 8, 8, 9, 8, 0, 9, 1, 9);
	public static final Sprite STONE_WALL_STRUCTURE = registerENSW("stone_wall_structure", TextureAtlases.ISO_WALLS, 2, 9, 3, 9, 4, 9, 5, 9);
	public static final Sprite STONE_WALL_TOP = registerENSW("stone_wall_top", TextureAtlases.ISO_WALLS, 6, 9, 7, 9, 8, 9, 9, 9);
	public static final Sprite STONE_WALL_WINDOW_BARS = registerENSW("stone_wall_window_bars", TextureAtlases.ISO_WALLS, 0, 10, 1, 10, 2, 10, 3, 10);
	public static final Sprite STONE_WALL_WINDOW = registerENSW("stone_wall_window", TextureAtlases.ISO_WALLS, 4, 10, 5, 10, 6, 10, 7, 10);
	public static final Sprite STONE_WALL = registerENSW("stone_wall", TextureAtlases.ISO_WALLS, 8, 10, 9, 10, 0, 11, 1, 11);

	private static Sprite registerENSW(String name, TextureAtlas atlas, int ex, int ey, int nx, int ny, int sx, int sy, int wx, int wy) {
		Sprite r = new OrientedSprite.Builder(atlas)
				.east(ex, ey).north(nx, ny).south(sx, sy).west(wx, wy).build();
		REGISTRY.put(name, r);
		return r;
	}

}
