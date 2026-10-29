package fr.ubordeaux.ao.mazing;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.entity.AnimatedEntity;
import fr.ubordeaux.ao.mazing.view.entity.Entity;
import fr.ubordeaux.ao.mazing.view.entity.FloorTile;
import fr.ubordeaux.ao.mazing.view.entity.OccludingEntity;
import fr.ubordeaux.ao.mazing.view.entity.ShadowMode;
import fr.ubordeaux.ao.mazing.view.renderer.IsometricRenderer;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedOrientedSprite;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedSprites;
import fr.ubordeaux.ao.mazing.view.sprite.IsometricSprites;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;
import fr.ubordeaux.ao.mazing.view.swing.ViewPanel;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Large visual gallery showing the available isometric assets on a shared
 * ground plane. Sprites are grouped by family so they can be inspected side by
 * side without modifying {@link Demo}.
 */
public final class Gallery {

    private static final int WINDOW_WIDTH = 2200;
    private static final int WINDOW_HEIGHT = 1500;
    private static final int GROUND_WIDTH = 30;
    private static final int GROUND_HEIGHT = 30;
    private static final int ZOOM = 3;
    private static final int CAMERA_STEP = 12; // Screen pixels per animation tick.

    private static final Sprite[] GROUND_SPRITES = {
            IsometricSprites.BRIDGE_BROKEN,
            IsometricSprites.BRIDGE,
            IsometricSprites.DIRT_TILES,
            IsometricSprites.DIRT,
            IsometricSprites.PLANKS_BROKEN,
            IsometricSprites.PLANKS_HOLE,
            IsometricSprites.PLANKS,
            IsometricSprites.STONE_CORNER,
            IsometricSprites.STONE_INSET,
            IsometricSprites.STONE_LEFT,
            IsometricSprites.STONE_MISSING_TILES,
            IsometricSprites.STONE_RIGHT,
            IsometricSprites.STONE_SIDE_UNEVEN,
            IsometricSprites.STONE_SIDE,
            IsometricSprites.STONE_STEP,
            IsometricSprites.STONE_STEPS,
            IsometricSprites.STONE_TILE,
            IsometricSprites.STONE_UNEVEN,
            IsometricSprites.STONE
    };

    private static final Sprite[] CONSTRUCTION_SPRITES = {
            IsometricSprites.STONE_COLUMN_WOOD,
            IsometricSprites.STONE_COLUMN,
            IsometricSprites.WOODEN_SUPPORT_BEAMS,
            IsometricSprites.WOODEN_SUPPORTS_BEAM,
            IsometricSprites.WOODEN_SUPPORTS_BLOCK,
            IsometricSprites.WOODEN_SUPPORTS
    };

    private static final Sprite[] OBJECT_SPRITES = {
            IsometricSprites.BARREL,
            IsometricSprites.BARRELS_STACKED,
            IsometricSprites.BARRELS,
            IsometricSprites.CHAIR,
            IsometricSprites.CHEST_CLOSED,
            IsometricSprites.CHEST_OPEN,
            IsometricSprites.TABLE_CHAIRS_BROKEN,
            IsometricSprites.TABLE_ROUND_CHAIRS,
            IsometricSprites.TABLE_ROUND_ITEMS_CHAIRS,
            IsometricSprites.TABLE_ROUND,
            IsometricSprites.TABLE_SHORT_CHAIRS,
            IsometricSprites.TABLE_SHORT,
            IsometricSprites.WOODEN_CRATE,
            IsometricSprites.WOODEN_CRATES,
            IsometricSprites.WOODEN_PILE
    };

    private static final Sprite[] STAIR_SPRITES = {
            IsometricSprites.STAIRS_AGED,
            IsometricSprites.STAIRS_CORNER,
            IsometricSprites.STAIRS_SPIRAL,
            IsometricSprites.STAIRS
    };

    private static final Sprite[] WALL_SPRITES = {
            IsometricSprites.STONE_WALL_AGED_LEFT,
            IsometricSprites.STONE_WALL_AGED_RIGHT,
            IsometricSprites.STONE_WALL_AGED,
            IsometricSprites.STONE_WALL_ARCHWAYS,
            IsometricSprites.STONE_WALL_BROKEN_LEFT,
            IsometricSprites.STONE_WALL_BROKEN_RIGHT,
            IsometricSprites.STONE_WALL_BROKEN,
            IsometricSprites.STONE_WALL_COLUMN_IN,
            IsometricSprites.STONE_WALL_COLUMN,
            IsometricSprites.STONE_WALL_DOOR_BARS,
            IsometricSprites.STONE_WALL_DOOR_CLOSED,
            IsometricSprites.STONE_WALL_DOOR_OPEN,
            IsometricSprites.STONE_WALL_DOOR,
            IsometricSprites.STONE_WALL_GATE_BARS,
            IsometricSprites.STONE_WALL_GATE_CLOSED,
            IsometricSprites.STONE_WALL_GATE_OPEN,
            IsometricSprites.STONE_WALL_GATE,
            IsometricSprites.STONE_WALL_HALF,
            IsometricSprites.STONE_WALL_HOLE,
            IsometricSprites.STONE_WALL_ROUND_BROKEN,
            IsometricSprites.STONE_WALL_ROUND_WINDOW,
            IsometricSprites.STONE_WALL_ROUND,
            IsometricSprites.STONE_WALL_STRUCTURE,
            IsometricSprites.STONE_WALL_TOP,
            IsometricSprites.STONE_WALL_WINDOW_BARS,
            IsometricSprites.STONE_WALL_WINDOW,
            IsometricSprites.STONE_WALL
    };

    // One specimen of every public animation, grouped by creature.
    private static final AnimatedOrientedSprite[] CRUSADER_ANIMATIONS = {
            AnimatedSprites.CRUSADER_ATTACK,
            AnimatedSprites.CRUSADER_BLOCK,
            AnimatedSprites.CRUSADER_DEATH,
            AnimatedSprites.CRUSADER_HIT,
            AnimatedSprites.CRUSADER_IDLE,
            AnimatedSprites.CRUSADER_JUMP,
            AnimatedSprites.CRUSADER_RUN,
            AnimatedSprites.CRUSADER_WALK
    };

    private static final AnimatedOrientedSprite[] SPIDER_ANIMATIONS = {
            AnimatedSprites.SPIDER_ATTACK,
            AnimatedSprites.SPIDER_DEATH,
            AnimatedSprites.SPIDER_WALK
    };

    private Gallery() {
        // Utility class.
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            IsometricRenderer renderer = new IsometricRenderer(0, 0);
            renderer.setZoom(ZOOM);
            renderer.setDebug(false);
            GalleryPanel scene = new GalleryPanel(WINDOW_WIDTH, WINDOW_HEIGHT, renderer);
            scene.setStaticShadowsEnabled(true);
            GalleryCamera camera = new GalleryCamera(renderer, scene);
            camera.centerOnGround();
            camera.bindKeys();
            scene.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent event) {
                    camera.centerOnGround();
                    scene.repaint();
                }
            });

            addBaseGround(scene);
            addGroundGallery(scene, 1, 1);
            addConstructionGallery(scene, 1, 12);
            addObjectGallery(scene, 1, 19);
            addStairGallery(scene, 13, 1);
            addWallGallery(scene, 13, 8);
            addCornerGallery(scene, 24, 8);
            addAnimatedSamples(scene, 13, 22);
            scene.prepareStaticShadows();

            JFrame window = new JFrame("Mazing - Gallery");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setContentPane(scene);
            window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            new Timer(1000 / 24, event -> {
                camera.update();
                scene.animationTick();
                scene.repaint();
            }).start();
        });
    }

    /** Camera panning is screen-relative and survives window resizing. */
    private static final class GalleryCamera {
        private final IsometricRenderer renderer;
        private final GalleryPanel scene;
        private int offsetX;
        private int offsetY;
        private boolean left;
        private boolean right;
        private boolean up;
        private boolean down;

        private GalleryCamera(IsometricRenderer renderer, GalleryPanel scene) {
            this.renderer = renderer;
            this.scene = scene;
        }

        private void centerOnGround() {
            // Project the parterre's center with a zero origin, then add pan.
            renderer.setOrigin(0, 0);
            var center = renderer.projectWorld(
                    GROUND_WIDTH / 2.0, GROUND_HEIGHT / 2.0, 0);
            int width = scene.getWidth() > 0 ? scene.getWidth() : WINDOW_WIDTH;
            int height = scene.getHeight() > 0 ? scene.getHeight() : WINDOW_HEIGHT;
            renderer.setOrigin(width / 2 - center.x() + offsetX,
                    height / 2 - center.y() + offsetY);
        }

        private void update() {
            int dx = (right ? 1 : 0) - (left ? 1 : 0);
            int dy = (down ? 1 : 0) - (up ? 1 : 0);
            if (dx != 0 || dy != 0) {
                int panX = dx * CAMERA_STEP;
                int panY = dy * CAMERA_STEP;
                offsetX += panX;
                offsetY += panY;
                renderer.pan(panX, panY);
            }
        }

        private void bindKeys() {
            bindArrow("LEFT", () -> left = true, () -> left = false);
            bindArrow("RIGHT", () -> right = true, () -> right = false);
            bindArrow("UP", () -> up = true, () -> up = false);
            bindArrow("DOWN", () -> down = true, () -> down = false);
            bind("HOME", "reset-camera", () -> {
                offsetX = 0;
                offsetY = 0;
                centerOnGround();
                scene.repaint();
            });
            bind("L", "toggle-labels", () -> {
                scene.setLabelsVisible(!scene.areLabelsVisible());
                scene.repaint();
            });
        }

        private void bindArrow(String key, Runnable pressed, Runnable released) {
            bind("pressed " + key, "pan-" + key + "-pressed", pressed);
            bind("released " + key, "pan-" + key + "-released", released);
        }

        private void bind(String keystroke, String actionName, Runnable action) {
            InputMap input = scene.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap actions = scene.getActionMap();
            input.put(KeyStroke.getKeyStroke(keystroke), actionName);
            actions.put(actionName, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    action.run();
                }
            });
        }
    }

    /** Covers the scene with a large stone parterre used by every sample. */
    private static void addBaseGround(ViewPanel scene) {
        for (int x = 0; x < GROUND_WIDTH; x++) {
            for (int y = 0; y < GROUND_HEIGHT; y++) {
                scene.addRenderable(new FloorTile(
                        IsometricSprites.STONE,
                        Orientation.NORTH,
                        x, y, 0));
            }
        }
    }

    /** Places one instance of every floor sprite directly on the base ground. */
    private static void addGroundGallery(GalleryPanel scene, int startX, int startY) {
        addFloorSprites(scene, GROUND_SPRITES, startX, startY, 4);
    }

    /** Presents the construction/support family on a regular grid. */
    private static void addConstructionGallery(GalleryPanel scene,
            int startX, int startY) {
        addEntities(scene, CONSTRUCTION_SPRITES, startX, startY, 3,
                PlacementMode.OCCLUDING, Orientation.NORTH);
    }

    /** Presents every furniture/object sprite once. */
    private static void addObjectGallery(GalleryPanel scene,
            int startX, int startY) {
        addEntities(scene, OBJECT_SPRITES, startX, startY, 4,
                PlacementMode.ENTITY, Orientation.NORTH);
    }

    /** Displays the available stairs and ramp-related sprites. */
    private static void addStairGallery(GalleryPanel scene, int startX, int startY) {
        addEntities(scene, STAIR_SPRITES, startX, startY, 2,
                PlacementMode.ENTITY, Orientation.NORTH);
    }

    /** Displays all wall and doorway variants with their standard occlusion. */
    private static void addWallGallery(GalleryPanel scene, int startX, int startY) {
        addEntities(scene, WALL_SPRITES, startX, startY, 4,
                PlacementMode.OCCLUDING, Orientation.NORTH);
    }

    /** Shows all four corner orientations, including their automatic wall faces. */
    private static void addCornerGallery(GalleryPanel scene, int startX, int startY) {
        addCorner(scene, Orientation.SOUTH_WEST, startX, startY);
        addCorner(scene, Orientation.SOUTH_EAST, startX + 2, startY);
        addCorner(scene, Orientation.NORTH_WEST, startX, startY + 2);
        addCorner(scene, Orientation.NORTH_EAST, startX + 2, startY + 2);
    }

    private static void addCorner(GalleryPanel scene, Orientation orientation,
            int x, int y) {
        scene.addRenderable(new OccludingEntity(
                IsometricSprites.STONE_WALL_CORNER, orientation, x, y, 0));
        scene.addLabel(IsometricSprites.STONE_WALL_CORNER, orientation, x, y);
    }

    /** Displays all Crusader and Spider animations, each with its API name. */
    private static void addAnimatedSamples(GalleryPanel scene, int startX, int startY) {
        addAnimatedFamily(scene, CRUSADER_ANIMATIONS, Orientation.SOUTH,
                startX, startY, 4);
        addAnimatedFamily(scene, SPIDER_ANIMATIONS, Orientation.SOUTH,
                startX, startY + 6, 4);
    }

    /** Places each animated specimen at a distinct position on the parterre. */
    private static void addAnimatedFamily(GalleryPanel scene,
            AnimatedOrientedSprite[] animations, Orientation direction,
            int startX, int startY, int columns) {
        for (int index = 0; index < animations.length; index++) {
            AnimatedOrientedSprite animation = animations[index];
            int x = startX + (index % columns) * 4;
            int y = startY + (index / columns) * 3;
            // Keep the existing walking reference facing east.
            scene.addRenderable(new AnimatedEntity(
                    animation, direction, x, y, 0, 2, ShadowMode.WITH_SHADOW));
            scene.addLabel(animation, direction, x, y);
        }
    }

    /** Places floor sprites in a compact regular catalogue. */
    private static void addFloorSprites(GalleryPanel scene,
            Sprite[] sprites, int startX, int startY, int columns) {
        for (int index = 0; index < sprites.length; index++) {
            int column = index % columns;
            int row = index / columns;
            int x = startX + column * 2;
            int y = startY + row * 2;
            scene.addRenderable(new FloorTile(
                    sprites[index], Orientation.NORTH, x, y, 0));
            scene.addLabel(sprites[index], Orientation.NORTH, x, y);
        }
    }

    /** Places entity-like samples in a compact regular catalogue. */
    private static void addEntities(GalleryPanel scene,
            Sprite[] sprites, int startX, int startY, int columns,
            PlacementMode mode, Orientation orientation) {
        for (int index = 0; index < sprites.length; index++) {
            int column = index % columns;
            int row = index / columns;
            int x = startX + column * 2;
            int y = startY + row * 2;
            switch (mode) {
                case ENTITY -> scene.addRenderable(new Entity(
                        sprites[index], orientation, x, y, 0));
                case OCCLUDING -> scene.addRenderable(new OccludingEntity(
                        sprites[index], orientation, x, y, 0));
            }
            scene.addLabel(sprites[index], orientation, x, y);
        }
    }

    /**
     * Scene-local labels drawn on top of the world, with no effect on sprite
     * rendering, sorting, shadows, or occlusion. They use the same projection
     * as their objects and therefore follow camera panning automatically.
     */
    private static final class GalleryPanel extends ViewPanel {
        private static final Font LABEL_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 10);
        private static final Color LABEL_BACKGROUND = new Color(250, 250, 245, 221);
        private static final Color LABEL_FOREGROUND = new Color(25, 33, 45);
        private static final Color HELP_BACKGROUND = new Color(25, 33, 45, 216);
        private static final Color HELP_FOREGROUND = Color.WHITE;
        private static final int LABEL_LINE_HEIGHT = 12;
        private final IsometricRenderer renderer;
        private final List<Caption> captions = new ArrayList<>();
        private final Map<Sprite, String> names = new IdentityHashMap<>();
        private boolean labelsVisible = true;

        private record Caption(String name, double x, double y, double z) { }

        private GalleryPanel(int width, int height, IsometricRenderer renderer) {
            super(width, height, renderer);
            this.renderer = renderer;
            IsometricSprites.REGISTRY.forEach((name, sprite) ->
                    names.put(sprite, name.toUpperCase(Locale.ROOT)));
            AnimatedSprites.REGISTRY.forEach((name, sprite) ->
                    names.put(sprite, name.toUpperCase(Locale.ROOT)));
        }

        private void addLabel(Sprite sprite, Orientation orientation, double x, double y) {
            String name = names.get(sprite);
            if (name == null) {
                throw new IllegalArgumentException("Unregistered gallery sprite: " + sprite);
            }
            if (sprite == IsometricSprites.STONE_WALL_CORNER) {
                name += " [" + orientation.name() + "]";
            }
            captions.add(new Caption(name, x, y, 0));
        }

        private boolean areLabelsVisible() {
            return labelsVisible;
        }

        private void setLabelsVisible(boolean visible) {
            labelsVisible = visible;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setFont(LABEL_FONT);
                if (labelsVisible) {
                    paintCaptions(g);
                }
                paintHelp(g);
            } finally {
                g.dispose();
            }
        }

        private void paintCaptions(Graphics2D g) {
            FontMetrics metrics = g.getFontMetrics();
            List<Rectangle> occupied = new ArrayList<>();
            Rectangle visible = new Rectangle(0, 0, getWidth(), getHeight());
            for (Caption caption : captions) {
                var position = renderer.projectWorld(
                        caption.x() + 0.5, caption.y() + 0.5, caption.z());
                List<String> lines = wrapApiName(caption.name());
                int width = lines.stream().mapToInt(metrics::stringWidth).max().orElse(0) + 12;
                int height = lines.size() * LABEL_LINE_HEIGHT + 6;
                Rectangle box = new Rectangle(position.x() - width / 2,
                        position.y() + 16, width, height);
                // Labels are intentionally overlaid, but never on top of each
                // other: push each caption down if the cells are crowded.
                for (int tries = 0; tries < 32; tries++) {
                    boolean collision = occupied.stream().anyMatch(other ->
                            other.intersects(box));
                    if (!collision) {
                        break;
                    }
                    box.y += height + 3;
                }
                occupied.add(new Rectangle(box));
                if (!visible.intersects(box)) {
                    continue;
                }
                g.setColor(LABEL_BACKGROUND);
                g.fillRoundRect(box.x, box.y, box.width, box.height, 7, 7);
                g.setColor(LABEL_FOREGROUND);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    g.drawString(line,
                            box.x + (box.width - metrics.stringWidth(line)) / 2,
                            box.y + 14 + i * LABEL_LINE_HEIGHT);
                }
            }
        }

        /** Splits long API identifiers only at underscores; their spelling stays intact. */
        private static List<String> wrapApiName(String name) {
            if (name.length() <= 19) {
                return List.of(name);
            }
            int target = Math.min(19, name.length() / 2 + 1);
            int breakAt = name.lastIndexOf('_', target);
            if (breakAt < 6) {
                breakAt = name.indexOf('_', target);
            }
            if (breakAt < 6) {
                return List.of(name);
            }
            return List.of(name.substring(0, breakAt + 1),
                    name.substring(breakAt + 1));
        }

        private void paintHelp(Graphics2D g) {
            String message = "Flèches : déplacer  |  Home : recentrer  |  L : noms "
                    + (labelsVisible ? "(affichés)" : "(masqués)");
            FontMetrics fm = g.getFontMetrics();
            int width = fm.stringWidth(message) + 22;
            g.setColor(HELP_BACKGROUND);
            g.fillRoundRect(10, 10, width, 29, 10, 10);
            g.setColor(HELP_FOREGROUND);
            g.drawString(message, 21, 29);
        }
    }

    /** Minimal placement categories used by the static gallery helpers. */
    private enum PlacementMode {
        ENTITY,
        OCCLUDING
    }
}
