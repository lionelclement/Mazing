package fr.ubordeaux.ao.mazing.view.sprite;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.texture.TextureAtlas;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimatedOrientedSprite implements Sprite {
	private final Orientation defaultOrientation;
	private final TextureAtlas atlas;
	private final Map<Orientation, List<Point>> textureIndexes;
	private final int animationSize;

	/**
	 * @param defaultOrientation the default orientation
	 * @param atlas              the atlas to use for drawing
	 * @param textureIndexes     indexes of the texture to use for the direction, in the order of the direction enum
	 * @param animationSize   the number of sprites to display an animation
	 */
	public AnimatedOrientedSprite(Orientation defaultOrientation, TextureAtlas atlas, Map<Orientation, List<Point>> textureIndexes, int animationSize) {
		this.defaultOrientation = defaultOrientation;
		this.atlas = atlas;
		this.textureIndexes = textureIndexes;
		this.animationSize = animationSize;
	}

	/// @param atlas          the atlas to use for drawing
	/// @param textureIndexes indexes of the texture to use for the direction, in the order of the direction enum
	public AnimatedOrientedSprite(TextureAtlas atlas, Map<Orientation, List<Point>> textureIndexes, int animationSize) {
		this(Orientation.NORTH, atlas, textureIndexes, animationSize);
	}

	@Override
	public void render(Graphics2D g, int x, int y, Orientation orientation, int animationIndex, int zoom) {
		List<Point> ps = this.textureIndexes.get(orientation);
		if (ps == null) {
			// if the direction is not known, try the "simpler" version of the direction
			ps = this.textureIndexes.get(orientation.asSimple());
		}
		Point p = ps.get(animationIndex);
		atlas.blit(g, x, y - 448, p.x(), p.y(), zoom);

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

	public static class Builder {
		private final Orientation orientation;
		private final TextureAtlas atlas;
		private final Map<Orientation, List<Point>> textureIndexes;
		private int animationSize;

		public Builder(TextureAtlas atlas, Orientation orientation, int animationSize) {
			this.orientation = orientation;
			this.atlas = atlas;
			this.textureIndexes = new HashMap<>();
			this.animationSize = animationSize;
		}

		public Builder(TextureAtlas atlas) {
			this(atlas, Orientation.NORTH, 0);
		}

		public Builder animationSize(int animationSize) {
			this.animationSize = animationSize;
			return this;
		}

		public Builder inOrientation(Orientation orientation, int... xy) {
			if (xy.length % 2 != 0 && xy.length / 2 != this.animationSize) {
				throw new IllegalArgumentException("xy must have an even number of elements and be two times the number of animation sprites");
			}
			ArrayList<Point> ps = new ArrayList<>();
			for (int i = 0; i < xy.length; i += 2) {
				ps.add(new Point(xy[i], xy[i + 1]));
			}
			textureIndexes.put(orientation, ps);
			return this;
		}

		public Builder north(int... xy) {
			return inOrientation(Orientation.NORTH, xy);
		}

		public Builder south(int... xy) {
			return inOrientation(Orientation.SOUTH, xy);
		}

		public Builder east(int... xy) {
			return inOrientation(Orientation.EAST, xy);
		}

		public Builder west(int... xy) {
			return inOrientation(Orientation.WEST, xy);
		}

		public Builder northEast(int... xy) {
			return inOrientation(Orientation.NORTH_EAST, xy);
		}

		public Builder northWest(int... xy) {
			return inOrientation(Orientation.NORTH_WEST, xy);
		}

		public Builder southEast(int... xy) {
			return inOrientation(Orientation.SOUTH_EAST, xy);
		}

		public Builder southWest(int... xy) {
			return inOrientation(Orientation.SOUTH_WEST, xy);
		}

		public AnimatedOrientedSprite build() {
			return new AnimatedOrientedSprite(this.orientation, this.atlas, this.textureIndexes, this.animationSize);
		}
	}
}
