package fr.ubordeaux.ao.mazing.view.sprite;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.texture.TextureAtlas;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a sprite whose texture depends on its orientation.
 * Each orientation refers to a texture position in an atlas.
 */
public class OrientedSprite implements Sprite {
	private final Orientation defaultOrientation;
	private final TextureAtlas atlas;
	private final Map<Orientation, Point> textureIndexes;

	/**
	 * Creates an oriented sprite using the default orientation.
	 * @param atlas texture atlas for rendering
	 * @param textureIndexes atlas coordinates for each orientation
	 */
	public OrientedSprite(TextureAtlas atlas, Map<Orientation, Point> textureIndexes) {
		this.defaultOrientation = Orientation.NORTH;
		this.atlas = atlas;
		this.textureIndexes = Map.copyOf(textureIndexes);
	}

	/**
	 * Creates an oriented sprite from an atlas.
	 * @param defaultOrientation orientation used by default
	 * @param atlas texture atlas for rendering
	 * @param textureIndexes atlas coordinates for each orientation
	 */
	public OrientedSprite(Orientation defaultOrientation, TextureAtlas atlas, Map<Orientation, Point> textureIndexes) {
		this.defaultOrientation = defaultOrientation;
		this.atlas = atlas;
		this.textureIndexes = Map.copyOf(textureIndexes);
	}

	@Override
	public void render(Graphics2D g, int x, int y, Orientation orientation, int animationIndex, int zoom) {
		Point texturePosition = this.textureIndexes.get(orientation);
		if (texturePosition == null) {
			// Fall back to the corresponding cardinal orientation.
			texturePosition = this.textureIndexes.get(orientation.asSimple());
		}
		atlas.blit(g, x, y, texturePosition.x(), texturePosition.y(), zoom);
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
		return new java.awt.Rectangle(
				screenX + atlas.getXOffset() / zoom,
				screenY + atlas.getYOffset() / zoom,
				Math.max(1, atlas.getTextureWidth() / zoom),
				Math.max(1, atlas.getTextureHeight() / zoom));
	}

	@Override
	public int animationSize() {
		return 0;
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
	 * Represents Builder in the rendering system.
	 */
	public static class Builder {
		private final Orientation orientation;
		private final TextureAtlas atlas;
		private final Map<Orientation, Point> textureIndexes;

		/**
		 * Creates a Builder instance.
		 * @param atlas atlas value
		 * @param orientation orientation of the sprite
		 */
		public Builder(TextureAtlas atlas, Orientation orientation) {
			this.orientation = orientation;
			this.atlas = atlas;
			this.textureIndexes = new HashMap<>();
		}

		/**
		 * Creates a Builder instance.
		 * @param atlas atlas value
		 */
		public Builder(TextureAtlas atlas) {
			this.orientation = Orientation.NORTH;
			this.atlas = atlas;
			this.textureIndexes = new HashMap<>();
		}

		/**
		 * Performs the north operation.
		 * @param x horizontal coordinate
		 * @param y vertical coordinate
		 * @return the resulting value
		 */
		public Builder north(int x, int y) {
			textureIndexes.put(Orientation.NORTH, new Point(x, y));
			return this;
		}

		/**
		 * Performs the south operation.
		 * @param x horizontal coordinate
		 * @param y vertical coordinate
		 * @return the resulting value
		 */
		public Builder south(int x, int y) {
			textureIndexes.put(Orientation.SOUTH, new Point(x, y));
			return this;
		}

		/**
		 * Performs the east operation.
		 * @param x horizontal coordinate
		 * @param y vertical coordinate
		 * @return the resulting value
		 */
		public Builder east(int x, int y) {
			textureIndexes.put(Orientation.EAST, new Point(x, y));
			return this;
		}

		/**
		 * Performs the west operation.
		 * @param x horizontal coordinate
		 * @param y vertical coordinate
		 * @return the resulting value
		 */
		public Builder west(int x, int y) {
			textureIndexes.put(Orientation.WEST, new Point(x, y));
			return this;
		}

		/**
		 * Performs the northEast operation.
		 * @param x horizontal coordinate
		 * @param y vertical coordinate
		 * @return the resulting value
		 */
		public Builder northEast(int x, int y) {
			textureIndexes.put(Orientation.NORTH_EAST, new Point(x, y));
			return this;
		}

		/**
		 * Performs the northWest operation.
		 * @param x horizontal coordinate
		 * @param y vertical coordinate
		 * @return the resulting value
		 */
		public Builder northWest(int x, int y) {
			textureIndexes.put(Orientation.NORTH_WEST, new Point(x, y));
			return this;
		}

		/**
		 * Performs the southEast operation.
		 * @param x horizontal coordinate
		 * @param y vertical coordinate
		 * @return the resulting value
		 */
		public Builder southEast(int x, int y) {
			textureIndexes.put(Orientation.SOUTH_EAST, new Point(x, y));
			return this;
		}

		/**
		 * Performs the southWest operation.
		 * @param x horizontal coordinate
		 * @param y vertical coordinate
		 * @return the resulting value
		 */
		public Builder southWest(int x, int y) {
			textureIndexes.put(Orientation.SOUTH_WEST, new Point(x, y));
			return this;
		}

		/**
		 * Performs the build operation.
		 * @return the resulting value
		 */
		public OrientedSprite build() {
			return new OrientedSprite(orientation, atlas, textureIndexes);
		}
	}
}
