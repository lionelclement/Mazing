package fr.ubordeaux.ao.mazing.view.shadow;

/**
 * Marks a stationary scene element with approximate geometry for shadows.
 * A moving caster needs dynamic shadow updates and should not be put into
 * the cached static shadow pass without invalidating that cache.
 */
public interface ShadowCaster {
    /**
     * Returns explicitly defined physical geometry, or {@code null} to let
     * the shadow renderer estimate it from the sprite's alpha silhouette.
     */
    ShadowGeometry shadowGeometry();
}
