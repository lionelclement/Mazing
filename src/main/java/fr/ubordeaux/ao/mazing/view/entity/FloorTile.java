package fr.ubordeaux.ao.mazing.view.entity;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

/**
 * Represents a floor tile rendered behind scene objects.
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

    @Override
    public int getRenderLayer() {
        return 0;
    }
}
