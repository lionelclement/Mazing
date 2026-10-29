package fr.ubordeaux.ao.mazing.view.sprite;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.texture.TextureAtlas;
import fr.ubordeaux.ao.mazing.view.texture.TextureAtlases;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Provides predefined isometric sprites for terrain, objects,
 * structures, walls, and stairs.
 */
public final class IsometricSprites {

	private static final HashMap<String, Sprite> registry = new HashMap<>();
	private static final Set<Sprite> objectSprites =
			Collections.newSetFromMap(new IdentityHashMap<>());
	private static final Set<Sprite> wallSprites =
			Collections.newSetFromMap(new IdentityHashMap<>());
	/** Associates a corner sprite with the matching family of straight walls. */
	private static final Map<Sprite, Sprite> cornerWallFamilies = new IdentityHashMap<>();

	/** One independently rendered, independently occluding face of a corner. */
	public record CornerFace(Sprite sprite, Orientation orientation) { }

	/**
	 * Registers a family whose E/W PNG corners are assembled from straight walls.
	 * Additional wall families can register here without changing the renderer.
	 */
	public static void registerWallCorner(Sprite corner, Sprite straightWall) {
		cornerWallFamilies.put(Objects.requireNonNull(corner),
				Objects.requireNonNull(straightWall));
	}

	/**
	 * Corner artwork naming is opposite the actual tile side. N and S keep
	 * their single PNG; E becomes E then N (WEST then SOUTH in world axes),
	 * and W becomes S then W (NORTH then EAST in world axes).
	 * An empty list means that the original corner PNG must be used unchanged.
	 */
	public static List<CornerFace> cornerFaces(Sprite sprite,
			Orientation worldCorner) {
		Sprite wall = cornerWallFamilies.get(sprite);
		if (wall == null) {
			return List.of();
		}
		return switch (worldCorner) {
			case SOUTH_WEST -> List.of(
					new CornerFace(wall, Orientation.WEST),
					new CornerFace(wall, Orientation.SOUTH));
			case NORTH_EAST -> List.of(
					new CornerFace(wall, Orientation.NORTH),
					new CornerFace(wall, Orientation.EAST));
			default -> List.of();
		};
	}

	/** Whether a sprite belongs to the wall atlas, not the column atlas. */
	public static boolean isWallSprite(Sprite sprite) {
		return wallSprites.contains(sprite);
	}

	/** Sprites whose PNG already contains the object's ground shadow. */
	public static boolean hasEmbeddedShadow(Sprite sprite) {
		return objectSprites.contains(sprite);
	}
	public static final Map<String, Sprite> REGISTRY = Collections.unmodifiableMap(registry);

	public static final Sprite STONE_COLUMN_WOOD = buildCardinalOrientedSprite("stone_column_wood", TextureAtlases.ISO_CONSTRUCTIONS, 0, 0, 1, 0, 2, 0, 3, 0);
	public static final Sprite STONE_COLUMN = buildCardinalOrientedSprite("stone_column", TextureAtlases.ISO_CONSTRUCTIONS, 0, 1, 1, 1, 2, 1, 3, 1);
	public static final Sprite WOODEN_SUPPORT_BEAMS = buildCardinalOrientedSprite("wooden_support_beams", TextureAtlases.ISO_CONSTRUCTIONS, 0, 2, 1, 2, 2, 2, 3, 2);
	public static final Sprite WOODEN_SUPPORTS_BEAM = buildCardinalOrientedSprite("wooden_supports_beam", TextureAtlases.ISO_CONSTRUCTIONS, 0, 3, 1, 3, 2, 3, 3, 3);
	public static final Sprite WOODEN_SUPPORTS_BLOCK = buildCardinalOrientedSprite("wooden_supports_block", TextureAtlases.ISO_CONSTRUCTIONS, 0, 4, 1, 4, 2, 4, 3, 4);
	public static final Sprite WOODEN_SUPPORTS = buildCardinalOrientedSprite("wooden_supports", TextureAtlases.ISO_CONSTRUCTIONS, 0, 5, 1, 5, 2, 5, 3, 5);

	public static final Sprite BRIDGE_BROKEN = buildCardinalOrientedSprite("bridge_broken", TextureAtlases.ISO_GROUNDS, 0, 0, 1, 0, 2, 0, 3, 0);
	public static final Sprite BRIDGE = buildCardinalOrientedSprite("bridge", TextureAtlases.ISO_GROUNDS, 4, 0, 5, 0, 6, 0, 7, 0);
	public static final Sprite DIRT_TILES = buildCardinalOrientedSprite("dirt_tiles", TextureAtlases.ISO_GROUNDS, 0, 1, 1, 1, 2, 1, 3, 1);
	public static final Sprite DIRT = buildCardinalOrientedSprite("dirt", TextureAtlases.ISO_GROUNDS, 4, 1, 5, 1, 6, 1, 7, 1);
	public static final Sprite PLANKS_BROKEN = buildCardinalOrientedSprite("planks_broken", TextureAtlases.ISO_GROUNDS, 0, 2, 1, 2, 2, 2, 3, 2);
	public static final Sprite PLANKS_HOLE = buildCardinalOrientedSprite("planks_hole", TextureAtlases.ISO_GROUNDS, 4, 2, 5, 2, 6, 2, 7, 2);
	public static final Sprite PLANKS = buildCardinalOrientedSprite("planks", TextureAtlases.ISO_GROUNDS, 0, 3, 1, 3, 2, 3, 3, 3);
	public static final Sprite STONE_CORNER = buildCardinalOrientedSprite("stone_corner", TextureAtlases.ISO_GROUNDS, 4, 3, 5, 3, 6, 3, 7, 3);
	public static final Sprite STONE_INSET = buildCardinalOrientedSprite("stone_inset", TextureAtlases.ISO_GROUNDS, 0, 4, 1, 4, 2, 4, 3, 4);
	public static final Sprite STONE_LEFT = buildCardinalOrientedSprite("stone_left", TextureAtlases.ISO_GROUNDS, 4, 4, 5, 4, 6, 4, 7, 4);
	public static final Sprite STONE_MISSING_TILES = buildCardinalOrientedSprite("stone_missing_tiles", TextureAtlases.ISO_GROUNDS, 0, 5, 1, 5, 2, 5, 3, 5);
	public static final Sprite STONE_RIGHT = buildCardinalOrientedSprite("stone_right", TextureAtlases.ISO_GROUNDS, 4, 5, 5, 5, 6, 5, 7, 5);
	public static final Sprite STONE_SIDE_UNEVEN = buildCardinalOrientedSprite("stone_side_uneven", TextureAtlases.ISO_GROUNDS, 0, 6, 1, 6, 2, 6, 3, 6);
	public static final Sprite STONE_SIDE = buildCardinalOrientedSprite("stone_side", TextureAtlases.ISO_GROUNDS, 4, 6, 5, 6, 6, 6, 7, 6);
	public static final Sprite STONE_STEP = buildCardinalOrientedSprite("stone_step", TextureAtlases.ISO_GROUNDS, 0, 7, 1, 7, 2, 7, 3, 7);
	public static final Sprite STONE_STEPS = buildCardinalOrientedSprite("stone_steps", TextureAtlases.ISO_GROUNDS, 4, 7, 5, 7, 6, 7, 7, 7);
	public static final Sprite STONE_TILE = buildCardinalOrientedSprite("stone_tile", TextureAtlases.ISO_GROUNDS, 0, 8, 1, 8, 2, 8, 3, 8);
	public static final Sprite STONE_UNEVEN = buildCardinalOrientedSprite("stone_uneven", TextureAtlases.ISO_GROUNDS, 4, 8, 5, 8, 6, 8, 7, 8);
	public static final Sprite STONE = buildCardinalOrientedSprite("stone", TextureAtlases.ISO_GROUNDS, 0, 9, 1, 9, 2, 9, 3, 9);

	public static final Sprite BARREL = buildCardinalOrientedSprite("barrel", TextureAtlases.ISO_OBJECTS, 0, 0, 1, 0, 2, 0, 3, 0);
	public static final Sprite BARRELS_STACKED = buildCardinalOrientedSprite("barrels_stacked", TextureAtlases.ISO_OBJECTS, 4, 0, 5, 0, 6, 0, 0, 1);
	public static final Sprite BARRELS = buildCardinalOrientedSprite("barrels", TextureAtlases.ISO_OBJECTS, 1, 1, 2, 1, 3, 1, 4, 1);
	public static final Sprite CHAIR = buildCardinalOrientedSprite("chair", TextureAtlases.ISO_OBJECTS, 5, 1, 6, 1, 0, 2, 1, 2);
	public static final Sprite CHEST_CLOSED = buildCardinalOrientedSprite("chest_closed", TextureAtlases.ISO_OBJECTS, 2, 2, 3, 2, 4, 2, 5, 2);
	public static final Sprite CHEST_OPEN = buildCardinalOrientedSprite("chest_open", TextureAtlases.ISO_OBJECTS, 6, 2, 0, 3, 1, 3, 2, 3);
	public static final Sprite TABLE_CHAIRS_BROKEN = buildCardinalOrientedSprite("table_chairs_broken", TextureAtlases.ISO_OBJECTS, 3, 3, 4, 3, 5, 3, 6, 3);
	public static final Sprite TABLE_ROUND_CHAIRS = buildCardinalOrientedSprite("table_round_chairs", TextureAtlases.ISO_OBJECTS, 0, 4, 1, 4, 2, 4, 3, 4);
	public static final Sprite TABLE_ROUND_ITEMS_CHAIRS = buildCardinalOrientedSprite("table_round_items_chairs", TextureAtlases.ISO_OBJECTS, 4, 4, 5, 4, 6, 4, 0, 5);
	public static final Sprite TABLE_ROUND = buildCardinalOrientedSprite("table_round", TextureAtlases.ISO_OBJECTS, 1, 5, 2, 5, 3, 5, 4, 5);
	public static final Sprite TABLE_SHORT_CHAIRS = buildCardinalOrientedSprite("table_short_chairs", TextureAtlases.ISO_OBJECTS, 5, 5, 6, 5, 0, 6, 1, 6);
	public static final Sprite TABLE_SHORT = buildCardinalOrientedSprite("table_short", TextureAtlases.ISO_OBJECTS, 2, 6, 3, 6, 4, 6, 5, 6);
	public static final Sprite WOODEN_CRATE = buildCardinalOrientedSprite("wooden_crate", TextureAtlases.ISO_OBJECTS, 6, 6, 0, 7, 1, 7, 2, 7);
	public static final Sprite WOODEN_CRATES = buildCardinalOrientedSprite("wooden_crates", TextureAtlases.ISO_OBJECTS, 3, 7, 4, 7, 5, 7, 6, 7);
	public static final Sprite WOODEN_PILE = buildCardinalOrientedSprite("wooden_pile", TextureAtlases.ISO_OBJECTS, 0, 8, 1, 8, 2, 8, 3, 8);

	public static final Sprite STAIRS_AGED = ramp("stairs_aged", buildCardinalOrientedSprite("stairs_aged", TextureAtlases.ISO_STAIRS, 0, 0, 1, 0, 2, 0, 3, 0), 0.65);
	public static final Sprite STAIRS_CORNER = buildCardinalOrientedSprite("stairs_corner", TextureAtlases.ISO_STAIRS, 0, 1, 1, 1, 2, 1, 3, 1);
	public static final Sprite STAIRS_SPIRAL = buildCardinalOrientedSprite("stairs_spiral", TextureAtlases.ISO_STAIRS, 0, 2, 1, 2, 2, 2, 3, 2);
	public static final Sprite STAIRS = ramp("stairs", buildCardinalOrientedSprite("stairs", TextureAtlases.ISO_STAIRS, 0, 3, 1, 3, 2, 3, 3, 3), 0.65);

	public static final Sprite STONE_WALL_AGED_LEFT = buildCardinalOrientedSprite("stone_wall_aged_left", TextureAtlases.ISO_WALLS, 0, 0, 1, 0, 2, 0, 3, 0);
	public static final Sprite STONE_WALL_AGED_RIGHT = buildCardinalOrientedSprite("stone_wall_aged_right", TextureAtlases.ISO_WALLS, 4, 0, 5, 0, 6, 0, 7, 0);
	public static final Sprite STONE_WALL_AGED = buildCardinalOrientedSprite("stone_wall_aged", TextureAtlases.ISO_WALLS, 8, 0, 9, 0, 0, 1, 1, 1);
	public static final Sprite STONE_WALL_ARCHWAYS = buildCardinalOrientedSprite("stone_wall_archways", TextureAtlases.ISO_WALLS, 2, 1, 3, 1, 4, 1, 5, 1);
	public static final Sprite STONE_WALL_BROKEN_LEFT = buildCardinalOrientedSprite("stone_wall_broken_left", TextureAtlases.ISO_WALLS, 6, 1, 7, 1, 8, 1, 9, 1);
	public static final Sprite STONE_WALL_BROKEN_RIGHT = buildCardinalOrientedSprite("stone_wall_broken_right", TextureAtlases.ISO_WALLS, 0, 2, 1, 2, 2, 2, 3, 2);
	public static final Sprite STONE_WALL_BROKEN = buildCardinalOrientedSprite("stone_wall_broken", TextureAtlases.ISO_WALLS, 4, 2, 5, 2, 6, 2, 7, 2);
	public static final Sprite STONE_WALL_COLUMN_IN = buildCardinalOrientedSprite("stone_wall_column_in", TextureAtlases.ISO_WALLS, 8, 2, 9, 2, 0, 3, 1, 3);
	public static final Sprite STONE_WALL_COLUMN = buildCardinalOrientedSprite("stone_wall_column", TextureAtlases.ISO_WALLS, 2, 3, 3, 3, 4, 3, 5, 3);
	public static final Sprite STONE_WALL_CORNER = buildDiagonalOrientedSprite("stone_wall_corner", TextureAtlases.ISO_WALLS, 6, 3, 7, 3, 8, 3, 9, 3);
	public static final Sprite STONE_WALL_DOOR_BARS = buildCardinalOrientedSprite("stone_wall_door_bars", TextureAtlases.ISO_WALLS, 0, 4, 1, 4, 2, 4, 3, 4);
	public static final Sprite STONE_WALL_DOOR_CLOSED = buildCardinalOrientedSprite("stone_wall_door_closed", TextureAtlases.ISO_WALLS, 4, 4, 5, 4, 6, 4, 7, 4);
	public static final Sprite STONE_WALL_DOOR_OPEN = buildCardinalOrientedSprite("stone_wall_door_open", TextureAtlases.ISO_WALLS, 8, 4, 9, 4, 0, 5, 1, 5);
	public static final Sprite STONE_WALL_DOOR = buildCardinalOrientedSprite("stone_wall_door", TextureAtlases.ISO_WALLS, 2, 5, 3, 5, 4, 5, 5, 5);
	public static final Sprite STONE_WALL_GATE_BARS = buildCardinalOrientedSprite("stone_wall_gate_bars", TextureAtlases.ISO_WALLS, 6, 5, 7, 5, 8, 5, 9, 5);
	public static final Sprite STONE_WALL_GATE_CLOSED = buildCardinalOrientedSprite("stone_wall_gate_closed", TextureAtlases.ISO_WALLS, 0, 6, 1, 6, 2, 6, 3, 6);
	public static final Sprite STONE_WALL_GATE_OPEN = buildCardinalOrientedSprite("stone_wall_gate_open", TextureAtlases.ISO_WALLS, 4, 6, 5, 6, 6, 6, 7, 6);
	public static final Sprite STONE_WALL_GATE = buildCardinalOrientedSprite("stone_wall_gate", TextureAtlases.ISO_WALLS, 8, 6, 9, 6, 0, 7, 1, 7);
	public static final Sprite STONE_WALL_HALF = buildCardinalOrientedSprite("stone_wall_half", TextureAtlases.ISO_WALLS, 2, 7, 3, 7, 4, 7, 5, 7);
	public static final Sprite STONE_WALL_HOLE = buildCardinalOrientedSprite("stone_wall_hole", TextureAtlases.ISO_WALLS, 6, 7, 7, 7, 8, 7, 9, 7);
	public static final Sprite STONE_WALL_ROUND_BROKEN = buildCardinalOrientedSprite("stone_wall_round_broken", TextureAtlases.ISO_WALLS, 0, 8, 1, 8, 2, 8, 3, 8);
	public static final Sprite STONE_WALL_ROUND_WINDOW = buildCardinalOrientedSprite("stone_wall_round_window", TextureAtlases.ISO_WALLS, 4, 8, 5, 8, 6, 8, 7, 8);
	public static final Sprite STONE_WALL_ROUND = buildCardinalOrientedSprite("stone_wall_round", TextureAtlases.ISO_WALLS, 8, 8, 9, 8, 0, 9, 1, 9);
	public static final Sprite STONE_WALL_STRUCTURE = buildCardinalOrientedSprite("stone_wall_structure", TextureAtlases.ISO_WALLS, 2, 9, 3, 9, 4, 9, 5, 9);
	public static final Sprite STONE_WALL_TOP = buildCardinalOrientedSprite("stone_wall_top", TextureAtlases.ISO_WALLS, 6, 9, 7, 9, 8, 9, 9, 9);
	public static final Sprite STONE_WALL_WINDOW_BARS = buildCardinalOrientedSprite("stone_wall_window_bars", TextureAtlases.ISO_WALLS, 0, 10, 1, 10, 2, 10, 3, 10);
	public static final Sprite STONE_WALL_WINDOW = buildCardinalOrientedSprite("stone_wall_window", TextureAtlases.ISO_WALLS, 4, 10, 5, 10, 6, 10, 7, 10);
	public static final Sprite STONE_WALL = buildCardinalOrientedSprite("stone_wall", TextureAtlases.ISO_WALLS, 8, 10, 9, 10, 0, 11, 1, 11);

	static {
		registerWallCorner(STONE_WALL_CORNER, STONE_WALL);
	}

	/** Assigns a physical ramp to any oriented sprite without changing its image. */
	private static Sprite ramp(String name, Sprite artwork, double rise) {
		Sprite sprite = new RampSprite(artwork, rise);
		registry.put(name, sprite);
		return sprite;
	}

	private static Sprite buildDiagonalOrientedSprite(
			String name, TextureAtlas atlas,
			int northEastX, int northEastY,
			int northWestX, int northWestY,
			int southEastX, int southEastY,
			int southWestX, int southWestY) {
		// The four STONE_WALL_CORNER atlas files (E, N, S, W) sit at the
		// south-west, south-east, north-west and north-east tile corners,
		// respectively. Orientations in the API describe the *world* corner.
		boolean wallCorner = atlas == TextureAtlases.ISO_WALLS;
		Sprite sprite = new OrientedSprite.Builder(atlas)
				.northEast(wallCorner ? southWestX : northEastX,
						wallCorner ? southWestY : northEastY)
				.northWest(wallCorner ? southEastX : northWestX,
						wallCorner ? southEastY : northWestY)
				.southEast(wallCorner ? northWestX : southEastX,
						wallCorner ? northWestY : southEastY)
				.southWest(wallCorner ? northEastX : southWestX,
						wallCorner ? northEastY : southWestY)
				.build();
		registry.put(name, sprite);
		if (atlas == TextureAtlases.ISO_OBJECTS) {
			objectSprites.add(sprite);
		} else if (atlas == TextureAtlases.ISO_WALLS) {
			wallSprites.add(sprite);
		}
		return sprite;
	}

	private static Sprite buildCardinalOrientedSprite(String name, TextureAtlas atlas, int eastColumn, int eastRow, int northColumn, int northRow, int southColumn, int southRow, int westColumn, int westRow) {
		// In the wall/construction artwork, the filename denotes the
		// *opposite* tile side: E.png lies on WEST, N.png on SOUTH,
		// W.png on EAST and S.png on NORTH. The public Orientation always
		// denotes the actual world side (+X=EAST, +Y=NORTH).
		// Do not apply this rule to ground, furniture or stair sprites.
		boolean oppositeSideAtlas = atlas == TextureAtlases.ISO_WALLS
				|| atlas == TextureAtlases.ISO_CONSTRUCTIONS;
		// Stair PNGs point toward their low step in the new world axes.
		// Swap the four pictures so Orientation names the uphill direction.
		// A spiral staircase has no low/high end and keeps its own mapping.
		boolean reverseStairs = atlas == TextureAtlases.ISO_STAIRS
				&& !name.equals("stairs_spiral");
		boolean reverseCardinals = oppositeSideAtlas || reverseStairs;
		Sprite sprite = new OrientedSprite.Builder(atlas)
				.east(reverseCardinals ? westColumn : eastColumn,
						reverseCardinals ? westRow : eastRow)
				.north(reverseCardinals ? southColumn : northColumn,
						reverseCardinals ? southRow : northRow)
				.south(reverseCardinals ? northColumn : southColumn,
						reverseCardinals ? northRow : southRow)
				.west(reverseCardinals ? eastColumn : westColumn,
						reverseCardinals ? eastRow : westRow)
				.build();
		registry.put(name, sprite);
		if (atlas == TextureAtlases.ISO_OBJECTS) {
			objectSprites.add(sprite);
		} else if (atlas == TextureAtlases.ISO_WALLS) {
			wallSprites.add(sprite);
		}
		return sprite;
	}

}
