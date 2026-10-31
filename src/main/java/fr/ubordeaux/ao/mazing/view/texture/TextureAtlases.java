package fr.ubordeaux.ao.mazing.view.texture;

import java.util.HashMap;

/**
 * Represents TextureAtlases in the rendering system.
 */
public final class TextureAtlases {

    /** Creates an instance of TextureAtlases. */
    public TextureAtlases() { }
	/**
	 * Performs the operation operation.
	 */
	public static final HashMap<String, TextureAtlas> REGISTRY = new HashMap<>();

	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas ISO_CONSTRUCTIONS = register("constructions", TextureAtlas.stitchFromResourceDir("/Dungeon-Pack/Isometric/constructions/png/", 0, -448));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas ISO_GROUNDS = register("grounds", TextureAtlas.stitchFromResourceDir("/Dungeon-Pack/Isometric/grounds/png/", 0, -448));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas ISO_OBJECTS = register("objects", TextureAtlas.stitchFromResourceDir("/Dungeon-Pack/Isometric/objects/png/", 0, -448));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas ISO_STAIRS = register("stairs", TextureAtlas.stitchFromResourceDir("/Dungeon-Pack/Isometric/stairs/png/", 0, -448));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas ISO_WALLS = register("walls", TextureAtlas.stitchFromResourceDir("/Dungeon-Pack/Isometric/walls/png/", 0, -448));

	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas SPIDER_ATTACK = register("spider-attack", TextureAtlas.stitchFromResourceDir("/bw_spider/attack/", -5, -130));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas SPIDER_ATTACK_SHADOW = register("spider-attack-shadow", TextureAtlas.stitchFromResourceDir("/bw_spider/attack/_shadows/", -5, -130));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas SPIDER_DEATH = register("spider-death", TextureAtlas.stitchFromResourceDir("/bw_spider/death/", -5, -130));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas SPIDER_DEATH_SHADOW = register("spider-death-shadow", TextureAtlas.stitchFromResourceDir("/bw_spider/death/_shadows/", -5, -130));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas SPIDER_WALK = register("spider-walk", TextureAtlas.stitchFromResourceDir("/bw_spider/walk/", -5, -130));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas SPIDER_WALK_SHADOW = register("spider-walk-shadow", TextureAtlas.stitchFromResourceDir("/bw_spider/walk/_shadows/", -5, -130));

	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_ATTACK = register("crusader-attack", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/attack/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_ATTACK_SHADOW = register("crusader-attack-shadow", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/attack/_shadows/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_BLOCK = register("crusader-block", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/block/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_BLOCK_SHADOW = register("crusader-block-shadow", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/block/_shadows/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_DEATH = register("crusader-death", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/death/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_DEATH_SHADOW = register("crusader-death-shadow", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/death/_shadows/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_HIT = register("crusader-hit", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/gothit/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_HIT_SHADOW = register("crusader-hit-shadow", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/gothit/_shadows/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_IDLE = register("crusader-idle", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/idle/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_IDLE_SHADOW = register("crusader-idle-shadow", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/idle/_shadows/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_JUMP = register("crusader-jump", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/jump/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_JUMP_SHADOW = register("crusader-jump-shadow", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/jump/_shadows/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_RUN = register("crusader-run", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/run/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_RUN_SHADOW = register("crusader-run-shadow", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/run/_shadows/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_WALK = register("crusader-walk", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/walk/", -10, -180));
	/**
	 * Performs the stitchFromResourceDir operation.
	 */
	public static final TextureAtlas CRUSADER_WALK_SHADOW = register("crusader-walk-shadow", TextureAtlas.stitchFromResourceDir("/isometric-Mini-Crusader/walk/_shadows/", -10, -180));



	private static TextureAtlas register(String name, TextureAtlas atlas) {
		REGISTRY.put(name, atlas);
		return atlas;
	}


	/**
	 * Performs the writeAllToDisk operation.
	 * @param dir dir value
	 */
	public static void writeAllToDisk(String dir) {
		REGISTRY.forEach((name, atlas) -> {
			try {
				atlas.writeToDisk(dir, name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		System.out.println("Atlases written to disk.");

	}
}
