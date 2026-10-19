package fr.ubordeaux.ao.mazing.view.sprite;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.texture.TextureAtlases;

import java.util.HashMap;
import java.util.List;

public final class AnimatedSprites {

	public static final HashMap<String, AnimatedOrientedSprite> REGISTRY = new HashMap<>();

	public static final AnimatedOrientedSprite CRUSADER_ATTACK = register("crusader_attack", setupSprites(new AnimatedOrientedSprite.Builder(TextureAtlases.CRUSADER_ATTACK), 18, 12).build());
	public static final AnimatedOrientedSprite CRUSADER_BLOCK = register("crusader_block", setupSprites(new AnimatedOrientedSprite.Builder(TextureAtlases.CRUSADER_BLOCK), 16, 11).build());
	public static final AnimatedOrientedSprite CRUSADER_DEATH = register("crusader_death", setupSprites(new AnimatedOrientedSprite.Builder(TextureAtlases.CRUSADER_DEATH), 9, 8).build());
	public static final AnimatedOrientedSprite CRUSADER_HIT = register("crusader_hit", setupSprites(new AnimatedOrientedSprite.Builder(TextureAtlases.CRUSADER_HIT), 13, 10).build());
	public static final AnimatedOrientedSprite CRUSADER_IDLE = register("crusader_idle", setupSprites(new AnimatedOrientedSprite.Builder(TextureAtlases.CRUSADER_IDLE), 16, 11).build());
	public static final AnimatedOrientedSprite CRUSADER_JUMP = register("crusader_jump", setupSprites(new AnimatedOrientedSprite.Builder(TextureAtlases.CRUSADER_JUMP), 16, 11).build());
	public static final AnimatedOrientedSprite CRUSADER_RUN = register("crusader_run", setupSprites(new AnimatedOrientedSprite.Builder(TextureAtlases.CRUSADER_RUN), 17, 11).build());
	public static final AnimatedOrientedSprite CRUSADER_WALK = register("crusader_walk", setupSprites(new AnimatedOrientedSprite.Builder(TextureAtlases.CRUSADER_WALK), 15, 10).build());

	private static AnimatedOrientedSprite.Builder setupSprites(AnimatedOrientedSprite.Builder b, int animSize, int spriteAmountInWidthInAtlas) {
		int x = 0;
		int y = 0;
		for (Orientation o : List.of(Orientation.NORTH_WEST, Orientation.WEST, Orientation.SOUTH_WEST, Orientation.SOUTH, Orientation.SOUTH_EAST, Orientation.EAST, Orientation.NORTH_EAST, Orientation.NORTH)) {
			int[] dirs = new int[animSize * 2];
			for (int i = 0; i < animSize*2; i+=2) {
				dirs[i] = x;
				dirs[i + 1] = y;
				x++;
				if (x == spriteAmountInWidthInAtlas) {
					x = 0;
					y++;
				}
			}
			b.inOrientation(o, dirs);
		}

		return b.animationSize(animSize);
	}

	private static AnimatedOrientedSprite register(String name, AnimatedOrientedSprite sprite) {
		REGISTRY.put(name, sprite);
		return sprite;
	}

}
