package fr.ubordeaux.ao.mazing.tools;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Extracts one transparent shadow silhouette for each isometric source PNG.
 *
 * <p>These masks retain the exact source alpha at the original pixel position.
 * A light direction is deliberately not baked into them: a 2D image does not
 * specify the 3D height or depth required for a correct cast shadow.
 * The renderer can tint and project these silhouettes once physical geometry
 * is known. Generated files are not required to run the demonstration.
 */
public final class ShadowMaskGenerator {
    private static final Path DEFAULT_SOURCE = Path.of(
            "src/main/resources/Dungeon-Pack/Isometric");
    private static final Path DEFAULT_TARGET = Path.of(
            "build/generated/shadow-masks/Dungeon-Pack/Isometric");

    private ShadowMaskGenerator() { }

    /**
     * Generates masks for all nonempty source images, preserving subdirectories.
     * @param args args value
     * @throws java.io.IOException if the operation cannot be completed
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 2) {
            throw new IllegalArgumentException(
                    "Usage: ShadowMaskGenerator [source-directory [output-directory]]");
        }
        Path source = args.length > 0 ? Path.of(args[0]) : DEFAULT_SOURCE;
        Path target = args.length > 1 ? Path.of(args[1]) : DEFAULT_TARGET;
        if (!Files.isDirectory(source)) {
            throw new IOException("Sprite directory not found: " + source);
        }

        List<Path> inputs;
        try (Stream<Path> paths = Files.walk(source)) {
            inputs = paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString()
                            .toLowerCase(java.util.Locale.ROOT).endsWith(".png"))
                    .sorted()
                    .toList();
        }
        int written = 0;
        for (Path input : inputs) {
            BufferedImage sprite = ImageIO.read(input.toFile());
            if (sprite == null) {
                throw new IOException("Cannot read PNG: " + input);
            }
            BufferedImage silhouette = new BufferedImage(
                    sprite.getWidth(), sprite.getHeight(), BufferedImage.TYPE_INT_ARGB);
            boolean visible = false;
            for (int y = 0; y < sprite.getHeight(); y++) {
                for (int x = 0; x < sprite.getWidth(); x++) {
                    int alpha = sprite.getRGB(x, y) >>> 24;
                    if (alpha != 0) {
                        visible = true;
                        // Black silhouette, original per-pixel transparency.
                        silhouette.setRGB(x, y, alpha << 24);
                    }
                }
            }
            if (!visible) {
                continue;
            }
            Path relative = source.relativize(input);
            String name = relative.getFileName().toString();
            Path output = target.resolve(relative).resolveSibling(
                    name.substring(0, name.length() - 4) + "_shadow.png");
            Files.createDirectories(output.getParent());
            if (!ImageIO.write(silhouette, "png", output.toFile())) {
                throw new IOException("Cannot write PNG: " + output);
            }
            written++;
        }
        System.out.printf("Generated %d shadow silhouettes in %s%n", written, target);
    }
}
