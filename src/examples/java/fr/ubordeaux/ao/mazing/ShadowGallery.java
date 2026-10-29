package fr.ubordeaux.ao.mazing;

/**
 * Compatibility entry point for builds still launching the historical
 * ShadowGallery class. New Gradle tasks should launch {@link Gallery} directly.
 *
 * @deprecated Use {@link Gallery} as the application main class.
 */
@Deprecated
public final class ShadowGallery {

    private ShadowGallery() {
        // No instances.
    }

    /**
     * Launches the current gallery.
     *
     * @param args command-line arguments forwarded to {@link Gallery}
     */
    public static void main(String[] args) {
        Gallery.main(args);
    }
}
