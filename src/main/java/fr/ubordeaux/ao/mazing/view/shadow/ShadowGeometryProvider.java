package fr.ubordeaux.ao.mazing.view.shadow;

import fr.ubordeaux.ao.mazing.view.Orientation;

/** Optional geometry metadata for sprites whose footprint is not a simple box. */
public interface ShadowGeometryProvider {
    /**
     * Performs the shadowGeometry operation.
     * @param orientation orientation of the sprite
     * @return the resulting value
     */
    ShadowGeometry shadowGeometry(Orientation orientation);
}
