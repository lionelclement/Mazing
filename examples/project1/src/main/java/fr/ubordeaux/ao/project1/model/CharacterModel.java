package fr.ubordeaux.ao.project1.model;

import java.awt.geom.Point2D;
import java.util.Map;
import java.util.UUID;

import javax.swing.Timer;

import fr.ubordeaux.ao.mazing.api.CharacterMode;
import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.project1.view.MazingView;

public class CharacterModel {

	private static final float WALK_SPEED = 0.03f;
	private static final float RUN_SPEED = 0.08f;

	private ModelListener listener;

	private float x, y, z;
	public float speedX;
	public float speedY;
	private float speedZ;
	private CharacterMode mode;
	private CharacterMode savedMode;
	private Direction direction;
	private boolean freeze;
	private UUID id;
	private final Maze maze;
	private float oldx;
	private float oldy;
	private float oldz;
	private float offsetX;
	private float offsetY;

	public CharacterModel(float x, float y, float z, float offsetX, float offsetY, Direction startDir, Maze maze) {
		this.id = UUID.randomUUID();
		this.x = x;
		this.y = y;
		this.z = z;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.maze = maze;
		this.mode = Crusader.Mode.IDLE;
		this.direction = startDir;
	}

	public UUID getId() {
		return id;
	}

	public void attachModelListener(ModelListener modelListener) {
		this.listener = modelListener;
	}

	public void init() {
		this.mode = Crusader.Mode.IDLE;
		this.direction = Direction.SOUTH;
		updateVelocity();
		if (listener != null)
			listener.onModeChanged(id, mode);
		if (listener != null)
			listener.onDirectionChanged(id, direction);
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public void checkCollision(UUID id2) {
		float cellX = x / MazingView.CELL_WIDTH;
		float cellY = y / MazingView.CELL_HEIGHT;
		float cellZ = z / MazingView.CELL_DEPTH;
		if (!maze.canMove(cellX, cellY, cellZ, direction)) {
			x = oldx;
			y = oldy;
			z = oldz;
			this.setMode(Crusader.Mode.GOTHIT);
			speedX = speedY = speedZ = 0;
		}
	}

	public void updatePosition() {
		if (!freeze) {
			oldx = x;
			oldy = y;
			oldz = z;
			x += speedX;
			y += speedY;
			z += speedZ;
		}
		if (listener != null) {
			listener.updatePosition(id, offsetX, offsetY, x, y, z);
		}
	}

	private void updateVelocity() {
		System.err.println("updateVelocity");
		Map<Direction, Point2D.Double> dirVectors = Map.of(
				Direction.EAST, new Point2D.Double(1, 0),
				Direction.NORTH, new Point2D.Double(0, -1),
				Direction.NORTHEAST, new Point2D.Double(1, -1),
				Direction.NORTHWEST, new Point2D.Double(-1, -1),
				Direction.SOUTH, new Point2D.Double(0, 1),
				Direction.SOUTHEAST, new Point2D.Double(1, 1),
				Direction.SOUTHWEST, new Point2D.Double(-1, 1),
				Direction.WEST, new Point2D.Double(-1, 0));

		Point2D.Double vector = dirVectors.get(direction);
		double speed = switch (mode.name()) {
			case "WALK" -> WALK_SPEED;
			case "RUN" -> RUN_SPEED;
			case "IDLE", "GOTHIT" -> 0;
			default -> Math.hypot(speedX, speedY);
		};

		this.speedX = (float) (vector.x * speed);
		this.speedY = (float) (vector.y * speed);
	}

	public void setMode(CharacterMode mode) {
		System.out.println("CharacterModel@setMode");
		savedMode = this.mode;
		this.mode = mode;
		updateVelocity();
		if (listener != null) {
			listener.onModeChanged(id, mode);
		}
	}

	public void setDirection(Direction direction) {
		System.out.println("CharacterModel@setDirection");
		freeze = true;
		this.direction = direction;
		updateVelocity();
		if (listener != null)
			listener.onDirectionChanged(id, direction);

		Timer t = new Timer(350, _ -> freeze = false);
		t.setRepeats(false);
		t.start();
	}

	public void updateCrusaderEnd() {
		if (mode == Crusader.Mode.ATTACK)
			setMode(Crusader.Mode.IDLE);
		if (mode == Crusader.Mode.DEATH)
			speedX = speedY = 0;
	}

	public CharacterMode getMode() {
		return mode;
	}

}
