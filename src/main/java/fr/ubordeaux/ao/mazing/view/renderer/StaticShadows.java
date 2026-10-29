package fr.ubordeaux.ao.mazing.view.renderer;

import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.Renderable;
import fr.ubordeaux.ao.mazing.view.entity.FloorTile;
import fr.ubordeaux.ao.mazing.view.entity.Entity;
import fr.ubordeaux.ao.mazing.view.sprite.IsometricSprites;
import fr.ubordeaux.ao.mazing.view.shadow.DirectionalLight;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowCaster;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowGeometry;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Cached shadows on horizontal surfaces for a fixed directional light.
 *
 * <p>Upright static sprites cast a projected version of their actual alpha
 * silhouette, including holes between chair legs or fence pickets. Their
 * declared footprint and height determine where that silhouette lands.
 * A 2D sprite cannot recover its exact 3D shape; each image is approximated
 * as a vertical sheet supported by its footprint. Raised floors instead cast
 * the horizontal area of their surface.
 *
 * <p>Silhouettes, projected stamps, and floor overlays are cached. Changing
 * the camera does not rebuild them. Call invalidate() when a static caster
 * moves, changes sprite/orientation/scale, or when the scene is edited.
 */
public final class StaticShadows {
    private DirectionalLight light = DirectionalLight.DEFAULT;
    private final Map<FloorTile, BufferedImage> overlays = new IdentityHashMap<>();
    private final Map<FloorTile, PreparedTile> preparedTiles = new IdentityHashMap<>();
    private final Map<Renderable, Silhouette> silhouettes = new IdentityHashMap<>();
    private final Map<Renderable, Map<Double, Stamp>> stamps = new IdentityHashMap<>();
    private int cachedZoom = -1;

    private record Silhouette(BufferedImage image) { }
    private record PreparedTile(BufferedImage image, int offsetX, int offsetY) { }

    /** A projected image in camera-independent isometric screen coordinates. */
    private record Stamp(BufferedImage image, int x, int y) {
        Rectangle bounds() {
            return new Rectangle(x, y, image.getWidth(), image.getHeight());
        }
    }

    /** Configures the light; its color and direction are shared by all casters. */
    public void setLight(DirectionalLight light) {
        this.light = Objects.requireNonNull(light, "light");
        invalidate();
    }

    /** Discards cached shadows after a static scene or lighting change. */
    public void invalidate() {
        overlays.clear();
        preparedTiles.clear();
        silhouettes.clear();
        stamps.clear();
    }

    /**
     * Precomputes all static floor overlays. Invoke after building the scene,
     * before starting its animation loop, to avoid first-frame work.
     */
    public void prepare(IsometricRenderer renderer, List<Renderable> scene) {
        int zoom = renderer.getZoom();
        if (cachedZoom != zoom) {
            invalidate();
            cachedZoom = zoom;
        }
        for (Renderable element : scene) {
            if (element instanceof FloorTile floor && !preparedTiles.containsKey(floor)) {
                prepareFloor(floor, scene, renderer);
            }
        }
    }

    /**
     * Paints a precomposited static floor sprite with just one image blit.
     * Debug labels still use the renderer's original draw path.
     */
    public void paintFloor(Graphics2D graphics, IsometricRenderer renderer,
            FloorTile floor, List<Renderable> scene, int animationTick) {
        int zoom = renderer.getZoom();
        if (cachedZoom != zoom) {
            invalidate();
            cachedZoom = zoom;
        }
        if (renderer.isDebug()) {
            renderer.renderInIsoGrid(graphics, floor, animationTick);
            paint(graphics, renderer, floor, scene);
            return;
        }
        Point anchor = renderer.project(floor);
        Rectangle bounds = floor.getScreenBounds(anchor.x(), anchor.y(), zoom);
        Rectangle clip = graphics.getClipBounds();
        if (bounds == null) {
            renderer.renderInIsoGrid(graphics, floor, animationTick);
            return;
        }
        if (clip != null && !bounds.intersects(clip)) {
            return;
        }
        if (!preparedTiles.containsKey(floor)) {
            prepareFloor(floor, scene, renderer);
        }
        PreparedTile image = preparedTiles.get(floor);
        if (image != null) {
            graphics.drawImage(image.image(), bounds.x + image.offsetX(),
                    bounds.y + image.offsetY(), null);
        } else {
            renderer.renderInIsoGrid(graphics, floor, animationTick);
        }
    }

    /** Combines a static tile and its shadows once, outside the animation loop. */
    private void prepareFloor(FloorTile floor, List<Renderable> scene,
            IsometricRenderer renderer) {
        BufferedImage overlay = createOverlay(floor, scene, renderer);
        overlays.put(floor, overlay);
        if (overlay == null) {
            preparedTiles.put(floor, null);
            return;
        }
        Rectangle bounds = floor.getScreenBounds(0, 0, renderer.getZoom());
        if (bounds == null) {
            preparedTiles.put(floor, null);
            return;
        }
        BufferedImage composite = new BufferedImage(
                bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = composite.createGraphics();
        try {
            floor.renderInScreen(graphics, -bounds.x, -bounds.y, 0,
                    renderer.getZoom());
            graphics.drawImage(overlay, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        // Atlas cells contain substantial transparent padding. Crop it
        // before caching: drawing a small diamond is far cheaper than
        // compositing a mostly empty full-sized atlas cell every frame.
        int minX = composite.getWidth();
        int minY = composite.getHeight();
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < composite.getHeight(); y++) {
            for (int x = 0; x < composite.getWidth(); x++) {
                if ((composite.getRGB(x, y) >>> 24) != 0) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < minX) {
            preparedTiles.put(floor, null);
            return;
        }
        BufferedImage trimmed = new BufferedImage(
                maxX - minX + 1, maxY - minY + 1,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D crop = trimmed.createGraphics();
        try {
            crop.drawImage(composite, -minX, -minY, null);
        } finally {
            crop.dispose();
        }
        preparedTiles.put(floor, new PreparedTile(trimmed, minX, minY));
    }

    /** Draws the cached overlay using the receiver's real PNG alpha. */
    public void paint(Graphics2D graphics, IsometricRenderer renderer,
            FloorTile receiver, List<Renderable> scene) {
        int zoom = renderer.getZoom();
        if (cachedZoom != zoom) {
            invalidate();
            cachedZoom = zoom;
        }
        Point anchor = renderer.project(receiver);
        Rectangle bounds = receiver.getScreenBounds(anchor.x(), anchor.y(), zoom);
        Rectangle clip = graphics.getClipBounds();
        if (bounds == null || (clip != null && !bounds.intersects(clip))) {
            return;
        }
        if (!overlays.containsKey(receiver)) {
            overlays.put(receiver, createOverlay(receiver, scene, renderer));
        }
        BufferedImage overlay = overlays.get(receiver);
        if (overlay != null) {
            graphics.drawImage(overlay, bounds.x, bounds.y, null);
        }
    }

    /** Builds a tile-local image, independent of the camera position. */
    private BufferedImage createOverlay(FloorTile receiver,
            List<Renderable> scene, IsometricRenderer renderer) {
        int zoom = renderer.getZoom();
        Rectangle local = receiver.getScreenBounds(0, 0, zoom);
        if (local == null || local.isEmpty()) {
            return null;
        }
        double hx = renderer.getTileDiagonalX() / (2.0 * zoom);
        double hy = renderer.getTileDiagonalY() / (2.0 * zoom);
        double floorX = hx * (receiver.getPreciseX() + receiver.getPreciseY());
        double floorY = hy * (receiver.getPreciseX() - receiver.getPreciseY() - 1);
        Rectangle target = new Rectangle((int) Math.round(floorX) + local.x,
                (int) Math.round(floorY) + local.y, local.width, local.height);

        BufferedImage overlay = new BufferedImage(
                local.width, local.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = overlay.createGraphics();
        boolean hasShadow = false;
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(-target.x, -target.y);
            for (Renderable caster : scene) {
                if (caster == receiver) {
                    continue;
                }
                if (caster instanceof FloorTile raised) {
                    Path2D footprint = floorShadow(raised, receiver, hx, hy);
                    if (footprint == null) {
                        continue;
                    }
                    Rectangle bounds = footprint.getBounds();
                    bounds.translate((int) Math.round(floorX),
                            (int) Math.round(floorY));
                    if (!bounds.intersects(target)) {
                        continue;
                    }
                    Graphics2D localGraphics = (Graphics2D) g.create();
                    try {
                        localGraphics.translate(Math.round(floorX), Math.round(floorY));
                        localGraphics.setColor(light.shadowColor());
                        localGraphics.fill(footprint);
                        hasShadow = true;
                    } finally {
                        localGraphics.dispose();
                    }
                } else if (caster instanceof Entity entity
                        && IsometricSprites.hasEmbeddedShadow(entity.getSprite())) {
                    // The PNG already includes its shadow. Do not add a cached one.
                    continue;
                } else if (caster instanceof ShadowCaster shadowCaster) {
                    Stamp stamp = stampFor(caster, shadowCaster,
                            receiver.getPreciseZ(), renderer);
                    if (stamp != null && stamp.bounds().intersects(target)) {
                        g.drawImage(stamp.image(), stamp.x(), stamp.y(), null);
                        hasShadow = true;
                    }
                }
            }
        } finally {
            g.dispose();
        }
        if (!hasShadow) {
            return null;
        }

        // Do not darken transparent atlas padding or a hole in a floor tile.
        BufferedImage mask = new BufferedImage(
                local.width, local.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D maskGraphics = mask.createGraphics();
        try {
            receiver.renderInScreen(maskGraphics, -local.x, -local.y, 0, zoom);
        } finally {
            maskGraphics.dispose();
        }
        Graphics2D composite = overlay.createGraphics();
        try {
            composite.setComposite(AlphaComposite.DstIn);
            composite.drawImage(mask, 0, 0, null);
        } finally {
            composite.dispose();
        }
        return overlay;
    }

    /** Cache one projected silhouette per caster, receiving level and zoom. */
    private Stamp stampFor(Renderable caster, ShadowCaster source,
            double receivingLevel, IsometricRenderer renderer) {
        Map<Double, Stamp> levels = stamps.computeIfAbsent(
                caster, ignored -> new java.util.HashMap<>());
        if (levels.containsKey(receivingLevel)) {
            return levels.get(receivingLevel);
        }
        Stamp stamp = projectSilhouette(caster, source, receivingLevel, renderer);
        levels.put(receivingLevel, stamp);
        return stamp;
    }

    /**
     * Affine projection of a sprite's actual alpha, approximated as an upright
     * sheet spanning the declared physical footprint and height.
     * No individual shadow image is regenerated while the scene is painted.
     */
    private Stamp projectSilhouette(Renderable caster, ShadowCaster source,
            double receivingLevel, IsometricRenderer renderer) {
        Silhouette silhouette = silhouettes.computeIfAbsent(caster,
                key -> extractSilhouette(key, renderer.getZoom()));
        if (silhouette == null) {
            return null;
        }
        ShadowGeometry geometry = source.shadowGeometry();
        if (geometry == null) {
            // Estimate the width of the horizontal footprint and the height
            // of a generic object from its *visible* pixels. The sprite has
            // already been rendered at its actual scale and orientation.
            // Walls, fences, and manually configured objects keep their
            // explicitly declared physical geometry.
            double zoom = renderer.getZoom();
            geometry = ShadowGeometry.estimateFromSprite(
                    silhouette.image().getWidth(),
                    silhouette.image().getHeight(),
                    renderer.getTileDiagonalX() / zoom,
                    renderer.getTileDiagonalY() / zoom,
                    renderer.getTileHeight() / zoom);
        }
        if (geometry == null || geometry.height() <= 0.0) {
            return null;
        }
        // A directional light casts downward. A source whose highest point
        // does not rise above the receiver plane cannot cast onto it.
        // An epsilon also avoids a stray one-pixel shadow at a contact plane.
        if (caster.getPreciseZ() + geometry.height() <= receivingLevel + 1e-7) {
            return null;
        }
        double baseHeight = caster.getPreciseZ() - receivingLevel;

        // Find the two footprint points at the horizontal extremes of the
        // isometric view. This makes vertical structures orientation-aware.
        ShadowGeometry.Vertex left = null;
        ShadowGeometry.Vertex right = null;
        for (ShadowGeometry.Vertex vertex : geometry.footprint()) {
            double horizontal = vertex.x() + vertex.y();
            if (left == null || horizontal < left.x() + left.y()) left = vertex;
            if (right == null || horizontal > right.x() + right.y()) right = vertex;
        }
        if (left == null || right == null) {
            return null;
        }
        // A screen-wide object can have equal projected X coordinates, for
        // example a diagonal footprint. It still gets a nonzero-width stamp.
        if (Math.abs((right.x() + right.y()) - (left.x() + left.y())) < 1e-6) {
            right = new ShadowGeometry.Vertex(left.x() + 0.03, left.y() + 0.03);
        }

        double hx = renderer.getTileDiagonalX() / (2.0 * renderer.getZoom());
        double hy = renderer.getTileDiagonalY() / (2.0 * renderer.getZoom());
        double[] bottomLeft = project(caster, left, baseHeight, hx, hy);
        double[] bottomRight = project(caster, right, baseHeight, hx, hy);
        double[] topLeft = project(caster, left,
                baseHeight + geometry.height(), hx, hy);

        int width = silhouette.image().getWidth();
        int height = silhouette.image().getHeight();
        // Map source (0,height), (width,height), (0,0) to the three
        // projected points. The transform preserves legs and gaps in alpha.
        double m00 = (bottomRight[0] - bottomLeft[0]) / width;
        double m10 = (bottomRight[1] - bottomLeft[1]) / width;
        double m01 = (bottomLeft[0] - topLeft[0]) / height;
        double m11 = (bottomLeft[1] - topLeft[1]) / height;
        double m02 = bottomLeft[0] - height * m01;
        double m12 = bottomLeft[1] - height * m11;
        AffineTransform transform = new AffineTransform(m00, m10, m01, m11, m02, m12);

        double[][] corners = {{0, 0}, {width, 0}, {0, height}, {width, height}};
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (double[] corner : corners) {
            java.awt.geom.Point2D p = transform.transform(
                    new java.awt.geom.Point2D.Double(corner[0], corner[1]), null);
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
        }
        int x = (int) Math.floor(minX) - 2;
        int y = (int) Math.floor(minY) - 2;
        int w = (int) Math.ceil(maxX) - x + 2;
        int h = (int) Math.ceil(maxY) - y + 2;
        if (w < 1 || h < 1) {
            return null;
        }
        BufferedImage stamp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = stamp.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.translate(-x, -y);
            // An elevated receiving floor cannot receive shadows from the
            // portion of an object physically below that floor.
            BufferedImage visible = silhouette.image();
            if (baseHeight < 0.0) {
                int visibleRows = Math.min(height, Math.max(1, (int) Math.ceil(
                        height * (geometry.height() + baseHeight) / geometry.height())));
                visible = visible.getSubimage(0, 0, width, visibleRows);
            }
            g.drawImage(visible, transform, null);
        } finally {
            g.dispose();
        }
        return new Stamp(stamp, x, y);
    }

    /** Projects a physical point to zero-origin receiver-level screen space. */
    private double[] project(Renderable caster, ShadowGeometry.Vertex footprint,
            double height, double halfX, double halfY) {
        double x = caster.getPreciseX() + footprint.x() + light.rayX() * height;
        double y = caster.getPreciseY() + footprint.y() + light.rayY() * height;
        return new double[] {halfX * (x + y), halfY * (x - y - 1)};
    }

    /** Renders the source PNG once and keeps only its alpha silhouette. */
    private Silhouette extractSilhouette(Renderable caster, int zoom) {
        Rectangle bounds = caster.getScreenBounds(0, 0, zoom);
        if (bounds == null || bounds.isEmpty()) {
            return null;
        }
        BufferedImage source = new BufferedImage(
                bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = source.createGraphics();
        try {
            caster.renderInScreen(graphics, -bounds.x, -bounds.y, 0, zoom);
        } finally {
            graphics.dispose();
        }
        int minX = bounds.width;
        int minY = bounds.height;
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < bounds.height; y++) {
            for (int x = 0; x < bounds.width; x++) {
                if ((source.getRGB(x, y) >>> 24) > 8) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < minX || maxY < minY) {
            return null;
        }
        BufferedImage silhouette = new BufferedImage(
                maxX - minX + 1, maxY - minY + 1, BufferedImage.TYPE_INT_ARGB);
        int rgb = light.shadowColor().getRGB() & 0x00ffffff;
        int opacity = light.shadowColor().getAlpha();
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                int alpha = source.getRGB(x, y) >>> 24;
                if (alpha > 8) {
                    silhouette.setRGB(x - minX, y - minY,
                            ((alpha * opacity / 255) << 24) | rgb);
                }
            }
        }
        return new Silhouette(silhouette);
    }

    /** Horizontal raised tiles cast their own footprint, not an upright sheet. */
    private Path2D floorShadow(FloorTile raised, FloorTile receiver,
            double hx, double hy) {
        double altitude = raised.getPreciseZ() - receiver.getPreciseZ();
        if (altitude <= 0.0) {
            return null;
        }
        double x = raised.getPreciseX() + light.rayX() * altitude;
        double y = raised.getPreciseY() + light.rayY() * altitude;
        double[][] vertices = {{x, y}, {x + 1, y},
                {x + 1, y + 1}, {x, y + 1}};
        Path2D path = new Path2D.Double();
        for (int i = 0; i < vertices.length; i++) {
            double dx = vertices[i][0] - receiver.getPreciseX();
            double dy = vertices[i][1] - receiver.getPreciseY();
            double sx = hx * (dx + dy);
            double sy = hy * (dx - dy);
            if (i == 0) path.moveTo(sx, sy);
            else path.lineTo(sx, sy);
        }
        path.closePath();
        return path;
    }
}
