package fr.ubordeaux.ao.mazing.view.entity;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

/** A freely positioned decor object, typically furniture. */
public final class FreeEntity extends Entity {

    /** Existing API: fractional X/Y, integer Z. */
    public FreeEntity(Sprite sprite, Orientation orientation,
            double x, double y, int z) {
        super(sprite, orientation, x, y, z);
    }

    /** Existing API with independent visual scale. */
    public FreeEntity(Sprite sprite, Orientation orientation,
            double x, double y, int z, double scale) {
        super(sprite, orientation, x, y, z, scale);
    }

    /** Fractional world-space coordinates on all three axes. */
    public FreeEntity(Sprite sprite, Orientation orientation,
            double x, double y, double z) {
        super(sprite, orientation, x, y, z);
    }

    /** Fractional world-space coordinates and visual scale. */
    public FreeEntity(Sprite sprite, Orientation orientation,
            double x, double y, double z, double scale) {
        super(sprite, orientation, x, y, z, scale);
    }

    public FreeEntity(Sprite sprite, double x, double y, int z) {
        this(sprite, Orientation.NORTH, x, y, z);
    }

    public FreeEntity(Sprite sprite, double x, double y, double z) {
        this(sprite, Orientation.NORTH, x, y, z);
    }
}
