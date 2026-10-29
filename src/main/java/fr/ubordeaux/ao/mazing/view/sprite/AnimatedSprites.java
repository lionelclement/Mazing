package fr.ubordeaux.ao.mazing.view.sprite;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.texture.TextureAtlases;
import fr.ubordeaux.ao.mazing.view.texture.TextureAtlas;

import java.util.HashMap;
import java.util.List;

/**
 * Provides predefined animated sprites for characters and creatures.
 * Animation frames are registered according to their atlas layout.
 */
public final class AnimatedSprites {

    // The eight numbered animation groups use different directions in each pack.
    // These lists map each atlas group to its actual facing direction.
    // Crusader: 0=SE, 1=E, 2=NE, 3=N, 4=NW, 5=W, 6=SW, 7=S.
    // These same frame indexes are used for the matching shadow atlas.
    private static final List<Orientation> CRUSADER_ATLAS_DIRECTIONS = List.of(
            Orientation.SOUTH_EAST, Orientation.EAST, Orientation.NORTH_EAST,
            Orientation.NORTH, Orientation.NORTH_WEST, Orientation.WEST,
            Orientation.SOUTH_WEST, Orientation.SOUTH);

    // Spider: 0=NE, 1=N, 2=NW, 3=W, 4=SW, 5=S, 6=SE, 7=E.
    // This mapping already matches the source images.
    private static final List<Orientation> SPIDER_ATLAS_DIRECTIONS = List.of(
            Orientation.NORTH_EAST, Orientation.NORTH, Orientation.NORTH_WEST,
            Orientation.WEST, Orientation.SOUTH_WEST, Orientation.SOUTH,
            Orientation.SOUTH_EAST, Orientation.EAST);

	private static final HashMap<String, AnimatedOrientedSprite> registry = new HashMap<>();
	public static final java.util.Map<String, AnimatedOrientedSprite> REGISTRY = java.util.Collections
			.unmodifiableMap(registry);

	public static final AnimatedOrientedSprite CRUSADER_ATTACK = registerCrusaderAnimationWithShadow("crusader_attack",
			TextureAtlases.CRUSADER_ATTACK,
			TextureAtlases.CRUSADER_ATTACK_SHADOW, 18, 12);

	public static final AnimatedOrientedSprite CRUSADER_BLOCK = registerCrusaderAnimationWithShadow("crusader_block",
			TextureAtlases.CRUSADER_BLOCK,
			TextureAtlases.CRUSADER_BLOCK_SHADOW, 16, 11);

	public static final AnimatedOrientedSprite CRUSADER_DEATH = registerCrusaderAnimationWithShadow("crusader_death",
			TextureAtlases.CRUSADER_DEATH,
			TextureAtlases.CRUSADER_DEATH_SHADOW, 9, 8);

	public static final AnimatedOrientedSprite CRUSADER_HIT = registerCrusaderAnimationWithShadow("crusader_hit",
			TextureAtlases.CRUSADER_HIT,
			TextureAtlases.CRUSADER_HIT_SHADOW, 13, 10);

	public static final AnimatedOrientedSprite CRUSADER_IDLE = registerCrusaderAnimationWithShadow("crusader_idle",
			TextureAtlases.CRUSADER_IDLE,
			TextureAtlases.CRUSADER_IDLE_SHADOW, 16, 11);

	public static final AnimatedOrientedSprite CRUSADER_JUMP = registerCrusaderAnimationWithShadow("crusader_jump",
			TextureAtlases.CRUSADER_JUMP,
			TextureAtlases.CRUSADER_JUMP_SHADOW, 16, 11);

	public static final AnimatedOrientedSprite CRUSADER_RUN = registerCrusaderAnimationWithShadow("crusader_run",
			TextureAtlases.CRUSADER_RUN,
			TextureAtlases.CRUSADER_RUN_SHADOW, 17, 11);

	public static final AnimatedOrientedSprite CRUSADER_WALK = registerCrusaderAnimationWithShadow("crusader_walk",
			TextureAtlases.CRUSADER_WALK,
			TextureAtlases.CRUSADER_WALK_SHADOW, 15, 10);

	public static final AnimatedOrientedSprite SPIDER_ATTACK = registerSpiderAnimationWithShadow("spider_attack", TextureAtlases.SPIDER_ATTACK, TextureAtlases.SPIDER_ATTACK_SHADOW, 8, 8);
	public static final AnimatedOrientedSprite SPIDER_DEATH = registerSpiderAnimationWithShadow("spider_death", TextureAtlases.SPIDER_DEATH, TextureAtlases.SPIDER_DEATH_SHADOW, 8, 8);
	public static final AnimatedOrientedSprite SPIDER_WALK = registerSpiderAnimationWithShadow("spider_walk", TextureAtlases.SPIDER_WALK, TextureAtlases.SPIDER_WALK_SHADOW, 25, 14);

	private static AnimatedOrientedSprite.Builder configureAnimationFrames(AnimatedOrientedSprite.Builder spriteBuilder, int frameCount,
			int framesPerAtlasRow, List<Orientation> atlasDirections) {
		spriteBuilder.animationSize(frameCount);
		int atlasColumn = 0;
		int atlasRow = 0;

		for (Orientation orientation : atlasDirections) {
			int[] frameCoordinates = new int[frameCount * 2];
			for (int coordinateIndex = 0; coordinateIndex < frameCount * 2; coordinateIndex += 2) {
				frameCoordinates[coordinateIndex] = atlasColumn;
				frameCoordinates[coordinateIndex + 1] = atlasRow;
				atlasColumn++;
				if (atlasColumn == framesPerAtlasRow) {
					atlasColumn = 0;
					atlasRow++;
				}
			}
			spriteBuilder.inOrientation(orientation, frameCoordinates);
		}

		return spriteBuilder;
	}

	private static AnimatedOrientedSprite register(String name, AnimatedOrientedSprite sprite) {
		registry.put(name, sprite);
		return sprite;
	}

    private static AnimatedOrientedSprite registerCrusaderAnimationWithShadow(
            String name, TextureAtlas characterAtlas, TextureAtlas shadowAtlas,
            int frameCount, int framesPerAtlasRow) {
        return registerAnimationWithShadow(name, characterAtlas, shadowAtlas,
                frameCount, framesPerAtlasRow, CRUSADER_ATLAS_DIRECTIONS);
    }

    private static AnimatedOrientedSprite registerSpiderAnimationWithShadow(
            String name, TextureAtlas characterAtlas, TextureAtlas shadowAtlas,
            int frameCount, int framesPerAtlasRow) {
        return registerAnimationWithShadow(name, characterAtlas, shadowAtlas,
                frameCount, framesPerAtlasRow, SPIDER_ATLAS_DIRECTIONS);
    }

	private static AnimatedOrientedSprite registerAnimationWithShadow(
			String name,
			TextureAtlas characterAtlas,
			TextureAtlas shadowAtlas,
			int frameCount,
			int framesPerAtlasRow, List<Orientation> atlasDirections) {

		AnimatedOrientedSprite.Builder spriteBuilder = new AnimatedOrientedSprite.Builder(characterAtlas)
				.withShadowAtlas(shadowAtlas);

		AnimatedOrientedSprite sprite = configureAnimationFrames(
				spriteBuilder, frameCount, framesPerAtlasRow, atlasDirections).build();

		return register(name, sprite);
	}
}
