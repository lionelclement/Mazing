package fr.ubordeaux.ao.mazing.view.renderer;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IsometricRendererTest {

    @Test
    void projectsWorldOrigin() {
        IsometricRenderer renderer = new IsometricRenderer(0, 0);
        var point = renderer.projectWorld(0, 0, 0);

        assertEquals(0, point.x());
        assertEquals(-32, point.y());
    }

    @Test
    void projectsWorldCoordinates() {
        IsometricRenderer renderer = new IsometricRenderer(0, 0);

        var east = renderer.projectWorld(1, 0, 0);
        assertEquals(64, east.x());
        assertEquals(0, east.y());

        var north = renderer.projectWorld(0, 1, 0);
        assertEquals(64, north.x());
        assertEquals(-64, north.y());
    }

    @Test
    void changesProjectionWithZoom() {
        IsometricRenderer renderer = new IsometricRenderer(10, 20);
        renderer.setZoom(4);

        var point = renderer.projectWorld(0, 0, 0);
        assertEquals(10, point.x());
        assertEquals(4, point.y());
    }

    @Test
    void projectsFractionalCoordinates() {
        IsometricRenderer renderer = new IsometricRenderer(0, 0);
        var point = renderer.projectWorld(0.5, 0.25, 0);

        assertEquals(48, point.x());
        assertEquals(-24, point.y());
    }

    @Test
    void projectsAltitude() {
        IsometricRenderer renderer = new IsometricRenderer(0, 0);
        var ground = renderer.projectWorld(1, 1, 0);
        var raised = renderer.projectWorld(1, 1, 2);

        assertEquals(ground.x(), raised.x());
        assertEquals(ground.y() - 128, raised.y());
    }

    @Test
    void pansOriginWithoutChangingRelativeProjection() {
        IsometricRenderer renderer = new IsometricRenderer(0, 0);
        var before = renderer.projectWorld(2, 3, 1);

        renderer.pan(17, -9);
        var after = renderer.projectWorld(2, 3, 1);

        assertEquals(before.x() + 17, after.x());
        assertEquals(before.y() - 9, after.y());
    }

    @Test
    void rejectsNegativeZoom() {
        IsometricRenderer renderer = new IsometricRenderer(0, 0);
        assertThrows(IllegalArgumentException.class,
                () -> renderer.setZoom(-1));
    }

    @Test
    void rejectsInvalidZoom() {
        IsometricRenderer renderer = new IsometricRenderer(0, 0);
        assertThrows(IllegalArgumentException.class,
                () -> renderer.setZoom(0));
    }
}
