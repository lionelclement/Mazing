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
    void rejectsInvalidZoom() {
        IsometricRenderer renderer = new IsometricRenderer(0, 0);
        assertThrows(IllegalArgumentException.class,
                () -> renderer.setZoom(0));
    }
}
