package fr.ubordeaux.ao.mazing.view.sprite;

import fr.ubordeaux.ao.mazing.view.Orientation;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

/**
 * A one-tile walkable ramp. The underlying sprite provides its artwork; the
 * rise is its physical height from the low edge to the high edge (world Z).
 *
 * <p>The public orientation specifies the uphill direction (+X east,
 * +Y north). This metadata is independent of the PNG's transparent bounds
 * and can be attached to any ordinary sprite without changing its scale.
 */
public final class RampSprite implements Sprite {
    private final Sprite artwork;
    private final double rise;

    public RampSprite(Sprite artwork, double rise) {
        this.artwork = Objects.requireNonNull(artwork, "artwork");
        if (!Double.isFinite(rise) || rise <= 0) {
            throw new IllegalArgumentException("rise must be finite and positive");
        }
        this.rise = rise;
    }

    public double rise() {
        return rise;
    }

    @Override
    public void render(Graphics2D g, int x, int y, Orientation orientation,
            int animationIndex, int zoom) {
        artwork.render(g, x, y, orientation, animationIndex, zoom);
    }

    @Override
    public Rectangle getScreenBounds(int screenX, int screenY, int zoom) {
        return artwork.getScreenBounds(screenX, screenY, zoom);
    }

    @Override
    public double getScaleAnchorX(int zoom) {
        return artwork.getScaleAnchorX(zoom);
    }

    @Override
    public int animationSize() {
        return artwork.animationSize();
    }
}
