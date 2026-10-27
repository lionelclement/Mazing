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

	private static final HashMap<String, AnimatedOrientedSprite> registry = new HashMap<>();
	public static final java.util.Map<String, AnimatedOrientedSprite> REGISTRY = java.util.Collections
			.unmodifiableMap(registry);

	public static final AnimatedOrientedSprite CRUSADER_ATTACK = registerAnimationWithShadow("crusader_attack",
			TextureAtlases.CRUSADER_ATTACK,
			TextureAtlases.CRUSADER_ATTACK_SHADOW, 18, 12);

	public static final AnimatedOrientedSprite CRUSADER_BLOCK = registerAnimationWithShadow("crusader_block",
			TextureAtlases.CRUSADER_BLOCK,
			TextureAtlases.CRUSADER_BLOCK_SHADOW, 16, 11);

	public static final AnimatedOrientedSprite CRUSADER_DEATH = registerAnimationWithShadow("crusader_death",
			TextureAtlases.CRUSADER_DEATH,
			TextureAtlases.CRUSADER_DEATH_SHADOW, 9, 8);

	public static final AnimatedOrientedSprite CRUSADER_HIT = registerAnimationWithShadow("crusader_hit",
			TextureAtlases.CRUSADER_HIT,
			TextureAtlases.CRUSADER_HIT_SHADOW, 13, 10);

	public static final AnimatedOrientedSprite CRUSADER_IDLE = registerAnimationWithShadow("crusader_idle",
			TextureAtlases.CRUSADER_IDLE,
			TextureAtlases.CRUSADER_IDLE_SHADOW, 16, 11);

	public static final AnimatedOrientedSprite CRUSADER_JUMP = registerAnimationWithShadow("crusader_jump",
			TextureAtlases.CRUSADER_JUMP,
			TextureAtlases.CRUSADER_JUMP_SHADOW, 16, 11);

	public static final AnimatedOrientedSprite CRUSADER_RUN = registerAnimationWithShadow("crusader_run",
			TextureAtlases.CRUSADER_RUN,
			TextureAtlases.CRUSADER_RUN_SHADOW, 17, 11);

	public static final AnimatedOrientedSprite CRUSADER_WALK = registerAnimationWithShadow("crusader_walk",
			TextureAtlases.CRUSADER_WALK,
			TextureAtlases.CRUSADER_WALK_SHADOW, 15, 10);

	public static final AnimatedOrientedSprite SPIDER_ATTACK = registerAnimationWithShadow("spider_attack", TextureAtlases.SPIDER_ATTACK, TextureAtlases.SPIDER_ATTACK_SHADOW, 8, 8);
	public static final AnimatedOrientedSprite SPIDER_DEATH = registerAnimationWithShadow("spider_death", TextureAtlases.SPIDER_DEATH, TextureAtlases.SPIDER_DEATH_SHADOW, 8, 8);
	public static final AnimatedOrientedSprite SPIDER_WALK = registerAnimationWithShadow("spider_walk", TextureAtlases.SPIDER_WALK, TextureAtlases.SPIDER_WALK_SHADOW, 25, 14);

	private static AnimatedOrientedSprite.Builder configureAnimationFrames(AnimatedOrientedSprite.Builder spriteBuilder, int frameCount,
			int framesPerAtlasRow) {
		spriteBuilder.animationSize(frameCount);
		int atlasColumn = 0;
		int atlasRow = 0;

		for (Orientation orientation : List.of(
				Orientation.SOUTH_EAST,
				Orientation.EAST,
				Orientation.NORTH_EAST,
				Orientation.NORTH,
				Orientation.NORTH_WEST,
				Orientation.WEST,
				Orientation.SOUTH_WEST,
				Orientation.SOUTH)) {
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

	private static AnimatedOrientedSprite registerAnimationWithShadow(
			String name,
			TextureAtlas characterAtlas,
			TextureAtlas shadowAtlas,
			int frameCount,
			int framesPerAtlasRow) {

		AnimatedOrientedSprite.Builder spriteBuilder = new AnimatedOrientedSprite.Builder(characterAtlas)
				.withShadowAtlas(shadowAtlas);

		AnimatedOrientedSprite sprite = configureAnimationFrames(
				spriteBuilder, frameCount, framesPerAtlasRow).build();

		return register(name, sprite);
	}
}
