package fr.ubordeaux.ao.mazing.view.sprite;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.texture.TextureAtlas;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Objects;

/**
 * Represents an oriented sprite with multiple animation frames.
 * Frame selection depends on the requested orientation and frame index.
 * An optional shadow atlas may be rendered before the main texture.
 */
public class AnimatedOrientedSprite implements Sprite {
	private final Orientation defaultOrientation;
	private final TextureAtlas atlas;
	private final Map<Orientation, List<Point>> textureIndexes;
	private final int animationSize;
	private final TextureAtlas shadowAtlas;

	/**
	 * Creates an instance of AnimatedOrientedSprite.
	  * @param defaultOrientation defaultOrientation value
	  * @param atlas texture atlas
	  * @param textureIndexes atlas positions for each orientation
	  * @param animationSize number of animation frames
	 */
	public AnimatedOrientedSprite(
			Orientation defaultOrientation,
			TextureAtlas atlas,
			Map<Orientation, List<Point>> textureIndexes,
			int animationSize) {

		this(defaultOrientation, atlas, null, textureIndexes, animationSize);
	}

	private AnimatedOrientedSprite(
			Orientation defaultOrientation,
			TextureAtlas atlas,
			TextureAtlas shadowAtlas,
			Map<Orientation, List<Point>> textureIndexes,
			int animationSize) {

		this.defaultOrientation = defaultOrientation;
		this.atlas = atlas;
		this.shadowAtlas = shadowAtlas;
		this.textureIndexes = textureIndexes.entrySet().stream()
				.collect(java.util.stream.Collectors.toUnmodifiableMap(
						Map.Entry::getKey,
						entry -> List.copyOf(entry.getValue())));
		this.animationSize = animationSize;
	}

	/**
	 * Documents this public API element.
	  * @param atlas texture atlas
	  * @param textureIndexes atlas positions for each orientation
	  * @param animationSize number of animation frames
	 */
	/// Creates an animated sprite from a texture atlas and orientation-specific frames.
	///
	/// @param atlas      the atlas to use for drawing
	/// @param textureIndexes indexes of the texture to use for the direction, in the
	///                       order of the direction enum
	/// @param animationSize number of animation frames
	public AnimatedOrientedSprite(TextureAtlas atlas, Map<Orientation, List<Point>> textureIndexes, int animationSize) {
		this(Orientation.NORTH, atlas, textureIndexes, animationSize);
	}

	@Override
	public void render(
			Graphics2D graphics,
			int screenX,
			int screenY,
			Orientation orientation,
			int frameIndex,
			int zoom) {

		render(graphics, screenX, screenY, orientation,
				frameIndex, zoom, false);
	}

	/**
	 * Draws the sprite at the specified screen position.
	  * @param graphics graphics context
	  * @param screenX screen-space horizontal position
	  * @param screenY screen-space vertical position
	  * @param orientation orientation of the sprite
	  * @param frameIndex index of the frame to draw
	  * @param zoom zoom factor
	  * @param drawShadow drawShadow value
	 */
	public void render(
			Graphics2D graphics,
			int screenX,
			int screenY,
			Orientation orientation,
			int frameIndex,
			int zoom,
			boolean drawShadow) {

		List<Point> framePositions = textureIndexes.get(orientation);

		if (framePositions == null) {
			framePositions = textureIndexes.get(orientation.asSimple());
		}

		Point framePosition = framePositions.get(frameIndex);

		if (drawShadow) {
			if (shadowAtlas == null) {
				throw new IllegalStateException(
						"No shadow atlas configured for this sprite");
			}

			shadowAtlas.blit(
					graphics, screenX, screenY,
					framePosition.x(), framePosition.y(), zoom);
		}

		atlas.blit(
				graphics, screenX, screenY,
				framePosition.x(), framePosition.y(), zoom);
	}

	/**
	 * Scales around the center of the atlas cell, including its offset.
	 * This keeps the visual anchored to its isometric grid cell.
	 */
	@Override
	public double getScaleAnchorX(int zoom) {
		return (atlas.getXOffset() + atlas.getTextureWidth() / 2.0) / zoom;
	}

	@Override
	public java.awt.Rectangle getScreenBounds(int screenX, int screenY, int zoom) {
		java.awt.Rectangle bounds = new java.awt.Rectangle(
				screenX + atlas.getXOffset() / zoom,
				screenY + atlas.getYOffset() / zoom,
				Math.max(1, atlas.getTextureWidth() / zoom),
				Math.max(1, atlas.getTextureHeight() / zoom));
		if (shadowAtlas != null) {
			bounds.add(new java.awt.Rectangle(
					screenX + shadowAtlas.getXOffset() / zoom,
					screenY + shadowAtlas.getYOffset() / zoom,
					Math.max(1, shadowAtlas.getTextureWidth() / zoom),
					Math.max(1, shadowAtlas.getTextureHeight() / zoom)));
		}
		return bounds;
	}

	@Override
	public int animationSize() {
		return animationSize;
	}

	@Override
	public String toString() {
		return "SimpleOrientedSprite{" +
				"defaultOrientation=" + defaultOrientation +
				", atlas=" + atlas +
				", textureIndexes=" + textureIndexes +
				'}';
	}

	/**
	 * Provides Builder functionality.
	 */
	public static class Builder {
		private final Orientation orientation;
		private final TextureAtlas atlas;
		private final Map<Orientation, List<Point>> textureIndexes;
		private int animationSize;
		private TextureAtlas shadowAtlas;

		/**
		 * Creates an instance of Builder.
		  * @param atlas texture atlas
		  * @param orientation orientation of the sprite
		  * @param animationSize number of animation frames
		 */
		public Builder(TextureAtlas atlas, Orientation orientation, int animationSize) {
			this.orientation = orientation;
			this.atlas = atlas;
			this.textureIndexes = new HashMap<>();
			this.animationSize = animationSize;
		}

		/**
		 * Creates an instance of Builder.
		  * @param atlas texture atlas
		 */
		public Builder(TextureAtlas atlas) {
			this(atlas, Orientation.NORTH, 0);
		}

		/**
		 * Returns the number of animation frames.
		  * @param animationSize number of animation frames
		  * @return the value or object described above
		 */
		public Builder animationSize(int animationSize) {
			this.animationSize = animationSize;
			return this;
		}

		/**
		 * Configures animation frames for this orientation.
		  * @param orientation orientation of the sprite
		  * @param xy pairs of atlas coordinates
		  * @return the value or object described above
		 */
		public Builder inOrientation(Orientation orientation, int... xy) {
			if (xy.length % 2 != 0 || xy.length / 2 != this.animationSize) {
				throw new IllegalArgumentException(
						"xy must have an even number of elements and be two times the number of animation sprites");
			}
			ArrayList<Point> ps = new ArrayList<>();
			for (int i = 0; i < xy.length; i += 2) {
				ps.add(new Point(xy[i], xy[i + 1]));
			}
			textureIndexes.put(orientation, ps);
			return this;
		}

		/**
		 * Configures animation frames for this orientation.
		  * @param xy pairs of atlas coordinates
		  * @return the value or object described above
		 */
		public Builder north(int... xy) {
			return inOrientation(Orientation.NORTH, xy);
		}

		/**
		 * Configures animation frames for this orientation.
		  * @param xy pairs of atlas coordinates
		  * @return the value or object described above
		 */
		public Builder south(int... xy) {
			return inOrientation(Orientation.SOUTH, xy);
		}

		/**
		 * Configures animation frames for this orientation.
		  * @param xy pairs of atlas coordinates
		  * @return the value or object described above
		 */
		public Builder east(int... xy) {
			return inOrientation(Orientation.EAST, xy);
		}

		/**
		 * Configures animation frames for this orientation.
		  * @param xy pairs of atlas coordinates
		  * @return the value or object described above
		 */
		public Builder west(int... xy) {
			return inOrientation(Orientation.WEST, xy);
		}

		/**
		 * Configures animation frames for this orientation.
		  * @param xy pairs of atlas coordinates
		  * @return the value or object described above
		 */
		public Builder northEast(int... xy) {
			return inOrientation(Orientation.NORTH_EAST, xy);
		}

		/**
		 * Configures animation frames for this orientation.
		  * @param xy pairs of atlas coordinates
		  * @return the value or object described above
		 */
		public Builder northWest(int... xy) {
			return inOrientation(Orientation.NORTH_WEST, xy);
		}

		/**
		 * Configures animation frames for this orientation.
		  * @param xy pairs of atlas coordinates
		  * @return the value or object described above
		 */
		public Builder southEast(int... xy) {
			return inOrientation(Orientation.SOUTH_EAST, xy);
		}

		/**
		 * Configures animation frames for this orientation.
		  * @param xy pairs of atlas coordinates
		  * @return the value or object described above
		 */
		public Builder southWest(int... xy) {
			return inOrientation(Orientation.SOUTH_WEST, xy);
		}

		/**
		 * Performs the withShadowAtlas operation.
		  * @param shadowAtlas shadow texture atlas
		  * @return the value or object described above
		 */
		public Builder withShadowAtlas(TextureAtlas shadowAtlas) {
			this.shadowAtlas = Objects.requireNonNull(shadowAtlas);
			return this;
		}

		/**
		 * Builds the configured animated sprite.
		  * @return the value or object described above
		 */
		public AnimatedOrientedSprite build() {
			return new AnimatedOrientedSprite(
					orientation,
					atlas,
					shadowAtlas,
					textureIndexes,
					animationSize);
		}
	}
}
