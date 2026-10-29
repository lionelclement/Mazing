package fr.ubordeaux.ao.mazing.view.shadow;

import fr.ubordeaux.ao.mazing.view.Orientation;

/** Optional geometry metadata for sprites whose footprint is not a simple box. */
public interface ShadowGeometryProvider {
    ShadowGeometry shadowGeometry(Orientation orientation);
}
