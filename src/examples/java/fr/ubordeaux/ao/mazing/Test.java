package fr.ubordeaux.ao.mazing;

import static fr.ubordeaux.ao.mazing.view.entity.ShadowMode.WITH_SHADOW;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.entity.AnimatedEntity;
import fr.ubordeaux.ao.mazing.view.entity.Entity;
import fr.ubordeaux.ao.mazing.view.entity.FloorTile;
import fr.ubordeaux.ao.mazing.view.entity.FreeEntity;
import fr.ubordeaux.ao.mazing.view.entity.OccludingEntity;
import fr.ubordeaux.ao.mazing.view.renderer.IsometricRenderer;
import fr.ubordeaux.ao.mazing.view.sound.CrusaderCombatSounds;
import fr.ubordeaux.ao.mazing.view.sound.CrusaderStaticSounds;
import fr.ubordeaux.ao.mazing.view.sound.FootstepSound;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedOrientedSprite;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedSprites;
import fr.ubordeaux.ao.mazing.view.sprite.IsometricSprites;
import fr.ubordeaux.ao.mazing.view.sprite.PalisadeSprite;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowGeometry;
import fr.ubordeaux.ao.mazing.view.swing.ViewPanel;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.ActionEvent;

/**
 * Démonstration complète : décor, plateformes, escaliers, personnages,
 * déplacements et sons.
 */
public final class Test {

	// Scene size and tile dimensions.
	private static final int GRID_SIZE = 5;
	private static final int PLATFORM_SIZE = 5;
	private static final double PLATFORM_Z = 0.65;
	// The north platform begins one tile farther north and loses its south row.
	private static final int NORTH_PLATFORM_START_Y = GRID_SIZE + 2; // 7
	private static final int NORTH_PLATFORM_SIZE_Y = PLATFORM_SIZE - 1; // 4
	private static final double NORTH_PLATFORM_Z = 2.0 * PLATFORM_Z;
	// The stair sprite occupies one tile: rise only between its two half-tile edges.
	private static final double HALF_STAIR_TILE = 0.5;
	// Inset from the centers of perimeter tiles; fences need more clearance.
	private static final double PLATFORM_ROUTE_INSET = 0.10;
	private static final double PALISADE_ROUTE_INSET = PLATFORM_ROUTE_INSET + 0.10;
	private static final int WINDOW_WIDTH = 1280;
	private static final int WINDOW_HEIGHT = 800;
	private static final int ORIGIN_X = WINDOW_WIDTH / 2 - 256;
	private static final int ORIGIN_Y = WINDOW_HEIGHT / 2;
	
	// Screen refresh rate and sprite animation rate.
	private static final int RENDER_FPS = 60;
	private static final int ANIMATION_FPS = 24;
	private static final double MAX_ELAPSED_SECONDS = 0.05;
	private static final double CAMERA_SPEED = 280.0; // Pixels per second.

	// Limits of the square path.
	private static final int MIN_POSITION = 1;
	private static final int MAX_POSITION = 3;

	/** Starts the application. */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(Test::createAndShowScene);
	}

	/** Creates the scene and starts the animation. */
	private static void createAndShowScene() {
		IsometricRenderer renderer = createRenderer();
		ViewPanel scene = createScene(renderer);
		CameraController camera = new CameraController(scene, renderer);
		FreeEntity chair = createChair();
		scene.addRenderable(chair);

		AnimatedEntity firstCrusader = createCrusader();
		AnimatedEntity secondCrusader = createCrusader();
		registerCrusaderSounds(firstCrusader);
		registerCrusaderSounds(secondCrusader);
		scene.addRenderable(firstCrusader);
		scene.addRenderable(secondCrusader);

		// Build static shadows once, before the rendering timer starts.
		scene.prepareStaticShadows();
		CrusaderDuoSequence demo = new CrusaderDuoSequence(
				firstCrusader, secondCrusader);
		showWindow(scene);
		startAnimationLoop(scene, demo, camera, chair);
	}

	/** Creates the isometric renderer. */
	private static IsometricRenderer createRenderer() {
		IsometricRenderer renderer = new IsometricRenderer(ORIGIN_X, ORIGIN_Y);
		renderer.setDebug(false);
		return renderer;
	}

	/** Creates the scene and its objects. */
	private static ViewPanel createScene(IsometricRenderer renderer) {
		ViewPanel scene = new ViewPanel(WINDOW_WIDTH, WINDOW_HEIGHT, renderer);
		scene.setStaticShadowsEnabled(true);
		// Construit le décor avant d'ajouter les personnages.
		addWalls(scene);
		addFloor(scene);
		addPlatformColumns(scene);
		addUpperPlatforms(scene);
		addPlatformPalisades(scene);
		addStairs(scene);
		return scene;
	}

	/** Creates the crusader and its shadow. */
	private static AnimatedEntity createCrusader() {
		AnimatedEntity crusader = new AnimatedEntity(
				AnimatedSprites.CRUSADER_WALK,
				Orientation.EAST,
				MIN_POSITION, MIN_POSITION, 0, 2, WITH_SHADOW);
		crusader.setScale(1.00);
		return crusader;
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

			// Hold the last frame of every stationary animation.
			if (sprite != AnimatedSprites.CRUSADER_WALK
					&& sprite != AnimatedSprites.CRUSADER_RUN
					&& frame == sprite.animationSize() - 1) {
				crusader.setAnimationPaused(true);
			}
		});
	}

	/** Displays the window. */
	private static void showWindow(ViewPanel scene) {
		JFrame window = new JFrame("Mazing - Isometric Demo");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(scene);
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setExtendedState(window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	/** Starts the timer and installs the playback shortcuts. */
	private static void startAnimationLoop(
			ViewPanel scene, CrusaderDuoSequence demo, CameraController camera,
			FreeEntity chair) {
		DemoClock clock = new DemoClock(scene, demo, camera, chair);
		bindPlaybackKeys(scene, clock);
		new Timer(1000 / RENDER_FPS, event -> clock.tick()).start();
	}

	/** Q quits the application; P pauses or resumes the demo. */
	private static void bindPlaybackKeys(ViewPanel scene, DemoClock clock) {
		scene.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("pressed Q"), "quit-demo");
		scene.getActionMap().put("quit-demo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});

		// Ignore key auto-repeat to toggle only once per P key press.
		final boolean[] pauseKeyPressed = { false };
		scene.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("pressed P"), "toggle-pause");
		scene.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke("released P"), "release-pause");
		scene.getActionMap().put("toggle-pause", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (!pauseKeyPressed[0]) {
					pauseKeyPressed[0] = true;
					clock.togglePause();
				}
			}
		});
		scene.getActionMap().put("release-pause", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				pauseKeyPressed[0] = false;
			}
		});
	}

	/** Adds one continuous 17x17 stone ground below the entire scene. */
	private static void addFloor(ViewPanel scene) {
		int groundMin = -(GRID_SIZE + 1);
		int groundMax = 2 * GRID_SIZE;
		for (int x = groundMin; x <= groundMax; x++) {
			for (int y = groundMin; y <= groundMax; y++) {
				scene.addRenderable(new FloorTile(
						IsometricSprites.STONE, Orientation.NORTH, x, y, 0));
			}
		}
	}

	/** Adds the three ordinary platforms and the higher, shorter north platform. */
	private static void addUpperPlatforms(ViewPanel scene) {
		addUpperPlatform(scene, -6, 0); // West: x = -6..-2, y = 0..4.
		addUpperPlatform(scene, 6, 0); // East: x = 6..10, y = 0..4.
		addUpperPlatform(scene, 0, -6); // South: x = 0..4, y = -6..-2.
		addUpperPlatform(scene, 0, NORTH_PLATFORM_START_Y,
				PLATFORM_SIZE, NORTH_PLATFORM_SIZE_Y, NORTH_PLATFORM_Z);
	}

	/** Adds a normal 5x5 raised wooden platform. */
	private static void addUpperPlatform(ViewPanel scene, int startX, int startY) {
		addUpperPlatform(scene, startX, startY,
				PLATFORM_SIZE, PLATFORM_SIZE, PLATFORM_Z);
	}

	/** Adds a rectangular platform at an arbitrary elevation. */
	private static void addUpperPlatform(ViewPanel scene, int startX, int startY,
			int sizeX, int sizeY, double z) {
		for (int x = startX; x < startX + sizeX; x++) {
			for (int y = startY; y < startY + sizeY; y++) {
				scene.addRenderable(new FloorTile(
						IsometricSprites.PLANKS, Orientation.NORTH, x, y, z));
			}
		}
	}

	/** Adds fences to three platforms and stone walls to the eastern platform. */
	private static void addPlatformPalisades(ViewPanel scene) {
		int middle = PLATFORM_SIZE / 2;
		addPlatformPalisade(scene, -6, 0, PalisadeSprite.X_MAX, middle);
		addEastPlatformStoneWalls(scene);
		addPlatformPalisade(scene, 0, -6, PalisadeSprite.Y_MAX, middle);
		addPlatformPalisade(scene, 0, NORTH_PLATFORM_START_Y,
				PLATFORM_SIZE, NORTH_PLATFORM_SIZE_Y, NORTH_PLATFORM_Z,
				PalisadeSprite.Y_MIN, middle);
	}

	/** Stone walls around the eastern raised platform, leaving the stair entrance open. */
	private static void addEastPlatformStoneWalls(ViewPanel scene) {
		int startX = 6;
		int startY = 0;
		int endX = startX + PLATFORM_SIZE - 1;
		int endY = startY + PLATFORM_SIZE - 1;
		int entranceY = startY + PLATFORM_SIZE / 2;

		// The API decomposes SOUTH_WEST into WEST then SOUTH automatically.
		scene.addRenderable(new OccludingEntity(
				IsometricSprites.STONE_WALL_CORNER, Orientation.SOUTH_WEST,
				startX, startY, PLATFORM_Z));
		// The south-east corner only occupies the platform level.
		scene.addRenderable(new OccludingEntity(
				IsometricSprites.STONE_WALL_CORNER, Orientation.SOUTH_EAST,
				endX, startY, PLATFORM_Z));
		// Nord-ouest : conserver le sprite de coin.
		scene.addRenderable(new OccludingEntity(
				IsometricSprites.STONE_WALL_CORNER, Orientation.NORTH_WEST,
				startX, endY, PLATFORM_Z));
		// The API decomposes NORTH_EAST into NORTH then EAST automatically.
		scene.addRenderable(new OccludingEntity(
				IsometricSprites.STONE_WALL_CORNER, Orientation.NORTH_EAST,
				endX, endY, PLATFORM_Z));

		for (int index = 1; index < PLATFORM_SIZE - 1; index++) {
			int x = startX + index;
			int y = startY + index;
			// The western stair entrance is an open arch, not a missing wall.
			scene.addRenderable(new OccludingEntity(
					y == entranceY ? IsometricSprites.STONE_WALL_DOOR_OPEN : IsometricSprites.STONE_WALL,
					Orientation.WEST, startX, y, PLATFORM_Z, 1.2));
			scene.addRenderable(new OccludingEntity(
					IsometricSprites.STONE_WALL, Orientation.EAST,
					endX, y, PLATFORM_Z));
			scene.addRenderable(new OccludingEntity(
					IsometricSprites.STONE_WALL, Orientation.SOUTH,
					x, startY, PLATFORM_Z));
			scene.addRenderable(new OccludingEntity(
					IsometricSprites.STONE_WALL, Orientation.NORTH,
					x, endY, PLATFORM_Z));
		}
	}

	/**
	 * Places palisade segments along the four platform edges, leaving one
	 * tile-wide opening at the staircase. The edge sprites are anchored to
	 * the perimeter of each floor tile, not to its center.
	 */
	private static void addPlatformPalisade(ViewPanel scene,
			int startX, int startY, PalisadeSprite entranceEdge, int entranceIndex) {
		addPlatformPalisade(scene, startX, startY,
				PLATFORM_SIZE, PLATFORM_SIZE, PLATFORM_Z, entranceEdge, entranceIndex);
	}

	/** Fences a rectangular platform, leaving one opening in its chosen edge. */
	private static void addPlatformPalisade(ViewPanel scene,
			int startX, int startY, int sizeX, int sizeY, double z,
			PalisadeSprite entranceEdge, int entranceIndex) {
		int endX = startX + sizeX - 1;
		int endY = startY + sizeY - 1;

		for (int index = 0; index < sizeY; index++) {
			addPalisadeSegment(scene, startX, startY + index, z,
					PalisadeSprite.X_MIN,
					!(entranceEdge == PalisadeSprite.X_MIN && index == entranceIndex));
			addPalisadeSegment(scene, endX, startY + index, z,
					PalisadeSprite.X_MAX,
					!(entranceEdge == PalisadeSprite.X_MAX && index == entranceIndex));
		}
		for (int index = 0; index < sizeX; index++) {
			addPalisadeSegment(scene, startX + index, startY, z,
					PalisadeSprite.Y_MIN,
					!(entranceEdge == PalisadeSprite.Y_MIN && index == entranceIndex));
			addPalisadeSegment(scene, startX + index, endY, z,
					PalisadeSprite.Y_MAX,
					!(entranceEdge == PalisadeSprite.Y_MAX && index == entranceIndex));
		}
	}

	/** Places a fence segment, or an open stone arch at its staircase entrance. */
	private static void addPalisadeSegment(ViewPanel scene,
			int x, int y, double z, PalisadeSprite edge, boolean enabled) {
		if (enabled) {
			scene.addRenderable(new OccludingEntity(
					edge, Orientation.NORTH, x, y, z));
			return;
		}

		Orientation gateSide = switch (edge) {
			case X_MIN -> Orientation.WEST;
			case X_MAX -> Orientation.EAST;
			case Y_MIN -> Orientation.SOUTH;
			case Y_MAX -> Orientation.NORTH;
		};
		scene.addRenderable(new OccludingEntity(
				IsometricSprites.STONE_WALL_GATE, gateSide, x, y, z + 0.2, 1.3));
	}

	/** Adds four supporting columns under each platform. */
	private static void addPlatformColumns(ViewPanel scene) {
		addPlatformColumns(scene, -6, 0);
		addPlatformColumns(scene, 6, 0);
		addPlatformColumns(scene, 0, -6);
		addPlatformColumns(scene, 0, NORTH_PLATFORM_START_Y,
				PLATFORM_SIZE, NORTH_PLATFORM_SIZE_Y, NORTH_PLATFORM_Z);
	}

	/**
	 * Places a full-size wooden stone column on each ground-floor corner tile.
	 */
	private static void addPlatformColumns(ViewPanel scene, int startX, int startY) {
		addPlatformColumns(scene, startX, startY,
				PLATFORM_SIZE, PLATFORM_SIZE, PLATFORM_Z);
	}

	/** Places the four unchanged-size supports below a rectangular platform. */
	private static void addPlatformColumns(ViewPanel scene, int startX, int startY,
			int sizeX, int sizeY, double platformZ) {
		int endX = startX + sizeX - 1;
		int endY = startY + sizeY - 1;
		addCornerColumn(scene, startX, startY, Orientation.WEST, platformZ);
		addCornerColumn(scene, endX, startY, Orientation.SOUTH, platformZ);
		addCornerColumn(scene, startX, endY, Orientation.NORTH, platformZ);
		addCornerColumn(scene, endX, endY, Orientation.EAST, platformZ);
	}

	/** Aligns the column foot with the center of its wooden tile. */
	private static void addCornerColumn(ViewPanel scene,
			int x, int y, Orientation orientation, double supportHeight) {
		OccludingEntity column = new OccludingEntity(
				IsometricSprites.STONE_COLUMN_WOOD, orientation, x, y, 0);
		// Atlas-foot adjustment, in source pixels (independent of zoom).
		column.setRenderOffset(0, -13);
		// A support ends at the underside of its platform, regardless of height.
		// A receiver above its top must not receive a shadow from it.
		column.setShadowGeometry(ShadowGeometry.box(0.35, 0.35, supportHeight));
		scene.addRenderable(column);
	}

	/** Connects the four wall openings to their raised platforms. */
	private static void addStairs(ViewPanel scene) {
		int middle = GRID_SIZE / 2;
		int farEdge = GRID_SIZE;
		scene.addRenderable(new Entity(
				IsometricSprites.STAIRS, Orientation.WEST, -1, middle, 0));
		scene.addRenderable(new Entity(
				IsometricSprites.STAIRS, Orientation.EAST, farEdge, middle, 0));
		scene.addRenderable(new Entity(
				IsometricSprites.STAIRS, Orientation.SOUTH, middle, -1, 0));
		scene.addRenderable(new Entity(
				IsometricSprites.STAIRS, Orientation.NORTH, middle, farEdge, 0));
		// One tile farther north, a second staircase rises from z=PLATFORM_Z.
		scene.addRenderable(new Entity(
				IsometricSprites.STAIRS, Orientation.NORTH,
				middle, farEdge + 1, PLATFORM_Z));
	}

	/** Creates a freely positioned chair that can be moved every frame. */
	private static FreeEntity createChair() {
		return new FreeEntity(
				IsometricSprites.CHAIR, Orientation.WEST, 2.25, 2.40, 0, 1.75);
	}

	/** Adds four walls with open gates at the stair-facing exits. */
	private static void addWalls(ViewPanel scene) {
		for (int position = 1; position < GRID_SIZE - 1; position++) {
			Sprite wallOrGate = position == GRID_SIZE / 2
					? IsometricSprites.STONE_WALL_GATE : IsometricSprites.STONE_WALL;
			scene.addRenderable(new OccludingEntity(
					wallOrGate, Orientation.SOUTH, position, 0, 0, 1.3));
			scene.addRenderable(new OccludingEntity(
					wallOrGate, Orientation.WEST, 0, position, 0, 1.3));
			scene.addRenderable(new OccludingEntity(
					wallOrGate, Orientation.NORTH, position, GRID_SIZE - 1, 0, 1.3));
			scene.addRenderable(new OccludingEntity(
					wallOrGate, Orientation.EAST, GRID_SIZE - 1, position, 0, 1.3));
		}

		int last = GRID_SIZE - 1;
		scene.addRenderable(new OccludingEntity(
				IsometricSprites.STONE_WALL_CORNER, Orientation.SOUTH_WEST, 0, 0, 0));
		scene.addRenderable(new OccludingEntity(
				IsometricSprites.STONE_WALL_CORNER, Orientation.SOUTH_EAST, last, 0, 0));
		scene.addRenderable(new OccludingEntity(
				IsometricSprites.STONE_WALL_CORNER, Orientation.NORTH_WEST, 0, last, 0));
		scene.addRenderable(new OccludingEntity(
				IsometricSprites.STONE_WALL_CORNER, Orientation.NORTH_EAST, last, last, 0));
	}

	/**
	 * Updates movement at 60 FPS and sprite animation at 24 FPS.
	 */
	private static final class DemoClock {

		private static final long NANOS_PER_SECOND = 1_000_000_000L;
		private static final long ANIMATION_INTERVAL = NANOS_PER_SECOND / ANIMATION_FPS;

		private final ViewPanel scene;
		private final CrusaderDuoSequence demo;
		private final CameraController camera;
		private final FreeEntity chair;
		private double chairTimeSeconds;
		private double chairX = 2.25;
		private long previousTime = System.nanoTime();
		private long accumulatedAnimationNanos;
		private boolean paused;

		private DemoClock(ViewPanel scene, CrusaderDuoSequence demo,
				CameraController camera, FreeEntity chair) {
			this.scene = scene;
			this.demo = demo;
			this.camera = camera;
			this.chair = chair;
		}

		/** Toggles the simulation without stopping the Swing timer. */
		private void togglePause() {
			paused = !paused;
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
			// Keep the camera responsive while movement and animation are paused.
			camera.update(elapsedSeconds);
			if (!paused) {
				demo.update(elapsedSeconds);
				// Smooth back-and-forth motion, with a 4-second period.
				chairTimeSeconds += elapsedSeconds;
				double nextChairX = 2.25 + 0.75 * Math.sin(
						2.0 * Math.PI * chairTimeSeconds / 4.0);
				chair.moveBy(nextChairX - chairX, 0.0);
				chairX = nextChairX;
				accumulatedAnimationNanos += elapsedNanos;
				while (accumulatedAnimationNanos >= ANIMATION_INTERVAL) {
					scene.animationTick();
					accumulatedAnimationNanos -= ANIMATION_INTERVAL;
				}
			}

			scene.repaint();
		}
	}

	/** Moves the view with the arrow keys. Home restores its initial position. */
	private static final class CameraController {

		private final ViewPanel scene;
		private final IsometricRenderer renderer;
		private boolean leftPressed;
		private boolean rightPressed;
		private boolean upPressed;
		private boolean downPressed;

		// Keep fractional offsets so movement stays smooth at any frame rate.
		private double cameraX;
		private double cameraY;
		private int appliedX;
		private int appliedY;

		private CameraController(ViewPanel scene, IsometricRenderer renderer) {
			this.scene = scene;
			this.renderer = renderer;
			bindArrow("LEFT", () -> leftPressed = true, () -> leftPressed = false);
			bindArrow("RIGHT", () -> rightPressed = true, () -> rightPressed = false);
			bindArrow("UP", () -> upPressed = true, () -> upPressed = false);
			bindArrow("DOWN", () -> downPressed = true, () -> downPressed = false);

			scene.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke("HOME"), "camera-home");
			scene.getActionMap().put("camera-home", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent event) {
					cameraX = 0.0;
					cameraY = 0.0;
					applyOffset();
				}
			});
		}

		/** Records key presses and releases without relying on key repeat. */
		private void bindArrow(String key, Runnable press, Runnable release) {
			String name = "camera-" + key.toLowerCase();
			scene.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke("pressed " + key), name + "-pressed");
			scene.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke("released " + key), name + "-released");

			scene.getActionMap().put(name + "-pressed", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent event) {
					press.run();
				}
			});
			scene.getActionMap().put(name + "-released", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent event) {
					release.run();
				}
			});
		}

		/** Moves the view in screen coordinates at a constant speed. */
		private void update(double elapsedSeconds) {
			int dx = (rightPressed ? 1 : 0) - (leftPressed ? 1 : 0);
			int dy = (downPressed ? 1 : 0) - (upPressed ? 1 : 0);

			if (dx != 0 || dy != 0) {
				double distance = Math.hypot(dx, dy);
				cameraX += CAMERA_SPEED * elapsedSeconds * dx / distance;
				cameraY += CAMERA_SPEED * elapsedSeconds * dy / distance;
			}

			applyOffset();
		}

		/** Applies only the change in offset to the renderer's origin. */
		private void applyOffset() {
			int nextX = (int) Math.round(cameraX);
			int nextY = (int) Math.round(cameraY);
			renderer.pan(nextX - appliedX, nextY - appliedY);
			appliedX = nextX;
			appliedY = nextY;
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
	 * Runs both Crusaders on the same closed routes in opposite directions.
	 * Meetings interrupt their current movement for HIT, IDLE and a half-turn;
	 * the remaining demonstration animations are still played together.
	 */
	private static final class CrusaderDuoSequence {
		private static final double WALK_SPEED = 0.75; // Tiles per second.
		private static final double RUN_SPEED = 1.50;
		private static final double BEFORE_ACTION_IDLE_SECONDS = 0.6;
		private static final double LAST_FRAME_HOLD_SECONDS = 1.0;
		private static final double EPSILON = 1e-8;

		// Walk around the room and visit every platform before running.
		// Platform laps pass 0.20 tile inside the centers of fence tiles,
		// and 0.10 tile inside the stone-wall tiles on the eastern platform.
		// Stair ramps climb one full level within the staircase tile; entrances
		// and exits remain centered on their tiles.
		private static final Waypoint[] WALK_ROUTE = {
				new Waypoint(1, 1, 0),

				// South wall: climb, circle the platform, and return.
				new Waypoint(2, 1, 0),
				new Waypoint(2, 0, 0),
				new Waypoint(2, -HALF_STAIR_TILE, 0),
				new Waypoint(2, -2 + HALF_STAIR_TILE, PLATFORM_Z),
				new Waypoint(2, -2, PLATFORM_Z),
				new Waypoint(2, -2 - PALISADE_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(PALISADE_ROUTE_INSET, -2 - PALISADE_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(PALISADE_ROUTE_INSET, -6 + PALISADE_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(4 - PALISADE_ROUTE_INSET, -6 + PALISADE_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(4 - PALISADE_ROUTE_INSET, -2 - PALISADE_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(2, -2 - PALISADE_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(2, -2, PLATFORM_Z),
				new Waypoint(2, -2 + HALF_STAIR_TILE, PLATFORM_Z),
				new Waypoint(2, -HALF_STAIR_TILE, 0),
				new Waypoint(2, 0, 0),
				new Waypoint(2, 1, 0),

				// East wall.
				new Waypoint(2.5, 1, 0),
				new Waypoint(2.5, 2, 0),
				new Waypoint(3, 2, 0),
				new Waypoint(4, 2, 0),
				new Waypoint(4 + HALF_STAIR_TILE, 2, 0),
				new Waypoint(6 - HALF_STAIR_TILE, 2, PLATFORM_Z),
				new Waypoint(6, 2, PLATFORM_Z),
				new Waypoint(6 + PLATFORM_ROUTE_INSET, 2, PLATFORM_Z),
				new Waypoint(6 + PLATFORM_ROUTE_INSET, PLATFORM_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(10 - PLATFORM_ROUTE_INSET, PLATFORM_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(10 - PLATFORM_ROUTE_INSET, 4 - PLATFORM_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(6 + PLATFORM_ROUTE_INSET, 4 - PLATFORM_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(6 + PLATFORM_ROUTE_INSET, 2, PLATFORM_Z),
				new Waypoint(6, 2, PLATFORM_Z),
				new Waypoint(6 - HALF_STAIR_TILE, 2, PLATFORM_Z),
				new Waypoint(4 + HALF_STAIR_TILE, 2, 0),
				new Waypoint(4, 2, 0),
				new Waypoint(3, 2, 0),

				// North wall: two consecutive stairs, then a higher 5x4 platform.
				new Waypoint(2.5, 2, 0),
				new Waypoint(2.5, 3, 0),
				new Waypoint(2, 3, 0),
				new Waypoint(2, 4, 0),
				new Waypoint(2, 4 + HALF_STAIR_TILE, 0),
				new Waypoint(2, 6 - HALF_STAIR_TILE, PLATFORM_Z),
				new Waypoint(2, NORTH_PLATFORM_START_Y - HALF_STAIR_TILE, NORTH_PLATFORM_Z),
				new Waypoint(2, NORTH_PLATFORM_START_Y, NORTH_PLATFORM_Z),
				new Waypoint(2, NORTH_PLATFORM_START_Y + PALISADE_ROUTE_INSET, NORTH_PLATFORM_Z),
				new Waypoint(PALISADE_ROUTE_INSET, NORTH_PLATFORM_START_Y + PALISADE_ROUTE_INSET, NORTH_PLATFORM_Z),
				new Waypoint(PALISADE_ROUTE_INSET, 10 - PALISADE_ROUTE_INSET, NORTH_PLATFORM_Z),
				new Waypoint(4 - PALISADE_ROUTE_INSET, 10 - PALISADE_ROUTE_INSET, NORTH_PLATFORM_Z),
				new Waypoint(4 - PALISADE_ROUTE_INSET, NORTH_PLATFORM_START_Y + PALISADE_ROUTE_INSET, NORTH_PLATFORM_Z),
				new Waypoint(2, NORTH_PLATFORM_START_Y + PALISADE_ROUTE_INSET, NORTH_PLATFORM_Z),
				new Waypoint(2, NORTH_PLATFORM_START_Y, NORTH_PLATFORM_Z),
				new Waypoint(2, NORTH_PLATFORM_START_Y - HALF_STAIR_TILE, NORTH_PLATFORM_Z),
				new Waypoint(2, 6 - HALF_STAIR_TILE, PLATFORM_Z),
				new Waypoint(2, 4 + HALF_STAIR_TILE, 0),
				new Waypoint(2, 4, 0),
				new Waypoint(2, 3, 0),

				// West wall.
				new Waypoint(1.5, 3, 0),
				new Waypoint(1.5, 2, 0),
				new Waypoint(1, 2, 0),
				new Waypoint(0, 2, 0),
				new Waypoint(-HALF_STAIR_TILE, 2, 0),
				new Waypoint(-2 + HALF_STAIR_TILE, 2, PLATFORM_Z),
				new Waypoint(-2, 2, PLATFORM_Z),
				new Waypoint(-2 - PALISADE_ROUTE_INSET, 2, PLATFORM_Z),
				new Waypoint(-2 - PALISADE_ROUTE_INSET, PALISADE_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(-6 + PALISADE_ROUTE_INSET, PALISADE_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(-6 + PALISADE_ROUTE_INSET, 4 - PALISADE_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(-2 - PALISADE_ROUTE_INSET, 4 - PALISADE_ROUTE_INSET, PLATFORM_Z),
				new Waypoint(-2 - PALISADE_ROUTE_INSET, 2, PLATFORM_Z),
				new Waypoint(-2, 2, PLATFORM_Z),
				new Waypoint(-2 + HALF_STAIR_TILE, 2, PLATFORM_Z),
				new Waypoint(-HALF_STAIR_TILE, 2, 0),
				new Waypoint(0, 2, 0),
				new Waypoint(1, 2, 0),
				new Waypoint(1.5, 2, 0),
				new Waypoint(1.5, 1, 0),
				new Waypoint(1, 1, 0)
		};

		// Run closer to the four central walls while retaining a half-tile
		// clearance from their anchor lines. Each leg stays axis-aligned.
		private static final Waypoint[] RUN_ROUTE = {
				new Waypoint(1, 1, 0),
				new Waypoint(1, 0.5, 0),
				new Waypoint(0.5, 0.5, 0),
				new Waypoint(3.5, 0.5, 0),
				new Waypoint(3.5, 3.5, 0),
				new Waypoint(0.5, 3.5, 0),
				new Waypoint(0.5, 1, 0),
				new Waypoint(1, 1, 0)
		};

		private record Waypoint(double x, double y, double z) {
		}

		// A meeting is detected in world coordinates, including altitude.
		private static final double ENCOUNTER_DISTANCE = 0.34;
		private static final double ENCOUNTER_HEIGHT_TOLERANCE = 0.20;
		private static final double REARM_DISTANCE = 0.80;
		private static final double HIT_DURATION_SECONDS =
				AnimatedSprites.CRUSADER_HIT.animationSize() / (double) ANIMATION_FPS + 0.12;
		private static final double ENCOUNTER_IDLE_SECONDS = 0.90;
		private static final double ABOUT_FACE_SECONDS = 0.20;

		private enum EncounterPhase { NONE, HIT, IDLE, ABOUT_FACE }

		/** A closed polyline, sampled by horizontal distance (Z is interpolated). */
		private static final class Path {
			private final Waypoint[] waypoints;
			private final double[] distances;
			private final double length;

			private Path(Waypoint[] waypoints) {
				this.waypoints = waypoints;
				distances = new double[waypoints.length];
				for (int i = 1; i < waypoints.length; i++) {
					Waypoint a = waypoints[i - 1];
					Waypoint b = waypoints[i];
					double segmentLength = Math.hypot(b.x() - a.x(), b.y() - a.y());
					if (segmentLength <= EPSILON) {
						throw new IllegalArgumentException("Zero-length path segment at " + i);
					}
					distances[i] = distances[i - 1] + segmentLength;
				}
				length = distances[distances.length - 1];
				if (!samePosition(waypoints[0], waypoints[waypoints.length - 1])) {
					throw new IllegalArgumentException("Crusader route must be closed");
				}
			}

			private static boolean samePosition(Waypoint a, Waypoint b) {
				return Math.abs(a.x() - b.x()) < EPSILON
						&& Math.abs(a.y() - b.y()) < EPSILON
						&& Math.abs(a.z() - b.z()) < EPSILON;
			}

			/** Wraps around the closed route, including when travelling backwards. */
			private Waypoint at(double distance) {
				double position = ((distance % length) + length) % length;
				for (int i = 1; i < distances.length; i++) {
					if (position < distances[i]) {
						double t = (position - distances[i - 1]) /
								(distances[i] - distances[i - 1]);
						Waypoint a = waypoints[i - 1];
						Waypoint b = waypoints[i];
						return new Waypoint(
								a.x() + (b.x() - a.x()) * t,
								a.y() + (b.y() - a.y()) * t,
								a.z() + (b.z() - a.z()) * t);
					}
				}
				return waypoints[0];
			}
		}

		/** Position and direction of one Crusader on the same cyclic path. */
		private static final class Walker {
			private final AnimatedEntity entity;
			private Path path;
			private double pathDistance;
			private double distanceWalked;
			private int travelDirection;

			private Walker(AnimatedEntity entity, int travelDirection) {
				this.entity = entity;
				this.travelDirection = travelDirection;
			}

			private void start(Path nextPath, double fraction) {
				path = nextPath;
				pathDistance = fraction * path.length;
				distanceWalked = 0.0;
				place();
			}

			private void walk(double distance) {
				double step = Math.min(distance, path.length - distanceWalked);
				pathDistance += travelDirection * step;
				distanceWalked += step;
				place();
			}

			private void reverse() {
				travelDirection = -travelDirection;
				faceDirectionOfTravel();
			}

			private void place() {
				Waypoint point = path.at(pathDistance);
				entity.moveBy(point.x() - entity.getPreciseX(),
						point.y() - entity.getPreciseY());
				entity.setPreciseZ(point.z());
				faceDirectionOfTravel();
			}

			private void faceDirectionOfTravel() {
				Waypoint here = path.at(pathDistance);
				Waypoint ahead = path.at(pathDistance + travelDirection * 0.001);
				entity.setOrientation(directionFor(
						ahead.x() - here.x(), ahead.y() - here.y()));
			}
		}

		private static final Path WALK_PATH = new Path(WALK_ROUTE);
		private static final Path RUN_PATH = new Path(RUN_ROUTE);

		private final Walker first;
		private final Walker second;
		private CrusaderDemoPhase phase = CrusaderDemoPhase.WALK;
		private EncounterPhase encounterPhase = EncounterPhase.NONE;
		private double encounterSeconds;
		private boolean encounterArmed = true;
		private double elapsedPhaseSeconds;
		private double elapsedLastFrameSeconds;
		private double phaseDurationSeconds;

		private CrusaderDuoSequence(AnimatedEntity firstCrusader,
				AnimatedEntity secondCrusader) {
			first = new Walker(firstCrusader, +1);
			second = new Walker(secondCrusader, -1);
			configurePhase();
		}

		/** Advances both actors together, then handles a possible encounter. */
		private void update(double elapsedSeconds) {
			if (elapsedSeconds <= 0.0) {
				return;
			}
			if (encounterPhase != EncounterPhase.NONE) {
				updateEncounter(elapsedSeconds);
			} else if (phase == CrusaderDemoPhase.WALK || phase == CrusaderDemoPhase.RUN) {
				updateMovement(elapsedSeconds);
			} else {
				updateStationaryAnimation(elapsedSeconds);
			}
		}

		private void updateMovement(double elapsedSeconds) {
			double before = separation();
			double speed = phase == CrusaderDemoPhase.RUN ? RUN_SPEED : WALK_SPEED;
			first.walk(speed * elapsedSeconds);
			second.walk(speed * elapsedSeconds);
			double after = separation();

			// Require an approach at the same elevation. Rearm only after the
			// actors have moved apart, so their 180-degree turn cannot retrigger.
			if (!encounterArmed && after > REARM_DISTANCE) {
				encounterArmed = true;
			}
			if (encounterArmed && after <= ENCOUNTER_DISTANCE
					&& after < before - EPSILON
					&& Math.abs(first.entity.getPreciseZ()
							- second.entity.getPreciseZ()) < ENCOUNTER_HEIGHT_TOLERANCE) {
				beginEncounter();
				return;
			}

			if (first.distanceWalked >= first.path.length - EPSILON
					&& second.distanceWalked >= second.path.length - EPSILON) {
				advancePhase();
			}
		}

		private double separation() {
			double dx = first.entity.getPreciseX() - second.entity.getPreciseX();
			double dy = first.entity.getPreciseY() - second.entity.getPreciseY();
			return Math.hypot(dx, dy);
		}

		private void beginEncounter() {
			encounterArmed = false;
			encounterPhase = EncounterPhase.HIT;
			encounterSeconds = 0.0;
			setBothAnimations(AnimatedSprites.CRUSADER_HIT, 1.0);
		}

		/** HIT, then IDLE, then a visible half-turn before resuming travel. */
		private void updateEncounter(double elapsedSeconds) {
			encounterSeconds += elapsedSeconds;
			switch (encounterPhase) {
				case HIT -> {
					if (encounterSeconds >= HIT_DURATION_SECONDS) {
						encounterPhase = EncounterPhase.IDLE;
						encounterSeconds = 0.0;
						setBothAnimations(AnimatedSprites.CRUSADER_IDLE, 1.0);
					}
				}
				case IDLE -> {
					if (encounterSeconds >= ENCOUNTER_IDLE_SECONDS) {
						encounterPhase = EncounterPhase.ABOUT_FACE;
						encounterSeconds = 0.0;
						first.reverse();
						second.reverse();
					}
				}
				case ABOUT_FACE -> {
					if (encounterSeconds >= ABOUT_FACE_SECONDS) {
						encounterPhase = EncounterPhase.NONE;
						setBothAnimations(phase == CrusaderDemoPhase.RUN
								? AnimatedSprites.CRUSADER_RUN : AnimatedSprites.CRUSADER_WALK,
								phase == CrusaderDemoPhase.RUN ? 1.5 : 1.0);
					}
				}
				case NONE -> { /* Already moving. */ }
			}
		}

		/** Plays the other animation demonstrations in parallel, as before. */
		private void updateStationaryAnimation(double elapsedSeconds) {
			elapsedPhaseSeconds += elapsedSeconds;
			if (first.entity.isAnimationPaused() && second.entity.isAnimationPaused()) {
				elapsedLastFrameSeconds += elapsedSeconds;
			}
			if (elapsedLastFrameSeconds >= LAST_FRAME_HOLD_SECONDS
					&& elapsedPhaseSeconds >= phaseDurationSeconds) {
				advancePhase();
			}
		}

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
			if (phase == CrusaderDemoPhase.WALK || phase == CrusaderDemoPhase.RUN) {
				Path path = phase == CrusaderDemoPhase.WALK ? WALK_PATH : RUN_PATH;
				first.start(path, 0.0);
				second.start(path, 0.5);
				encounterArmed = true;
			}
			setBothAnimations(animation, phase == CrusaderDemoPhase.RUN ? 1.5 : 1.0);
			elapsedPhaseSeconds = 0.0;
			elapsedLastFrameSeconds = 0.0;
			phaseDurationSeconds = Math.max(
					phase == CrusaderDemoPhase.IDLE ? 1.5 :
							(phase == CrusaderDemoPhase.IDLE_BEFORE_ATTACK
							 || phase == CrusaderDemoPhase.IDLE_BEFORE_DEATH
							 || phase == CrusaderDemoPhase.IDLE_BEFORE_HIT)
								? BEFORE_ACTION_IDLE_SECONDS : 0.0,
					animation.animationSize() /
							(ANIMATION_FPS * (phase == CrusaderDemoPhase.RUN ? 1.5 : 1.0)));
		}

		private void setBothAnimations(AnimatedOrientedSprite sprite, double speed) {
			setAnimation(first.entity, sprite, speed);
			setAnimation(second.entity, sprite, speed);
		}

		private static void setAnimation(
				AnimatedEntity entity, AnimatedOrientedSprite sprite, double speed) {
			entity.setSprite(sprite);
			entity.setAnimationPaused(false);
			entity.setAnimationSpeed(speed);
		}

		private static Orientation directionFor(double dx, double dy) {
			if (dx > EPSILON) return Orientation.EAST;
			if (dx < -EPSILON) return Orientation.WEST;
			if (dy > EPSILON) return Orientation.NORTH;
			if (dy < -EPSILON) return Orientation.SOUTH;
			throw new IllegalArgumentException("Path direction is undefined");
		}
	}
}
