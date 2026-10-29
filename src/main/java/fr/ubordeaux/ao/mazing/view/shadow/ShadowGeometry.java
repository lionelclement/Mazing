package fr.ubordeaux.ao.mazing.view.shadow;

import fr.ubordeaux.ao.mazing.view.Orientation;

import java.util.List;
import java.util.Objects;

/**
 * Approximate physical shape of a shadow caster in grid units.
 * Footprint vertices are relative to the caster's grid position.
 * The height is measured in floor levels, not sprite pixels.
 */
public record ShadowGeometry(List<Vertex> footprint, double height, boolean closed) {

    /** Compatibility constructor: 2 points denote a segment, 3+ a polygon. */
    public ShadowGeometry(List<Vertex> footprint, double height) {
        this(footprint, height, footprint != null && footprint.size() > 2);
    }

    /** One vertex of the horizontal footprint. */
    public record Vertex(double x, double y) { }

    public ShadowGeometry {
        Objects.requireNonNull(footprint, "footprint");
        footprint = List.copyOf(footprint);
        if (footprint.size() < 2) {
            throw new IllegalArgumentException("A footprint needs at least two vertices");
        }
        if (!Double.isFinite(height) || height <= 0.0) {
            throw new IllegalArgumentException("Shadow height must be positive and finite");
        }
        for (Vertex vertex : footprint) {
            if (!Double.isFinite(vertex.x()) || !Double.isFinite(vertex.y())) {
                throw new IllegalArgumentException("Footprint coordinates must be finite");
            }
        }
    }

    /** A box centered on a tile, suitable for furniture and columns. */
    public static ShadowGeometry box(double width, double depth, double height) {
        if (!Double.isFinite(width) || !Double.isFinite(depth)
                || width <= 0.0 || depth <= 0.0) {
            throw new IllegalArgumentException("Box dimensions must be positive and finite");
        }
        double minX = (1.0 - width) / 2.0;
        double minY = (1.0 - depth) / 2.0;
        double maxX = minX + width;
        double maxY = minY + depth;
        return new ShadowGeometry(List.of(
                new Vertex(minX, minY), new Vertex(maxX, minY),
                new Vertex(maxX, maxY), new Vertex(minX, maxY)), height);
    }

    /**
     * Approximates an object's dimensions from the visible part of its sprite.
     *
     * <p>For an axis-aligned, roughly symmetric footprint, the isometric
     * projection spans one tile diagonal times the footprint's side length.
     * Its vertical span consumes a corresponding part of the sprite height;
     * the remaining pixels approximate the object's height above the floor.
     * All lengths must use the same screen zoom. This is a visual estimate,
     * not a reconstruction of hidden 3D geometry.
     *
     * @return estimated geometry, or null for a flat/degenerate silhouette
     */
    public static ShadowGeometry estimateFromSprite(double visibleWidth,
            double visibleHeight, double tileDiagonalX,
            double tileDiagonalY, double levelHeight) {
        if (!Double.isFinite(visibleWidth) || !Double.isFinite(visibleHeight)
                || !Double.isFinite(tileDiagonalX) || !Double.isFinite(tileDiagonalY)
                || !Double.isFinite(levelHeight)
                || visibleWidth <= 0.0 || visibleHeight <= 0.0
                || tileDiagonalX <= 0.0 || tileDiagonalY <= 0.0
                || levelHeight <= 0.0) {
            throw new IllegalArgumentException("Projected sprite dimensions must be positive and finite");
        }
        double footprintSide = visibleWidth / tileDiagonalX;
        double groundSpanY = footprintSide * tileDiagonalY;
        double height = (visibleHeight - groundSpanY) / levelHeight;
        if (height <= 0.0) {
            return null;
        }
        return box(footprintSide, footprintSide, height);
    }

    /** A wall segment along the corresponding edge of a tile. */
    public static ShadowGeometry wall(Orientation orientation, double height) {
        Objects.requireNonNull(orientation, "orientation");
        return switch (orientation) {
            case NORTH -> edgeY(1.0, height);
            case SOUTH -> edgeY(0.0, height);
            case EAST -> edgeX(1.0, height);
            case WEST -> edgeX(0.0, height);
            default -> box(0.3, 0.3, height);
        };
    }

    /**
     * A thin L-shaped barrier following the two edges of a tile corner.
     * Three vertices describe an OPEN polyline (not a triangular solid).
     * This is a geometric primitive, independent of the texture used to draw it.
     */
    public static ShadowGeometry corner(Orientation orientation, double height) {
        Objects.requireNonNull(orientation, "orientation");
        List<Vertex> edges = switch (orientation) {
            case NORTH_EAST -> List.of(new Vertex(1, 0), new Vertex(1, 1), new Vertex(0, 1));
            case NORTH_WEST -> List.of(new Vertex(0, 0), new Vertex(0, 1), new Vertex(1, 1));
            case SOUTH_EAST -> List.of(new Vertex(0, 0), new Vertex(1, 0), new Vertex(1, 1));
            case SOUTH_WEST -> List.of(new Vertex(1, 0), new Vertex(0, 0), new Vertex(0, 1));
            default -> throw new IllegalArgumentException("Expected a diagonal orientation: " + orientation);
        };
        return new ShadowGeometry(edges, height, false);
    }

    /** An open polyline describes a wall or barrier without volume. */
    public boolean isThinBarrier() {
        return !closed;
    }

    /** A barrier at a fixed X edge of the centered grid tile. */
    public static ShadowGeometry edgeX(double x, double height) {
        return new ShadowGeometry(List.of(new Vertex(x, 0.0),
                new Vertex(x, 1.0)), height);
    }

    /** A barrier at a fixed Y edge of the centered grid tile. */
    public static ShadowGeometry edgeY(double y, double height) {
        return new ShadowGeometry(List.of(new Vertex(0.0, y),
                new Vertex(1.0, y)), height);
    }
}
