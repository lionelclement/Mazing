package fr.ubordeaux.ao.mazing.view.shadow;

import java.awt.Color;
import java.util.Objects;

/**
 * Fixed light configuration for cached shadows on horizontal surfaces.
 * Ray components express horizontal displacement in grid units per level.
 * The shadow color includes its opacity in the alpha channel.
 */
public record DirectionalLight(double rayX, double rayY, Color shadowColor) {
    public static final DirectionalLight DEFAULT =
            new DirectionalLight(
                    -0.85, 0.50,
                    new Color(24, 29, 37, 95));

    public DirectionalLight {
        if (!Double.isFinite(rayX) || !Double.isFinite(rayY)) {
            throw new IllegalArgumentException("Light direction must be finite");
        }
        Objects.requireNonNull(shadowColor, "shadowColor");
    }
}
