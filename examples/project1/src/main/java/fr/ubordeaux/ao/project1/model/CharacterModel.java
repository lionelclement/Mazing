package fr.ubordeaux.ao.project1.model;

import java.util.Map;
import java.util.UUID;

import javax.swing.Timer;

import fr.ubordeaux.ao.mazing.api.CharacterMode;
import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.Spider;
import fr.ubordeaux.ao.project1.view.MazingView;

public class CharacterModel {

	private static final float WALK_SPEED = 0.03f;
	private static final float RUN_SPEED = 0.08f;
	private static final float TURN_LEFT_SPEED = 0.01f;

	public static enum Type {
		CRUSADER,
		SPIDER
	};

	private ModelListener listener;

	private float x, y, z;
	public float speedX;
	public float speedY;
	private float speedZ;
	private CharacterMode mode;
	private Direction direction;
	private boolean freeze;
	private UUID id;
	private final Maze maze;
	private float oldx;
	private float oldy;
	private float oldz;
	private Type type;

	public CharacterModel(Type type, float x, float y, float z, Direction startDir, Maze maze) {
		this.id = UUID.randomUUID();
		this.type = type;
		this.x = x;
		this.y = y;
		this.z = z;
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

	public void checkCollision() {
		float cellX = x / MazingView.CELL_WIDTH;
		float cellY = y / MazingView.CELL_HEIGHT;
		float cellZ = z / MazingView.CELL_DEPTH;
		if (!maze.canMove(cellX, cellY, cellZ, direction.name())) {
			x = oldx;
			y = oldy;
			z = oldz;
			switch (type) {
				case CRUSADER:
					this.setMode(Crusader.Mode.GOTHIT);
					break;
				case SPIDER:
					this.setMode(Spider.Mode.IDLE);
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
        Direction.EAST,      new Point3D( 1,  0,  0),
        Direction.WEST,      new Point3D(-1,  0,  0),
        Direction.NORTH,     new Point3D( 0, -1,  0),
        Direction.SOUTH,     new Point3D( 0,  1,  0),
        Direction.NORTHEAST, new Point3D( 1, -1,  0),
        Direction.NORTHWEST, new Point3D(-1, -1,  0),
        Direction.SOUTHEAST, new Point3D( 1,  1,  0),
        Direction.SOUTHWEST, new Point3D(-1,  1,  0)
    );

    Point3D vector = dirVectors.get(direction);
    if (vector == null) {
        this.speedX = 0;
        this.speedY = 0;
        this.speedZ = 0;
        return;
    }

    double speed = switch (mode.name()) {
        case "WALK" -> WALK_SPEED;
        case "TURN_LEFT" -> TURN_LEFT_SPEED;
        case "RUN" -> RUN_SPEED;
        case "IDLE", "GOTHIT" -> 0;
        default -> Math.hypot(speedX, speedY);
    };

    // Normaliser les diagonales (XY ou XYZ)
    double norm = Math.sqrt(vector.x * vector.x + vector.y * vector.y + vector.z * vector.z);
    if (norm == 0) norm = 1;

    this.speedX = (float) (vector.x / norm * speed);
    this.speedY = (float) (vector.y / norm * speed);
    this.speedZ = (float) (vector.z / norm * speed);
}
	public void setMode(CharacterMode mode) {
		// System.out.println("CharacterModel@setMode");
		this.mode = mode;
		updateVelocity();
		if (listener != null) {
			listener.onModeChanged(id, mode);
		}
	}

	public void setDirection(Direction direction) {
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
		if (mode == Crusader.Mode.ATTACK)
			setMode(Crusader.Mode.IDLE);
		if (mode == Crusader.Mode.DEATH)
			speedX = speedY = 0;
	}

	public CharacterMode getMode() {
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

	public void setSpeed(int x, int y, float z) {
		speedX = x;
		speedY = y;
		speedZ = z;
	}

}
