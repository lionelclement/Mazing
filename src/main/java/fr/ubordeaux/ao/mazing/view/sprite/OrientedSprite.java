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

	/// @param atlas          the atlas to use for drawing
	/// @param textureIndexes indexes of the texture to use for the direction, in the order of the direction enum
	public OrientedSprite(TextureAtlas atlas, Map<Orientation, Point> textureIndexes) {
		this.defaultOrientation = Orientation.NORTH;
		this.atlas = atlas;
		this.textureIndexes = Map.copyOf(textureIndexes);
	}

	/**
	 * @param defaultOrientation the default orientation
	 * @param atlas              the atlas to use for drawing
	 * @param textureIndexes     indexes of the texture to use for the direction, in the order of the direction enum
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

	public static class Builder {
		private final Orientation orientation;
		private final TextureAtlas atlas;
		private final Map<Orientation, Point> textureIndexes;

		public Builder(TextureAtlas atlas, Orientation orientation) {
			this.orientation = orientation;
			this.atlas = atlas;
			this.textureIndexes = new HashMap<>();
		}

		public Builder(TextureAtlas atlas) {
			this.orientation = Orientation.NORTH;
			this.atlas = atlas;
			this.textureIndexes = new HashMap<>();
		}

		public Builder north(int x, int y) {
			textureIndexes.put(Orientation.NORTH, new Point(x, y));
			return this;
		}

		public Builder south(int x, int y) {
			textureIndexes.put(Orientation.SOUTH, new Point(x, y));
			return this;
		}

		public Builder east(int x, int y) {
			textureIndexes.put(Orientation.EAST, new Point(x, y));
			return this;
		}

		public Builder west(int x, int y) {
			textureIndexes.put(Orientation.WEST, new Point(x, y));
			return this;
		}

		public Builder northEast(int x, int y) {
			textureIndexes.put(Orientation.NORTH_EAST, new Point(x, y));
			return this;
		}

		public Builder northWest(int x, int y) {
			textureIndexes.put(Orientation.NORTH_WEST, new Point(x, y));
			return this;
		}

		public Builder southEast(int x, int y) {
			textureIndexes.put(Orientation.SOUTH_EAST, new Point(x, y));
			return this;
		}

		public Builder southWest(int x, int y) {
			textureIndexes.put(Orientation.SOUTH_WEST, new Point(x, y));
			return this;
		}

		public OrientedSprite build() {
			return new OrientedSprite(orientation, atlas, textureIndexes);
		}
	}
}
