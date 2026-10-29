package fr.ubordeaux.ao.mazing.view.entity;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowGeometry;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowGeometryProvider;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

/**
 * An upright structure that can occlude characters at another elevation.
 * Marks walls and supporting columns, not flat floor tiles.
 */
public final class OccludingEntity extends Entity {

    public OccludingEntity(Sprite sprite, Orientation orientation,
                      int x, int y, int z) {
        super(sprite, orientation, x, y, z);
    }

    public OccludingEntity(Sprite sprite, Orientation orientation,
                           int x, int y, int z, double scale) {
        super(sprite, orientation, x, y, z, scale);
    }
    /** Structures can also be anchored to a fractional platform elevation. */
    public OccludingEntity(Sprite sprite, Orientation orientation,
            int x, int y, double z) {
        this(sprite, orientation, (double) x, (double) y, z);
    }

    /** A wall or other occluding structure placed freely in 3D space. */
    public OccludingEntity(Sprite sprite, Orientation orientation,
            double x, double y, double z) {
        super(sprite, orientation, x, y, z);
    }

    /** Exact world position with an optional visual size multiplier. */
    public OccludingEntity(Sprite sprite, Orientation orientation,
            double x, double y, double z, double scale) {
        super(sprite, orientation, x, y, z, scale);
    }

    /** Ordinary walls cast narrow edge-shaped shadows. */
    @Override
    protected ShadowGeometry defaultShadowGeometry() {
        if (getSprite() instanceof ShadowGeometryProvider provider) {
            return provider.shadowGeometry(getOrientation());
        }
        // A diagonal orientation describes a two-sided corner, rather than
        // an unrelated little box centered in the tile.
        return switch (getOrientation()) {
            case NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST ->
                    ShadowGeometry.corner(getOrientation(), getScale());
            default -> ShadowGeometry.wall(getOrientation(), getScale());
        };
    }
}
