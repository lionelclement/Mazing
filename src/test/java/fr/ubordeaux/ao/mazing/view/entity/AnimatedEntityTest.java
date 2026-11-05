package fr.ubordeaux.ao.mazing.view.entity;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedOrientedSprite;
import fr.ubordeaux.ao.mazing.view.texture.TextureAtlas;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AnimatedEntityTest {

    @TempDir
    Path directory;

    /** An animated sprite without shadow atlas must remain usable. */
    private AnimatedOrientedSprite spriteWithoutShadow() throws Exception {
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xffff0000);
        Path file = directory.resolve("sprite.png");
        ImageIO.write(image, "png", file.toFile());
        TextureAtlas atlas = TextureAtlas.loadFromImage(file, 2, 2);
        return new AnimatedOrientedSprite(
                Orientation.NORTH,
                atlas,
                Map.of(Orientation.NORTH, List.of(new Point(0, 0))),
                1);
    }

    @Test
    void drawsShadowByDefaultWhenSpriteHasShadowAtlas() throws Exception {
        BufferedImage spriteImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        spriteImage.setRGB(0, 0, 0xffff0000);
        BufferedImage shadowImage = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        shadowImage.setRGB(1, 0, 0xff008800);
        Path spriteFile = directory.resolve("foreground.png");
        Path shadowFile = directory.resolve("shadow.png");
        ImageIO.write(spriteImage, "png", spriteFile.toFile());
        ImageIO.write(shadowImage, "png", shadowFile.toFile());
        AnimatedOrientedSprite sprite = new AnimatedOrientedSprite.Builder(
                TextureAtlas.loadFromImage(spriteFile, 2, 2))
                .withShadowAtlas(TextureAtlas.loadFromImage(shadowFile, 2, 2))
                .animationSize(1)
                .north(0, 0)
                .build();
        AnimatedEntity entity = new AnimatedEntity(sprite);
        BufferedImage output = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = output.createGraphics();
        try {
            entity.renderInScreen(graphics, 0, 0, 0, 1);
        } finally {
            graphics.dispose();
        }
        assertEquals(0xffff0000, output.getRGB(0, 0));
        assertEquals(0xff008800, output.getRGB(1, 0));
    }

    @Test
    void constructorsUseBlitOrderTwo() throws Exception {
        AnimatedOrientedSprite sprite = spriteWithoutShadow();

        assertEquals(2, new AnimatedEntity(sprite).getBlitOrder());
        assertEquals(2, new AnimatedEntity(sprite, Orientation.NORTH).getBlitOrder());
        assertEquals(2, new AnimatedEntity(sprite, 1, 2, 3).getBlitOrder());
        assertEquals(2, new AnimatedEntity(sprite, Orientation.NORTH, 1, 2, 3).getBlitOrder());
    }

    @Test
    void renderingDoesNotAdvanceAnimationOrInvokeFrameListeners() throws Exception {
        AnimatedEntity entity = new AnimatedEntity(spriteWithoutShadow());
        java.util.List<Integer> frames = new java.util.ArrayList<>();
        entity.setAnimationFrameListener(frames::add);

        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            entity.renderInScreen(graphics, 0, 0, 1, 1);
            entity.renderInScreen(graphics, 0, 0, 2, 1);
            assertEquals(List.of(), frames);

            entity.updateAnimation(1);
            assertEquals(List.of(0), frames);

            entity.renderInScreen(graphics, 0, 0, 3, 1);
            assertEquals(List.of(0), frames);

            entity.updateAnimation(1); // Same tick must not advance twice.
            assertEquals(List.of(0), frames);
            entity.updateAnimation(2);
            assertEquals(List.of(0, 0), frames);
        } finally {
            graphics.dispose();
        }
    }

    @Test
    void drawsCustomSpriteEvenWithoutShadowAtlas() throws Exception {
        AnimatedEntity entity = new AnimatedEntity(
                spriteWithoutShadow(), Orientation.NORTH, 0, 0, 0);
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            assertDoesNotThrow(() -> entity.renderInScreen(graphics, 0, 0, 0, 1));
        } finally {
            graphics.dispose();
        }
        assertEquals(0xffff0000, image.getRGB(0, 0));
    }
}
