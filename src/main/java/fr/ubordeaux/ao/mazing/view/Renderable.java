package fr.ubordeaux.ao.mazing.view;

import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

import java.awt.*;

/**
 * Defines an object that can be positioned and rendered
 * in an isometric scene.
 */
public interface Renderable extends Comparable<Renderable> {
	/**
	 * Renders the object at its projected screen position.
	 *
	 * @param g graphics context used for drawing
	 * @param x projected X screen coordinate
	 * @param y projected Y screen coordinate
	 * @param animationTick current animation tick
	 * @param zoom renderer zoom factor
	 */
	void renderInScreen(Graphics2D g, int x, int y, int animationTick, int zoom);

	/** Returns the precise X coordinate in grid units. */
	default double getPreciseX() {
		return getX();
	}

	/** Returns the precise Y coordinate in grid units. */
	default double getPreciseY() {
		return getY();
	}

	int getX();

	int getY();

	int getZ();

	/** Returns the precise height in grid levels. */
	default double getPreciseZ() {
		return getZ();
	}

	/**
	 * Rendering order level, independent of the rounded grid position.
	 * Animated characters may enter the next level while climbing.
	 */
	default int getRenderLevel() {
		return getZ();
	}

	/** Returns the rendering layer; lower layers are rendered first. */
	default int getRenderLayer() {
		return 1;
	}

	default int getBlitOrder() {
		return 0;
	}

	@Override
	default int compareTo(Renderable other) {
		// Draw lower floors first, then higher floors.
		int levelComparison = Integer.compare(getRenderLevel(), other.getRenderLevel());
		if (levelComparison != 0) {
			return levelComparison;
		}

		// On the same level, draw every floor tile before other objects.
		int layerComparison = Integer.compare(
				getRenderLayer(), other.getRenderLayer());
		if (layerComparison != 0) {
			return layerComparison;
		}

		// Within a layer, draw back-to-front along the isometric grid.
		// The viewer is toward +X (east) and -Y (south).
		int depthComparison = Double.compare(
				getPreciseX() - getPreciseY(),
				other.getPreciseX() - other.getPreciseY());
		if (depthComparison != 0) {
			return depthComparison;
		}

		return Integer.compare(getBlitOrder(), other.getBlitOrder());
	}

	/** Advances animated state even when the sprite is off-screen. */
	default void updateAnimation(int animationTick) { }

	/** Returns null when conservative painted bounds are unknown. */
	default Rectangle getScreenBounds(int screenX, int screenY, int zoom) {
		return null;
	}

	void setSprite(Sprite sprite);
}
