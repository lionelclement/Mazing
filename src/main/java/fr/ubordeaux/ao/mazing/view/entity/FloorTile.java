package fr.ubordeaux.ao.mazing.view.entity;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

/**
 * Represents a floor tile drawn below objects on the same level.
 */
public final class FloorTile extends Entity {

    /**
     * Creates a floor tile at a grid position.
     *
     * @param sprite the tile's visual appearance
     * @param orientation the tile's orientation
     * @param xPosition the X coordinate in grid units
     * @param yPosition the Y coordinate in grid units
     * @param zPosition the Z coordinate in grid units
     */
    public FloorTile(
            Sprite sprite,
            Orientation orientation,
            int xPosition,
            int yPosition,
            int zPosition) {

        super(sprite, orientation, xPosition, yPosition, zPosition);
    }

    /**
     * Allows raised tiles at fractional world elevations, e.g. 0.75.
     * @param sprite sprite to draw
     * @param orientation orientation of the sprite
     * @param xPosition horizontal grid position
     * @param yPosition vertical grid position
     * @param zPosition height in grid levels
     */
    public FloorTile(Sprite sprite, Orientation orientation,
            int xPosition, int yPosition, double zPosition) {
        this(sprite, orientation, xPosition, yPosition,
                (int) Math.ceil(zPosition));
        setPreciseZ(zPosition);
    }

    @Override
    public int getRenderLayer() {
        return 0;
    }
}
