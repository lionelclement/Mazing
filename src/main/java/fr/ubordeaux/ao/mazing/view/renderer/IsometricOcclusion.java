package fr.ubordeaux.ao.mazing.view.renderer;

import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.Renderable;
import fr.ubordeaux.ao.mazing.view.entity.FloorTile;
import fr.ubordeaux.ao.mazing.view.entity.Entity;
import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.sprite.RampSprite;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowCaster;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowGeometry;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Geometric occlusion for an orthographic 2.5D camera.
 *
 * <p>Thin barriers (2-point footprints) use a camera-facing half-plane
 * classification, as in BSP/painter ordering. Their visible PNG silhouette
 * determines the exact covered pixels. Solid objects use a per-pixel ray/box
 * depth test. Floors are treated as supporting surfaces, not standing walls.
 *
 * <p>World geometry, not atlas orientation or image naming, decides depth.
 * Every ordinary structure can expose its footprint via ShadowCaster; for
 * decorative objects without explicit geometry, the visible silhouette gives
 * an approximate box. A flat floor is a horizontal slab.
 *
 * <p>A 2D image contains no per-pixel 3D depth information. For physically
 * interpenetrating models a true mesh/depth map would be required instead.
 */
public final class IsometricOcclusion {

    /** Creates an instance of IsometricOcclusion. */
    public IsometricOcclusion() { }
    private static final double EPSILON = 1e-7;
    private static final ShadowGeometry FLOOR_SURFACE =
            ShadowGeometry.box(1.0, 1.0, 0.01);
    private final Map<Renderable, ShadowGeometry> inferredGeometry =
            new IdentityHashMap<>();
    private final Map<Renderable, SpriteAlpha> spriteAlphas =
            new IdentityHashMap<>();
    private int alphaZoom = -1;

    /** Cropped alpha silhouette relative to a sprite's projected anchor. */
    private record SpriteAlpha(BufferedImage image, int localX, int localY) {
        Rectangle onScreen(Point projected) {
            return new Rectangle(projected.x() + localX,
                    projected.y() + localY,
                    image.getWidth(), image.getHeight());
        }
    }

    /** Clears estimated sprite geometries after structural scene edits. */
    public void invalidate() {
        inferredGeometry.clear();
        spriteAlphas.clear();
        alphaZoom = -1;
    }

    /**
     * Returns an oriented object's physical proxy, or null if unavailable.
     * @param object object value
     * @param renderer isometric renderer
     * @param animationTick animationTick value
     * @return the value or object described above
     */
    public ShadowGeometry geometryFor(Renderable object,
            IsometricRenderer renderer, int animationTick) {
        if (object instanceof FloorTile) {
            return FLOOR_SURFACE;
        }
        // The shadow proxy or a PNG-inferred box is not a correct occluder
        // for a walkable ramp. Its wedge is evaluated directly per pixel.
        if (object instanceof Entity entity
                && entity.getSprite() instanceof RampSprite) {
            return null;
        }
        if (!(object instanceof ShadowCaster caster)) {
            return null;
        }
        ShadowGeometry declared = caster.shadowGeometry();
        if (declared != null) {
            return declared;
        }
        if (!inferredGeometry.containsKey(object)) {
            inferredGeometry.put(object,
                    estimateGeometry(object, renderer, animationTick));
        }
        return inferredGeometry.get(object);
    }

    /**
     * Classify the actor against each open barrier segment using the same
     * camera-facing half-plane rule (the standard BSP side-of-line test).
     * Corners are two adjoining segments, not a fictitious solid box.
     * Coordinates of the actor's feet are at the tile center (+0.5,+0.5).
      * @param geometry geometry value
      * @param barrierX barrierX value
      * @param barrierY barrierY value
      * @param actorX actorX value
      * @param actorY actorY value
      * @return the value or object described above
     */
    public static boolean behindBarrier(ShadowGeometry geometry,
            double barrierX, double barrierY,
            double actorX, double actorY) {
        if (!geometry.isThinBarrier()) {
            return false;
        }
        List<ShadowGeometry.Vertex> vertices = geometry.footprint();
        for (int i = 1; i < vertices.size(); i++) {
            ShadowGeometry.Vertex a = vertices.get(i - 1);
            ShadowGeometry.Vertex b = vertices.get(i);
            double dx = b.x() - a.x();
            double dy = b.y() - a.y();
            double ax = barrierX + a.x();
            double ay = barrierY + a.y();
            double actorSide = dx * (actorY + 0.5 - ay)
                    - dy * (actorX + 0.5 - ax);
            // Orthographic camera is at (+X, -Y) in the horizontal plane.
            double cameraSide = -dx - dy;
            // A character exactly on a wall plane belongs to the wall's
            // occluded side: the wall's visible pixels must cover the contact.
            // Do not classify a barrier parallel to the camera as foreground.
            // This tie-break applies to every wall/gate orientation, not to a
            // particular sprite or a positional offset in the demo.
            if (Math.abs(cameraSide) > EPSILON
                    && actorSide * cameraSide <= EPSILON) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fast rejection before rendering the static object's PNG alpha.
     * @param geometry geometry value
     * @param object object value
     * @param actorDepth actorDepth value
     * @return the value or object described above
     */
    public boolean mayBeInFront(ShadowGeometry geometry, Renderable object,
            double actorDepth) {
        double front = Double.NEGATIVE_INFINITY;
        for (ShadowGeometry.Vertex v : geometry.footprint()) {
            front = Math.max(front,
                    object.getPreciseX() + v.x()
                    - object.getPreciseY() - v.y());
        }
        return front > actorDepth + EPSILON;
    }

    /**
     * Is any point in the extruded footprint closer than the actor's
     * camera-facing billboard along the ray through this screen pixel?
     * The camera looks from +X, -Y, +Z, with an orthographic 2:1 projection.
      * @param geometry geometry value
      * @param objectX objectX value
      * @param objectY objectY value
      * @param objectZ objectZ value
      * @param actorDepth actorDepth value
      * @param pixelX pixelX value
      * @param pixelY pixelY value
     * @param renderer renderer providing projection coordinates
     * @return the resulting value
     */
    public static boolean isInFrontAtPixel(ShadowGeometry geometry,
            double objectX, double objectY, double objectZ,
            double actorDepth, int pixelX, int pixelY,
            IsometricRenderer renderer) {
        double hx = renderer.getTileDiagonalX() / (2.0 * renderer.getZoom());
        double hy = renderer.getTileDiagonalY() / (2.0 * renderer.getZoom());
        double h = renderer.getTileHeight() / (double) renderer.getZoom();
        Point origin = renderer.projectWorld(0, 0, 0);
        double s = (pixelX + 0.5 - origin.x()) / hx; // world X + Y

        // Intersect the horizontal camera ray (x+y=s) with the footprint.
        List<ShadowGeometry.Vertex> vertices = geometry.footprint();
        double minDepth = Double.POSITIVE_INFINITY;
        double maxDepth = Double.NEGATIVE_INFINITY;
        int count = vertices.size();
        int edges = geometry.isThinBarrier() ? count - 1 : count;
        for (int i = 0; i < edges; i++) {
            ShadowGeometry.Vertex a = vertices.get(i);
            ShadowGeometry.Vertex b = vertices.get((i + 1) % count);
            double ax = objectX + a.x();
            double ay = objectY + a.y();
            double bx = objectX + b.x();
            double by = objectY + b.y();
            double as = ax + ay;
            double bs = bx + by;
            if (Math.abs(bs - as) < EPSILON) {
                if (Math.abs(s - as) <= EPSILON) {
                    minDepth = Math.min(minDepth,
                            Math.min(ax - ay, bx - by));
                    maxDepth = Math.max(maxDepth,
                            Math.max(ax - ay, bx - by));
                }
            } else {
                double t = (s - as) / (bs - as);
                if (t >= -EPSILON && t <= 1 + EPSILON) {
                    t = Math.max(0, Math.min(1, t));
                    double x = ax + t * (bx - ax);
                    double y = ay + t * (by - ay);
                    minDepth = Math.min(minDepth, x - y);
                    maxDepth = Math.max(maxDepth, x - y);
                }
            }
        }
        if (maxDepth == Double.NEGATIVE_INFINITY) {
            return false;
        }

        // Along the same ray, Z increases monotonically with camera depth.
        // Clip its intersection interval against the object's vertical span.
        double relativeY = pixelY + 0.5 - (origin.y() + hy);
        double minFromHeight = 1 + (relativeY + h * objectZ) / hy;
        double maxFromHeight = 1 +
                (relativeY + h * (objectZ + geometry.height())) / hy;
        double near = Math.min(maxDepth, maxFromHeight);
        double far = Math.max(minDepth, minFromHeight);
        return near >= far - EPSILON && near > actorDepth + EPSILON;
    }

    /**
     * Ray/wedge depth test for a walkable, one-tile slope. The ramp is a solid
     * volume below the inclined walking plane, not a box as tall as its last
     * step. Orthographic screen rays are parameterized by d = worldX-worldY.
     *
     * <p>The screen coordinates are projected with the same +X/-Y convention
     * as IsometricRenderer. Clipping the ray against x/y bounds, the bottom
     * plane and the inclined top gives the actual nearest depth at this pixel.
     * In particular the ramp does not cover an actor standing on its top.
     * @param ramp ramp sprite to test
     * @param uphill direction toward the upper part of the ramp
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param z height coordinate
     * @param actorDepth depth of the rendered actor
     * @param pixelX horizontal screen pixel
     * @param pixelY vertical screen pixel
     * @param renderer renderer providing projection coordinates
     * @return the resulting value
     */
    public static boolean rampInFrontAtPixel(RampSprite ramp,
            Orientation uphill, double x, double y, double z,
            double actorDepth, int pixelX, int pixelY,
            IsometricRenderer renderer) {
        double hx = renderer.getTileDiagonalX() / (2.0 * renderer.getZoom());
        double hy = renderer.getTileDiagonalY() / (2.0 * renderer.getZoom());
        double h = renderer.getTileHeight() / (double) renderer.getZoom();
        Point origin = renderer.projectWorld(0, 0, 0);
        double s = (pixelX + 0.5 - origin.x()) / hx;
        double py = pixelY + 0.5;
        double lowX = x - 0.5, highX = x + 0.5;
        double lowY = y - 0.5, highY = y + 0.5;

        // Footprint intersection: x=(s+d)/2; y=(s-d)/2.
        double dMin = Math.max(2 * lowX - s, s - 2 * highY);
        double dMax = Math.min(2 * highX - s, s - 2 * lowY);
        if (dMin > dMax + EPSILON || dMax <= actorDepth + EPSILON) {
            return false;
        }

        // Screen Y is originY + hy*d - h*worldZ. Keep points at or
        // above the bottom plane, z. (Camera depth increases with d.)
        dMin = Math.max(dMin, (py - origin.y() + h * z) / hy);

        // Top plane: z + rise*(t0 + dt*d), with t in [0,1] on the tile.
        double t0;
        double dt;
        switch (uphill.asSimple()) {
            case NORTH -> { t0 = s / 2 - y + 0.5; dt = -0.5; }
            case SOUTH -> { t0 = y + 0.5 - s / 2; dt = 0.5; }
            case EAST  -> { t0 = s / 2 - x + 0.5; dt = 0.5; }
            case WEST  -> { t0 = x + 0.5 - s / 2; dt = -0.5; }
            default -> throw new IllegalStateException("Unexpected orientation");
        }
        // top(d) - rayZ(d) >= 0 is a linear half-plane constraint.
        double a = z + ramp.rise() * t0 - (origin.y() - py) / h;
        double b = ramp.rise() * dt - hy / h;
        if (Math.abs(b) < EPSILON) {
            if (a < -EPSILON) {
                return false;
            }
        } else {
            double boundary = -a / b;
            if (b > 0) {
                dMin = Math.max(dMin, boundary);
            } else {
                dMax = Math.min(dMax, boundary);
            }
        }
        return dMax >= dMin - EPSILON && dMax > actorDepth + EPSILON;
    }

    /**
     * Accurate alpha bounds of a static sprite, cached independently of its
     * current world position. Animated actors are not cached here.
     * @param object renderable object being evaluated
     * @param renderer renderer providing projection coordinates
     * @param animationTick current animation frame tick
     * @return the resulting value
     */
    public Rectangle visibleBounds(Renderable object,
            IsometricRenderer renderer, int animationTick) {
        SpriteAlpha alpha = staticAlpha(object, renderer, animationTick);
        return alpha == null ? null : alpha.onScreen(renderer.project(object));
    }

    private SpriteAlpha staticAlpha(Renderable object,
            IsometricRenderer renderer, int animationTick) {
        int zoom = renderer.getZoom();
        if (alphaZoom != zoom) {
            spriteAlphas.clear();
            alphaZoom = zoom;
        }
        if (spriteAlphas.containsKey(object)) {
            return spriteAlphas.get(object);
        }
        Rectangle bounds = object.getScreenBounds(0, 0, zoom);
        if (bounds == null || bounds.isEmpty()) {
            spriteAlphas.put(object, null);
            return null;
        }
        BufferedImage image = new BufferedImage(bounds.width, bounds.height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D painter = image.createGraphics();
        try {
            object.renderInScreen(painter, -bounds.x, -bounds.y,
                    animationTick, zoom);
        } finally {
            painter.dispose();
        }
        int minX = bounds.width, minY = bounds.height;
        int maxX = -1, maxY = -1;
        for (int y = 0; y < bounds.height; y++) {
            for (int x = 0; x < bounds.width; x++) {
                if ((image.getRGB(x, y) >>> 24) != 0) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < minX) {
            spriteAlphas.put(object, null);
            return null;
        }
        BufferedImage cropped = new BufferedImage(
                maxX - minX + 1, maxY - minY + 1,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D crop = cropped.createGraphics();
        try {
            crop.drawImage(image, -minX, -minY, null);
        } finally {
            crop.dispose();
        }
        SpriteAlpha result = new SpriteAlpha(cropped,
                bounds.x + minX, bounds.y + minY);
        spriteAlphas.put(object, result);
        return result;
    }

    /**
     * Applies an occluder only through the part that remains visible after
     * later-painted static sprites. This is a reverse painter / coverage-buffer
     * calculation, independent of the sprite type and atlas layout.
     *
     * <p>Even when {@code geometry} is null (or physically behind the actor),
     * the sprite contributes to the accumulated alpha coverage: a floor
     * painted over the base of a fence must prevent the hidden fence pixels
     * from erroneously erasing the actor.
     * @param actorImage image containing the rendered actor
     * @param actorRegion actor bounds in screen coordinates
     * @param occluder object that may hide part of the actor
     * @param occluderBounds screen-space bounds of the occluder
     * @param geometry geometric representation of the occluder
     * @param laterStaticCoverage coverage from subsequently rendered static objects
     * @param actorDepth depth of the rendered actor
     * @param actorX horizontal actor position
     * @param actorY vertical actor position
     * @param renderer renderer providing projection coordinates
     * @param animationTick current animation frame tick
     */
    public void subtractVisibleOccluder(BufferedImage actorImage,
            Rectangle actorRegion, Renderable occluder,
            Rectangle occluderBounds, ShadowGeometry geometry,
            byte[] laterStaticCoverage, double actorDepth,
            double actorX, double actorY,
            IsometricRenderer renderer, int animationTick) {
        if (laterStaticCoverage.length != actorRegion.width * actorRegion.height) {
            throw new IllegalArgumentException("Coverage buffer dimensions mismatch");
        }
        SpriteAlpha alpha = staticAlpha(occluder, renderer, animationTick);
        if (alpha == null) {
            return;
        }
        Point point = renderer.project(occluder);
        Rectangle overlap = actorRegion.intersection(occluderBounds);
        if (overlap.isEmpty()) {
            return;
        }
        int imageOriginX = point.x() + alpha.localX();
        int imageOriginY = point.y() + alpha.localY();

        // A ramp uses its own sloping volume, never its shadow geometry.
        RampSprite ramp = occluder instanceof Entity entity
                && entity.getSprite() instanceof RampSprite walkable
                ? walkable : null;
        boolean behindBarrier = geometry != null && geometry.isThinBarrier()
                && behindBarrier(geometry, occluder.getPreciseX(),
                        occluder.getPreciseY(), actorX, actorY);
        boolean solid = geometry != null && !geometry.isThinBarrier();
        for (int y = 0; y < overlap.height; y++) {
            int imageY = overlap.y + y - actorRegion.y;
            int screenY = overlap.y + y;
            for (int x = 0; x < overlap.width; x++) {
                int occluderAlpha = alpha.image().getRGB(
                        overlap.x + x - imageOriginX,
                        overlap.y + y - imageOriginY) >>> 24;
                if (occluderAlpha == 0) {
                    continue;
                }
                int imageX = overlap.x + x - actorRegion.x;
                int idx = imageY * actorRegion.width + imageX;
                int covered = Byte.toUnsignedInt(laterStaticCoverage[idx]);
                int actuallyVisible = occluderAlpha * (255 - covered) / 255;
                laterStaticCoverage[idx] = (byte) Math.min(255,
                        covered + actuallyVisible);
                if (actuallyVisible == 0) {
                    continue;
                }

                boolean inFront = ramp != null
                        ? rampInFrontAtPixel(ramp, ((Entity) occluder).getOrientation(),
                                occluder.getPreciseX(), occluder.getPreciseY(),
                                occluder.getPreciseZ(), actorDepth,
                                overlap.x + x, screenY, renderer)
                        : behindBarrier || (solid && isInFrontAtPixel(geometry,
                                occluder.getPreciseX(), occluder.getPreciseY(),
                                occluder.getPreciseZ(), actorDepth,
                                overlap.x + x, screenY, renderer));
                if (!inFront) {
                    continue;
                }
                int actorPixel = actorImage.getRGB(imageX, imageY);
                int actorAlpha = actorPixel >>> 24;
                if (actorAlpha == 0) {
                    continue;
                }
                int resultAlpha = actorAlpha * (255 - actuallyVisible) / 255;
                actorImage.setRGB(imageX, imageY,
                        (resultAlpha << 24) | (actorPixel & 0x00ffffff));
            }
        }
    }

    /** Automatic fallback when an entity has no explicitly modeled footprint. */
    private static ShadowGeometry estimateGeometry(Renderable renderable,
            IsometricRenderer renderer, int animationTick) {
        int zoom = renderer.getZoom();
        Rectangle bounds = renderable.getScreenBounds(0, 0, zoom);
        if (bounds == null || bounds.isEmpty()) {
            return null;
        }
        BufferedImage image = new BufferedImage(bounds.width, bounds.height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            renderable.renderInScreen(g, -bounds.x, -bounds.y,
                    animationTick, zoom);
        } finally {
            g.dispose();
        }
        int minX = bounds.width, minY = bounds.height;
        int maxX = -1, maxY = -1;
        for (int y = 0; y < bounds.height; y++) {
            for (int x = 0; x < bounds.width; x++) {
                if ((image.getRGB(x, y) >>> 24) > 8) {
                    minX = Math.min(x, minX);
                    minY = Math.min(y, minY);
                    maxX = Math.max(x, maxX);
                    maxY = Math.max(y, maxY);
                }
            }
        }
        if (maxX < minX || maxY < minY) {
            return null;
        }
        return ShadowGeometry.estimateFromSprite(
                maxX - minX + 1, maxY - minY + 1,
                renderer.getTileDiagonalX() / (double) zoom,
                renderer.getTileDiagonalY() / (double) zoom,
                renderer.getTileHeight() / (double) zoom);
    }
}
