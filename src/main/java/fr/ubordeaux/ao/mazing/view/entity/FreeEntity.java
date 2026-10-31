package fr.ubordeaux.ao.mazing.view.entity;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

/** A freely positioned decor object, typically furniture. */
public final class FreeEntity extends Entity {

    /**
     * Existing API: fractional X/Y, integer Z.
     * @param sprite sprite to draw
     * @param orientation orientation of the sprite
     * @param x x value
     * @param y y value
     * @param z z value
     */
    public FreeEntity(Sprite sprite, Orientation orientation,
            double x, double y, int z) {
        super(sprite, orientation, x, y, z);
    }

    /**
     * Existing API with independent visual scale.
     * @param sprite sprite to draw
     * @param orientation orientation of the sprite
     * @param x x value
     * @param y y value
     * @param z z value
     * @param scale scale value
     */
    public FreeEntity(Sprite sprite, Orientation orientation,
            double x, double y, int z, double scale) {
        super(sprite, orientation, x, y, z, scale);
    }

    /**
     * Fractional world-space coordinates on all three axes.
     * @param sprite sprite to draw
     * @param orientation orientation of the sprite
     * @param x x value
     * @param y y value
     * @param z z value
     */
    public FreeEntity(Sprite sprite, Orientation orientation,
            double x, double y, double z) {
        super(sprite, orientation, x, y, z);
    }

    /**
     * Fractional world-space coordinates and visual scale.
     * @param sprite sprite to draw
     * @param orientation orientation of the sprite
     * @param x x value
     * @param y y value
     * @param z z value
     * @param scale scale value
     */
    public FreeEntity(Sprite sprite, Orientation orientation,
            double x, double y, double z, double scale) {
        super(sprite, orientation, x, y, z, scale);
    }

    /**
     * Documents this public type or member.
     * @param sprite sprite to display
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param z height coordinate
     */
    public FreeEntity(Sprite sprite, double x, double y, int z) {
        this(sprite, Orientation.NORTH, x, y, z);
    }

    /**
     * Documents this public type or member.
     * @param sprite sprite to display
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param z height coordinate
     */
    public FreeEntity(Sprite sprite, double x, double y, double z) {
        this(sprite, Orientation.NORTH, x, y, z);
    }
}
