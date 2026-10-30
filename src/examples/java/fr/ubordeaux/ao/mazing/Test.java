
package fr.ubordeaux.ao.mazing;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.entity.AnimatedEntity;
import fr.ubordeaux.ao.mazing.view.entity.FloorTile;
import fr.ubordeaux.ao.mazing.view.entity.ShadowMode;
import fr.ubordeaux.ao.mazing.view.sound.CrusaderCombatSounds;
import fr.ubordeaux.ao.mazing.view.sound.CrusaderStaticSounds;
import fr.ubordeaux.ao.mazing.view.sound.FootstepSound;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedOrientedSprite;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedSprites;
import fr.ubordeaux.ao.mazing.view.sprite.IsometricSprites;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Interactive demonstration of Mazing.
 *
 * <p>The Crusader moves automatically according to its animation:
 * WALK moves slowly, RUN moves quickly, and all other animations
 * keep the character stationary.
 *
 * <p>When the Crusader reaches the edge of the map, it switches
 * automatically to IDLE.
 *
 * <p>Sound effects are synthesized in Java and synchronized
 * with animation frames.
 */
public final class Test {

    private static final int SCENE_WIDTH = 800;
    private static final int SCENE_HEIGHT = 600;
    private static final int MAP_SIZE = 10;

    private static final int ANIMATION_FPS = 24;
    private static final int CAMERA_STEP = 32;

    private static final double WALK_SPEED = 1.0;
    private static final double RUN_SPEED = 3.0;

    private static final int MIN_ZOOM = 1;
    private static final int MAX_ZOOM = 8;

    private static final double DIAGONAL_FACTOR =
            1.0 / Math.sqrt(2.0);

    private static final Orientation[] DIRECTIONS = {
        Orientation.NORTH,
        Orientation.NORTH_EAST,
        Orientation.EAST,
        Orientation.SOUTH_EAST,
        Orientation.SOUTH,
        Orientation.SOUTH_WEST,
        Orientation.WEST,
        Orientation.NORTH_WEST
    };

    /**
     * Available animations and their movement properties.
     */
    private enum CrusaderAnimation {

        IDLE("Repos", AnimatedSprites.CRUSADER_IDLE),
        WALK("Marche", AnimatedSprites.CRUSADER_WALK),
        RUN("Course", AnimatedSprites.CRUSADER_RUN),
        JUMP("Saut", AnimatedSprites.CRUSADER_JUMP),
        ATTACK("Attaque", AnimatedSprites.CRUSADER_ATTACK),
        BLOCK("Defense", AnimatedSprites.CRUSADER_BLOCK),
        HIT("Coup recu", AnimatedSprites.CRUSADER_HIT),
        DEATH("Mort", AnimatedSprites.CRUSADER_DEATH);

        private final String label;
        private final AnimatedOrientedSprite sprite;

        CrusaderAnimation(
                String label,
                AnimatedOrientedSprite sprite) {

            this.label = label;
            this.sprite = sprite;
        }

        boolean moves() {
            return this == WALK || this == RUN;
        }

        double movementSpeed() {
            return switch (this) {
                case WALK -> WALK_SPEED;
                case RUN -> RUN_SPEED;
                default -> 0.0;
            };
        }

        double animationSpeed() {
            return this == RUN ? 1.5 : 1.0;
        }
    }

    private final Scene scene;
    private final AnimatedEntity crusader;

    private final FootstepSound footsteps;
    private final CrusaderCombatSounds combatSounds;
    private final CrusaderStaticSounds staticSounds;

    private final JLabel legend;

    private CrusaderAnimation animation = CrusaderAnimation.IDLE;

    private Timer timer;
    private long previousFrameNanos;

    private int zoom = 2;
    private boolean debug;
    private boolean movingThisFrame;
    private boolean closed;

    private Test() {
        scene = new Scene(
                SCENE_WIDTH,
                SCENE_HEIGHT,
                MAP_SIZE,
                MAP_SIZE);

        scene.setZoom(zoom);
        scene.setDebug(debug);

        createFloor();

        crusader = new AnimatedEntity(
                AnimatedSprites.CRUSADER_IDLE,
                Orientation.SOUTH,
                MAP_SIZE / 2,
                MAP_SIZE / 2,
                0,
                2,
                ShadowMode.WITH_SHADOW);

        scene.addRenderable(crusader);

        footsteps = new FootstepSound();
        combatSounds = new CrusaderCombatSounds();
        staticSounds = new CrusaderStaticSounds();

        registerCrusaderSounds();

        legend = new JLabel();
        installLegend();
        installKeyboard();
        updateLegend();
    }

    /**
     * Creates a square stone floor.
     */
    private void createFloor() {
        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {
                scene.addRenderable(new FloorTile(
                        IsometricSprites.STONE,
                        Orientation.NORTH,
                        x, y, 0));
            }
        }
    }

    /**
     * Registers sound effects at specific animation frames.
     */
    private void registerCrusaderSounds() {
        crusader.setAnimationFrameListener(frame -> {
            var sprite = crusader.getSprite();

            if (sprite == AnimatedSprites.CRUSADER_WALK
                    || sprite == AnimatedSprites.CRUSADER_RUN) {

                // Two footsteps per animation cycle.
                // The sound remains silent if movement is blocked.
                if (movingThisFrame && (frame == 1 || frame == 8)) {
                    footsteps.play();
                }

            } else if (sprite == AnimatedSprites.CRUSADER_JUMP) {

                if (frame == 1) {
                    staticSounds.playJumpTakeoff();
                } else if (frame == sprite.animationSize() - 2) {
                    staticSounds.playJumpLanding();
                }

            } else if (sprite == AnimatedSprites.CRUSADER_IDLE) {

                if (frame == 1) {
                    staticSounds.playIdleBreath();
                }

            } else if (sprite == AnimatedSprites.CRUSADER_BLOCK) {

                if (frame == 3) {
                    staticSounds.playShieldBlock();
                }

            } else if (sprite == AnimatedSprites.CRUSADER_ATTACK) {

                if (frame == sprite.animationSize() - 2) {
                    combatSounds.playAttack();
                }

            } else if (sprite == AnimatedSprites.CRUSADER_HIT) {

                if (frame == 1) {
                    combatSounds.playHit();
                }

            } else if (sprite == AnimatedSprites.CRUSADER_DEATH) {

                if (frame == 1) {
                    combatSounds.playDeathCry();
                } else if (frame == sprite.animationSize() - 1) {
                    combatSounds.playDeathExhale();
                }
            }

            // Stationary action animations play once.
            // WALK, RUN and IDLE remain cyclic.
            if (!animation.moves()
                    && animation != CrusaderAnimation.IDLE
                    && frame == sprite.animationSize() - 1) {

                crusader.setAnimationPaused(true);
            }
        });
    }

    /**
     * Updates the Crusader's position based on its animation
     * and orientation.
     *
     * <p>When the character reaches the map boundary,
     * it switches automatically to IDLE.
     */
    private void updateCrusader(double elapsedSeconds) {
        movingThisFrame = false;

        if (!animation.moves()) {
            return;
        }

        double[] direction =
                directionVector(crusader.getOrientation());

        double distance =
                animation.movementSpeed() * elapsedSeconds;

        double oldX = crusader.getPreciseX();
        double oldY = crusader.getPreciseY();

        double nextX = clamp(
                oldX + direction[0] * distance,
                0,
                MAP_SIZE - 1);

        double nextY = clamp(
                oldY + direction[1] * distance,
                0,
                MAP_SIZE - 1);

        double dx = nextX - oldX;
        double dy = nextY - oldY;

        if (dx == 0.0 && dy == 0.0) {
            stopCrusader();
            return;
        }

        crusader.moveBy(dx, dy);
        movingThisFrame = true;

        // Detect whether the Crusader has reached a boundary
        // in its current direction of movement.
        boolean reachedBorder =
                (direction[0] > 0 && nextX >= MAP_SIZE - 1)
                || (direction[0] < 0 && nextX <= 0)
                || (direction[1] > 0 && nextY >= MAP_SIZE - 1)
                || (direction[1] < 0 && nextY <= 0);

        if (reachedBorder) {
            stopCrusader();
        }
    }

    /**
     * Stops movement and switches the Crusader to IDLE.
     */
    private void stopCrusader() {
        setAnimation(CrusaderAnimation.IDLE);
    }

    /**
     * Returns a normalized world-space direction.
     */
    private static double[] directionVector(
            Orientation orientation) {

        return switch (orientation) {
            case NORTH -> new double[] {0.0, 1.0};
            case NORTH_EAST -> diagonal(1.0, 1.0);
            case EAST -> new double[] {1.0, 0.0};
            case SOUTH_EAST -> diagonal(1.0, -1.0);
            case SOUTH -> new double[] {0.0, -1.0};
            case SOUTH_WEST -> diagonal(-1.0, -1.0);
            case WEST -> new double[] {-1.0, 0.0};
            case NORTH_WEST -> diagonal(-1.0, 1.0);
        };
    }

    private static double[] diagonal(double x, double y) {
        return new double[] {
            x * DIAGONAL_FACTOR,
            y * DIAGONAL_FACTOR
        };
    }

    private static double clamp(
            double value,
            double minimum,
            double maximum) {

        return Math.max(
                minimum,
                Math.min(maximum, value));
    }

    /**
     * Rotates the Crusader by one 45-degree step.
     */
    private void rotateCrusader(int step) {
        Orientation current = crusader.getOrientation();

        for (int index = 0; index < DIRECTIONS.length; index++) {
            if (DIRECTIONS[index] == current) {
                int next = Math.floorMod(
                        index + step,
                        DIRECTIONS.length);

                crusader.setOrientation(DIRECTIONS[next]);

                updateLegend();
                scene.repaint();
                return;
            }
        }
    }

    /**
     * Selects the next animation.
     */
    private void selectNextAnimation() {
        CrusaderAnimation[] animations =
                CrusaderAnimation.values();

        int next = (animation.ordinal() + 1)
                % animations.length;

        setAnimation(animations[next]);
    }

    /**
     * Changes the Crusader's animation and resets its playback state.
     */
    private void setAnimation(CrusaderAnimation nextAnimation) {
        animation = nextAnimation;
        movingThisFrame = false;

        crusader.setSprite(animation.sprite);
        crusader.setAnimationPaused(false);
        crusader.setAnimationSpeed(animation.animationSpeed());

        updateLegend();
        scene.repaint();
    }

    private void changeZoom(int delta) {
        zoom = Math.max(
                MIN_ZOOM,
                Math.min(MAX_ZOOM, zoom + delta));

        scene.setZoom(zoom);
        updateLegend();
        scene.repaint();
    }

    private void toggleDebug() {
        debug = !debug;

        scene.setDebug(debug);
        updateLegend();
        scene.repaint();
    }

    /**
     * Adds a permanent help legend in the upper-left corner.
     */
    private void installLegend() {
        JComponent component = scene.getComponent();
        component.setLayout(null);

        legend.setOpaque(true);
        legend.setBackground(new Color(25, 33, 45));
        legend.setForeground(Color.WHITE);
        legend.setFont(new Font(
                Font.MONOSPACED,
                Font.PLAIN,
                12));

        legend.setBounds(10, 10, 355, 245);
        component.add(legend);
    }

    private void updateLegend() {
        String movementStatus = switch (animation) {
            case WALK -> "Marche automatique";
            case RUN -> "Course automatique";
            default -> "Immobile";
        };

        legend.setText("""
                <html>
                <div style='padding:10px'>
                <b>MAZING - TEST INTERACTIF</b><br><br>
                L : animation suivante<br>
                Q / E : tourner de 45 degres<br>
                Fleches : deplacer la camera<br>
                C : recentrer la camera<br>
                + / - : zoom<br>
                B : debug ON / OFF<br>
                ESC : quitter<br><br>
                <b>Animation : %s</b><br>
                <b>Comportement : %s</b><br>
                <b>Orientation : %s</b><br>
                <b>Zoom : %d - Debug : %s</b>
                </div>
                </html>
                """.formatted(
                    animation.label,
                    movementStatus,
                    crusader.getOrientation(),
                    zoom,
                    debug ? "ON" : "OFF"));
    }

    /**
     * Registers keyboard controls.
     */
    private void installKeyboard() {
        JComponent component = scene.getComponent();

        bind(component, "L", "animation",
                this::selectNextAnimation);

        bind(component, "Q", "rotate-left",
                () -> rotateCrusader(-1));

        bind(component, "E", "rotate-right",
                () -> rotateCrusader(1));

        bind(component, "LEFT", "camera-left",
                () -> scene.moveScene(-CAMERA_STEP, 0));

        bind(component, "RIGHT", "camera-right",
                () -> scene.moveScene(CAMERA_STEP, 0));

        bind(component, "UP", "camera-up",
                () -> scene.moveScene(0, -CAMERA_STEP));

        bind(component, "DOWN", "camera-down",
                () -> scene.moveScene(0, CAMERA_STEP));

        bind(component, "C", "center",
                scene::centerMap);

        bind(component, "EQUALS", "zoom-in",
                () -> changeZoom(1));

        bind(component, "shift EQUALS", "zoom-in-shift",
                () -> changeZoom(1));

        bind(component, "ADD", "zoom-in-keypad",
                () -> changeZoom(1));

        bind(component, "MINUS", "zoom-out",
                () -> changeZoom(-1));

        bind(component, "SUBTRACT", "zoom-out-keypad",
                () -> changeZoom(-1));

        // B replaces F3 for debug mode.
        bind(component, "B", "debug",
                this::toggleDebug);

        bind(component, "ESCAPE", "close",
                this::close);
    }

    /**
     * Associates a key with an action.
     */
    private static void bind(
            JComponent component,
            String key,
            String name,
            Runnable action) {

        KeyStroke stroke = KeyStroke.getKeyStroke(key);

        if (stroke == null) {
            throw new IllegalArgumentException(
                    "Invalid key: " + key);
        }

        component.getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(stroke, name);

        component.getActionMap().put(
                name,
                new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        action.run();
                    }
                });
    }

    /**
     * Starts the animation and movement loop.
     */
    private void start() {
        scene.show();

        previousFrameNanos = System.nanoTime();

        timer = new Timer(
                1000 / ANIMATION_FPS,
                event -> tick());

        timer.start();
    }

    /**
     * Advances movement and sprite animation.
     */
    private void tick() {
        long now = System.nanoTime();

        double elapsedSeconds =
                (now - previousFrameNanos)
                        / 1_000_000_000.0;

        previousFrameNanos = now;

        // Avoid large movements after a system pause.
        elapsedSeconds = Math.min(elapsedSeconds, 0.1);

        updateCrusader(elapsedSeconds);

        scene.animationTick();
        scene.repaint();
    }

    /**
     * Stops animation and releases audio resources.
     */
    private void close() {
        if (closed) {
            return;
        }

        closed = true;

        if (timer != null) {
            timer.stop();
            timer = null;
        }

        footsteps.close();
        combatSounds.close();
        staticSounds.close();

        scene.close();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new Test().start());
    }
}
