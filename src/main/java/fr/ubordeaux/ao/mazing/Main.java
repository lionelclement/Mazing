package fr.ubordeaux.ao.mazing;

import static fr.ubordeaux.ao.mazing.view.entity.ShadowMode.WITH_SHADOW;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.entity.AnimatedEntity;
import fr.ubordeaux.ao.mazing.view.entity.Entity;
import fr.ubordeaux.ao.mazing.view.entity.FloorTile;
import fr.ubordeaux.ao.mazing.view.renderer.IsometricRenderer;
import fr.ubordeaux.ao.mazing.view.sound.CrusaderCombatSounds;
import fr.ubordeaux.ao.mazing.view.sound.CrusaderStaticSounds;
import fr.ubordeaux.ao.mazing.view.sound.FootstepSound;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedSprites;
import fr.ubordeaux.ao.mazing.view.sprite.IsometricSprites;
import fr.ubordeaux.ao.mazing.view.swing.ViewPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Shows an isometric scene with animated characters and sounds.
 */
public final class Main {

    // Scene size and tile dimensions.
    private static final int GRID_SIZE = 5;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int ORIGIN_X = 150;
    private static final int ORIGIN_Y = 320;
    private static final int TILE_DIAGONAL_X = 256;
    private static final int TILE_DIAGONAL_Y = 128;
    private static final int TILE_HEIGHT = 60;

    // Screen refresh rate and sprite animation rate.
    private static final int RENDER_FPS = 60;
    private static final int ANIMATION_FPS = 24;
    private static final double MAX_ELAPSED_SECONDS = 0.05;

    // Limits of the square path.
    private static final int MIN_POSITION = 1;
    private static final int MAX_POSITION = 3;

    /** Starts the application. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowScene);
    }

    /** Creates the scene and starts the animation. */
    private static void createAndShowScene() {
        ViewPanel scene = createScene();

        AnimatedEntity crusader = createCrusader();
        registerCrusaderSounds(crusader);
        scene.addRenderable(crusader);
        scene.addRenderable(createSpider());

        CrusaderDemoSequence demo = new CrusaderDemoSequence(crusader);
        showWindow(scene);
        startAnimationLoop(scene, demo);
    }

    /** Creates the renderer and the scene. */
    private static ViewPanel createScene() {
        IsometricRenderer renderer = new IsometricRenderer(
                ORIGIN_X, ORIGIN_Y,
                TILE_DIAGONAL_X, TILE_DIAGONAL_Y, TILE_HEIGHT);
        renderer.setDebug(false);

        ViewPanel scene = new ViewPanel(WINDOW_WIDTH, WINDOW_HEIGHT, renderer);
        addFloor(scene);

        // Add the chair and the walls.
        addObjects(scene);
        addWalls(scene);
        return scene;
    }

    /** Creates the crusader and its shadow. */
    private static AnimatedEntity createCrusader() {
        // On screen, this sprite orientation faces east.
        return new AnimatedEntity(
                AnimatedSprites.CRUSADER_WALK,
                Orientation.NORTH,
                MIN_POSITION, MIN_POSITION, 0, 2, WITH_SHADOW);
    }

    /** Creates an animated spider that stays in place. */
    private static AnimatedEntity createSpider() {
        AnimatedEntity spider = new AnimatedEntity(
                AnimatedSprites.SPIDER_WALK,
                Orientation.EAST, 4, 2, 0, 2, WITH_SHADOW);
        spider.setAnimationSpeed(0.5);
        return spider;
    }

    /** Plays sounds at specific animation frames. */
    private static void registerCrusaderSounds(AnimatedEntity crusader) {
        FootstepSound footstepsSound = new FootstepSound();
        CrusaderCombatSounds combatSound = new CrusaderCombatSounds();
        CrusaderStaticSounds stationarySound = new CrusaderStaticSounds();

        crusader.setAnimationFrameListener(frame -> {
            var sprite = crusader.getSprite();

            if (sprite == AnimatedSprites.CRUSADER_WALK
                    || sprite == AnimatedSprites.CRUSADER_RUN) {
                // Two sounds per walking or running cycle.
                if (frame == 1 || frame == 8) {
                    footstepsSound.play();
                }
            } else if (sprite == AnimatedSprites.CRUSADER_JUMP) {
                if (frame == 1) {
                    stationarySound.playJumpTakeoff();
                } else if (frame == sprite.animationSize() - 2) {
                    stationarySound.playJumpLanding();
                }
            } else if (sprite == AnimatedSprites.CRUSADER_IDLE) {
                if (frame == 1) {
                    stationarySound.playIdleBreath();
                }
            } else if (sprite == AnimatedSprites.CRUSADER_BLOCK) {
                if (frame == 3) {
                    stationarySound.playShieldBlock();
                }
            } else if (sprite == AnimatedSprites.CRUSADER_ATTACK) {
                if (frame == sprite.animationSize() - 2) {
                    combatSound.playAttack();
                }
            } else if (sprite == AnimatedSprites.CRUSADER_DEATH) {
                if (frame == 1) {
                    combatSound.playDeathCry();
                } else if (frame == sprite.animationSize() - 1) {
                    combatSound.playDeathExhale();
                }
            } else if (sprite == AnimatedSprites.CRUSADER_HIT && frame == 1) {
                combatSound.playHit();
            }
        });
    }

    /** Displays the window. */
    private static void showWindow(ViewPanel scene) {
        JFrame window = new JFrame("Mazing - Isometric Demo");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(scene);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    /** Starts the timer for movement and animation. */
    private static void startAnimationLoop(ViewPanel scene, CrusaderDemoSequence demo) {
        DemoClock clock = new DemoClock(scene, demo);
        new Timer(1000 / RENDER_FPS, event -> clock.tick()).start();
    }

    /** Adds the floor tiles. */
    private static void addFloor(ViewPanel scene) {
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                scene.addRenderable(new FloorTile(
                        IsometricSprites.STONE, Orientation.NORTH, x, y, 0));
            }
        }
    }

    /** Adds a chair to the floor. */
    private static void addObjects(ViewPanel scene) {
        // The last argument sets the size: 1.0 is the original size.
        scene.addRenderable(new Entity(
                IsometricSprites.CHAIR, Orientation.WEST, 2, 2, 0, 1.75));
    }

    /** Adds the walls around the floor. */
    private static void addWalls(ViewPanel scene) {
        for (int position = 1; position < GRID_SIZE - 1; position++) {
            scene.addRenderable(new Entity(
                    IsometricSprites.STONE_WALL,
                    Orientation.EAST, position, 0, 0));
            scene.addRenderable(new Entity(
                    IsometricSprites.STONE_WALL,
                    Orientation.NORTH, 0, position, 0));
            scene.addRenderable(new Entity(
                    IsometricSprites.STONE_WALL,
                    Orientation.WEST, position, GRID_SIZE - 1, 0));
            scene.addRenderable(new Entity(
                    IsometricSprites.STONE_WALL,
                    Orientation.SOUTH, GRID_SIZE - 1, position, 0));
        }

        int last = GRID_SIZE - 1;
        scene.addRenderable(new Entity(
                IsometricSprites.STONE_WALL_CORNER,
                Orientation.NORTH_EAST, 0, 0, 0));
        scene.addRenderable(new Entity(
                IsometricSprites.STONE_WALL_CORNER,
                Orientation.SOUTH_EAST, last, 0, 0));
        scene.addRenderable(new Entity(
                IsometricSprites.STONE_WALL_CORNER,
                Orientation.NORTH_WEST, 0, last, 0));
        scene.addRenderable(new Entity(
                IsometricSprites.STONE_WALL_CORNER,
                Orientation.SOUTH_WEST, last, last, 0));
    }

    /**
     * Updates movement at 60 FPS and sprite animation at 24 FPS.
     */
    private static final class DemoClock {

        private static final long NANOS_PER_SECOND = 1_000_000_000L;
        private static final long ANIMATION_INTERVAL =
                NANOS_PER_SECOND / ANIMATION_FPS;

        private final ViewPanel scene;
        private final CrusaderDemoSequence demo;
        private long previousTime = System.nanoTime();
        private long accumulatedAnimationNanos;

        private DemoClock(ViewPanel scene, CrusaderDemoSequence demo) {
            this.scene = scene;
            this.demo = demo;
        }

        /** Updates movement, sprite frames, and the screen. */
        private void tick() {
            long currentTime = System.nanoTime();
            long elapsedNanos = Math.max(0L, currentTime - previousTime);
            previousTime = currentTime;

            // Limit large jumps after a pause.
            double elapsedSeconds = Math.min(
                    elapsedNanos / (double) NANOS_PER_SECOND,
                    MAX_ELAPSED_SECONDS);
            demo.update(elapsedSeconds);

            accumulatedAnimationNanos += elapsedNanos;
            while (accumulatedAnimationNanos >= ANIMATION_INTERVAL) {
                scene.animationTick();
                accumulatedAnimationNanos -= ANIMATION_INTERVAL;
            }

            scene.repaint();
        }
    }

    /** Lists the crusader animations in order. */
    private enum CrusaderDemoPhase {
        WALK,
        RUN,
        JUMP,
        IDLE,
        BLOCK,
        IDLE_BEFORE_ATTACK,
        ATTACK,
        IDLE_BEFORE_DEATH,
        DEATH,
        IDLE_BEFORE_HIT,
        HIT
    }

    /**
     * Runs the crusader animations in sequence.
     * Walking and running make one full lap. Other animations stay in place.
     */
    private static final class CrusaderDemoSequence {
    private static final double WALK_SPEED = 0.75; // Tiles per second.
		private static final double RUN_SPEED = 1.50;
        private static final double CORNER_TURN_SECONDS = 0.12;
        private static final double BEFORE_ACTION_IDLE_SECONDS = 0.6;
        private static final double LAP_DISTANCE =
                4.0 * (MAX_POSITION - MIN_POSITION);
        private static final double EPSILON = 1e-8;

        private final AnimatedEntity crusader;
        private CrusaderDemoPhase phase = CrusaderDemoPhase.WALK;
        private Orientation gridDirection = Orientation.EAST;

        private double traveledDistance;
        private double remainingCornerTurnSeconds;
        private double elapsedPhaseSeconds;
        private double phaseDurationSeconds;

        private CrusaderDemoSequence(AnimatedEntity crusader) {
            this.crusader = crusader;
            configurePhase();
        }

        /** Updates the current animation phase. */
        private void update(double elapsedSeconds) {
            if (elapsedSeconds <= 0.0) {
                return;
            }

            if (phase == CrusaderDemoPhase.WALK || phase == CrusaderDemoPhase.RUN) {
                updateMovement(elapsedSeconds);
            } else {
                updateStationaryAnimation(elapsedSeconds);
            }
        }

        /** Moves the crusader and handles turns at the corners. */
        private void updateMovement(double elapsedSeconds) {
            double speed = phase == CrusaderDemoPhase.RUN ? RUN_SPEED : WALK_SPEED;
            double remainingTime = elapsedSeconds;

            while (remainingTime > EPSILON) {
                // Pause movement while turning.
                if (remainingCornerTurnSeconds > EPSILON) {
                    double turnTime = Math.min(
                            remainingTime, remainingCornerTurnSeconds);
                    remainingCornerTurnSeconds -= turnTime;
                    remainingTime -= turnTime;

                    if (remainingCornerTurnSeconds <= EPSILON) {
                        remainingCornerTurnSeconds = 0.0;
                        crusader.setOrientation(spriteOrientation(gridDirection));

                        // Finish the last turn before the next animation.
                        if (LAP_DISTANCE - traveledDistance <= EPSILON) {
                            advancePhase();
                            return;
                        }
                    }
                    continue;
                }

                double remainingLap = Math.max(0.0, LAP_DISTANCE - traveledDistance);
                if (remainingLap <= EPSILON) {
                    advancePhase();
                    return;
                }

                double distanceToCorner = distanceToCorner();
                double distance = Math.min(
                        Math.min(distanceToCorner, remainingLap),
                        speed * remainingTime);

                if (distance > EPSILON) {
                    moveAlongCurrentSide(distance);
                    traveledDistance += distance;
                    remainingTime -= distance / speed;
                } else if (distanceToCorner > EPSILON) {
                    return;
                }

                if (distanceToCorner - distance <= EPSILON) {
                    beginCornerTurn();
                }
            }
        }

        /** Returns the distance to the next corner. */
        private double distanceToCorner() {
            double x = crusader.getPreciseX();
            double y = crusader.getPreciseY();

            return switch (gridDirection) {
                case EAST -> Math.max(0.0, MAX_POSITION - x);
                case SOUTH -> Math.max(0.0, MAX_POSITION - y);
                case WEST -> Math.max(0.0, x - MIN_POSITION);
                case NORTH -> Math.max(0.0, y - MIN_POSITION);
                default -> throw new IllegalStateException("Expected cardinal direction");
            };
        }

        /** Moves the crusader along the current side. */
        private void moveAlongCurrentSide(double distance) {
            switch (gridDirection) {
                case EAST -> crusader.moveBy(distance, 0);
                case SOUTH -> crusader.moveBy(0, distance);
                case WEST -> crusader.moveBy(-distance, 0);
                case NORTH -> crusader.moveBy(0, -distance);
                default -> throw new IllegalStateException("Expected cardinal direction");
            }
            crusader.setOrientation(spriteOrientation(gridDirection));
        }

        /** Turns through a diagonal orientation. */
        private void beginCornerTurn() {
            Orientation diagonal = switch (gridDirection) {
                case EAST -> Orientation.NORTH_EAST;
                case SOUTH -> Orientation.SOUTH_EAST;
                case WEST -> Orientation.SOUTH_WEST;
                case NORTH -> Orientation.NORTH_WEST;
                default -> throw new IllegalStateException("Expected cardinal direction");
            };

            gridDirection = switch (gridDirection) {
                case EAST -> Orientation.SOUTH;
                case SOUTH -> Orientation.WEST;
                case WEST -> Orientation.NORTH;
                case NORTH -> Orientation.EAST;
                default -> throw new IllegalStateException("Expected cardinal direction");
            };

            crusader.setOrientation(diagonal);
            remainingCornerTurnSeconds = CORNER_TURN_SECONDS;
        }

        /**
         * Converts grid direction to sprite orientation.
         * The sprite directions are rotated by 90 degrees.
         */
        private static Orientation spriteOrientation(Orientation gridDirection) {
            return switch (gridDirection) {
                case EAST -> Orientation.NORTH;
                case SOUTH -> Orientation.EAST;
                case WEST -> Orientation.SOUTH;
                case NORTH -> Orientation.WEST;
                default -> throw new IllegalStateException("Expected cardinal direction");
            };
        }

        /** Waits for the current animation to finish. */
        private void updateStationaryAnimation(double elapsedSeconds) {
            elapsedPhaseSeconds += elapsedSeconds;
            if (elapsedPhaseSeconds >= phaseDurationSeconds) {
                advancePhase();
            }
        }

        /** Switches to the next animation. */
        private void advancePhase() {
            phase = switch (phase) {
                case WALK -> CrusaderDemoPhase.RUN;
                case RUN -> CrusaderDemoPhase.JUMP;
                case JUMP -> CrusaderDemoPhase.IDLE;
                case IDLE -> CrusaderDemoPhase.BLOCK;
                case BLOCK -> CrusaderDemoPhase.IDLE_BEFORE_ATTACK;
                case IDLE_BEFORE_ATTACK -> CrusaderDemoPhase.ATTACK;
                case ATTACK -> CrusaderDemoPhase.IDLE_BEFORE_DEATH;
                case IDLE_BEFORE_DEATH -> CrusaderDemoPhase.DEATH;
                case DEATH -> CrusaderDemoPhase.IDLE_BEFORE_HIT;
                case IDLE_BEFORE_HIT -> CrusaderDemoPhase.HIT;
                case HIT -> CrusaderDemoPhase.WALK;
            };
            configurePhase();
        }

        /** Sets the sprite, speed, and duration for this phase. */
        private void configurePhase() {
            var animation = switch (phase) {
                case WALK -> AnimatedSprites.CRUSADER_WALK;
                case RUN -> AnimatedSprites.CRUSADER_RUN;
                case JUMP -> AnimatedSprites.CRUSADER_JUMP;
                case IDLE, IDLE_BEFORE_ATTACK, IDLE_BEFORE_DEATH,
                        IDLE_BEFORE_HIT -> AnimatedSprites.CRUSADER_IDLE;
                case BLOCK -> AnimatedSprites.CRUSADER_BLOCK;
                case ATTACK -> AnimatedSprites.CRUSADER_ATTACK;
                case DEATH -> AnimatedSprites.CRUSADER_DEATH;
                case HIT -> AnimatedSprites.CRUSADER_HIT;
            };

            // Animation speed is independent of movement speed.
            double animationSpeed = phase == CrusaderDemoPhase.RUN ? 1.5 : 1.0;

            traveledDistance = 0.0;
            elapsedPhaseSeconds = 0.0;
            remainingCornerTurnSeconds = 0.0;

            if (phase == CrusaderDemoPhase.WALK || phase == CrusaderDemoPhase.RUN) {
                // Start each lap at the same corner.
                crusader.setX(MIN_POSITION);
                crusader.setY(MIN_POSITION);
                gridDirection = Orientation.EAST;
                crusader.setOrientation(spriteOrientation(gridDirection));
            }

            // Changing the sprite restarts its animation.
            crusader.setSprite(animation);
            crusader.setAnimationSpeed(animationSpeed);

            double animationDuration =
                    animation.animationSize() / (ANIMATION_FPS * animationSpeed);
            phaseDurationSeconds = switch (phase) {
                case IDLE -> Math.max(1.5, animationDuration);
                case IDLE_BEFORE_ATTACK, IDLE_BEFORE_DEATH,
                        IDLE_BEFORE_HIT -> BEFORE_ACTION_IDLE_SECONDS;
                default -> animationDuration;
            };
        }
    }
}
