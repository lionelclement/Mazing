package fr.ubordeaux.ao.project1.model;

import java.util.Map;
import java.util.UUID;

import javax.swing.Timer;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.Spider;
import fr.ubordeaux.ao.project1.view.MazingView;

public class CharacterModel {

	private static final float WALK_SPEED = 0.03f;
	private static final float RUN_SPEED = 0.08f;
	private static final float STAIR_CLIMB_SPEED = 0.15f;

	public static enum Type {
		CRUSADER,
		SPIDER
	};

	private ModelListener listener;

	private float x, y, z;
	private float offsetX, offsetY;
	public float speedX;
	public float speedY;
	private float speedZ;
	private String mode;
	private String direction;
	private boolean freeze;
	private UUID id;
	private final Maze maze;
	private float oldx;
	private float oldy;
	private float oldz;
	private Type type;
	private int currentFloor;

	public CharacterModel(Type type, float x, float y, float z, float offsetX, float offsetY, String startDir, Maze maze) {
		this.id = UUID.randomUUID();
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		currentFloor = (int) z;
		this.maze = maze;
		this.mode = Crusader.Mode.IDLE.name();
		this.direction = startDir;
	}

	public UUID getId() {
		return id;
	}

	public void attachModelListener(ModelListener modelListener) {
		this.listener = modelListener;
	}

	public void init() {
		this.mode = Crusader.Mode.IDLE.name();
		this.direction = Direction.SOUTH.name();
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

	public void checkCollision() {
		float cellX = x / MazingView.CELL_WIDTH;
		float cellY = y / MazingView.CELL_HEIGHT;
		float cellZ = z / MazingView.CELL_DEPTH;

		boolean changedFloor = false;
		if (speedZ != 0) {
			if (z > currentFloor + 1) {
				z = ++currentFloor;
				changedFloor = true;
			} else if (z < currentFloor - 1) {
				z = --currentFloor;
				changedFloor = true;
			}
			if (changedFloor){
			speedZ = 0;
			setMode("IDLE");
			if (listener != null) {
					listener.onMazeChanged(maze, z);
				}
			}
		}

		if (!maze.canMove(cellX, cellY, cellZ, direction, offsetX, offsetY)) {
			x = oldx;
			y = oldy;
			z = oldz;
			switch (type) {
				case CRUSADER:
					this.setMode(Crusader.Mode.GOTHIT.name());
					break;
				case SPIDER:
					this.setMode(Spider.Mode.IDLE.name());
					break;
				default:
					break;

			}
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
			listener.updatePosition(id, x, y, z);
		}
	}

	private void updateVelocity() {
		Map<Direction, Point3D> dirVectors = Map.of(
				Direction.EAST, new Point3D(1, 0, 0),
				Direction.WEST, new Point3D(-1, 0, 0),
				Direction.NORTH, new Point3D(0, -1, 0),
				Direction.SOUTH, new Point3D(0, 1, 0),
				Direction.NORTHEAST, new Point3D(1, -1, 0),
				Direction.NORTHWEST, new Point3D(-1, -1, 0),
				Direction.SOUTHEAST, new Point3D(1, 1, 0),
				Direction.SOUTHWEST, new Point3D(-1, 1, 0));

		Point3D vector = dirVectors.get(Direction.valueOf(direction));

		double speed = switch (mode) {
			case "WALK" -> WALK_SPEED;
			case "RUN" -> RUN_SPEED;
			case "IDLE", "GOTHIT" -> 0;
			default -> Math.hypot(speedX, speedY);
		};

		// Normaliser les diagonales (XY ou XYZ)
		double norm = Math.sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z);
		if (norm == 0)
			norm = 1;

		this.speedX = (float) (vector.x / norm * speed);
		this.speedY = (float) (vector.y / norm * speed);
	}

	public void setMode(String mode) {
		// System.out.println("CharacterModel@setMode");
		this.mode = mode;
		updateVelocity();
		if (listener != null) {
			listener.onModeChanged(id, mode);
		}
	}

	public void setDirection(String direction) {
		// System.out.println("CharacterModel@setDirection");
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
		if (mode.equals(Crusader.Mode.ATTACK.name()))
			setMode(Crusader.Mode.IDLE.name());
		if (mode.equals(Crusader.Mode.DEATH.name()))
			speedX = speedY = 0;
	}

	public String getMode() {
		return mode;
	}

	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		if (listener != null) {
			listener.updatePosition(id, x, y, z);
		}
	}

	public void setSpeed(float x, float y, float z) {
		speedX = x;
		speedY = y;
		speedZ = z;
	}

	public void climbsTheStairs() {
		this.setMode(Crusader.Mode.WALK_LEFT.name());
		this.setSpeed(0, 0, STAIR_CLIMB_SPEED);
	}

	public void crusaderDescendsTheStairs() {
		this.setMode(Crusader.Mode.WALK_RIGHT.name());
		this.setSpeed(0, 0, -STAIR_CLIMB_SPEED);
	}

}
