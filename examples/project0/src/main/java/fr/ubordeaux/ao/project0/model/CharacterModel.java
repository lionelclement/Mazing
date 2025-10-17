package fr.ubordeaux.ao.project0.model;

import java.awt.geom.Point2D;
import java.util.Map;
import javax.swing.Timer;
import fr.ubordeaux.ao.mazing.api.Crusader.Mode;
import fr.ubordeaux.ao.mazing.api.Direction;

public class CharacterModel {

	private static final float WALK_SPEED = 0.03f;
	private static final float RUN_SPEED = 0.08f;

	private float x, y, z;
	private float speedX, speedY, speedZ;
	private Mode mode;
	private Direction direction;
	private boolean freeze;
	private ModelListener listener;

	public CharacterModel(float startX, float startY, Direction startDir) {
		this.x = startX;
		this.y = startY;
		this.z = 0;
		this.mode = Mode.IDLE;
		this.direction = startDir;
	}

	public void init() {
		this.mode = Mode.IDLE;
		this.direction = Direction.SOUTH;
		updateVelocity();
		if (listener != null)
			listener.onModeChanged(mode);
		if (listener != null)
			listener.onDirectionChanged(direction);
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

	public void updatePosition() {
		if (!freeze) {
			x += speedX;
			y += speedY;
			z += speedZ;
		}
	}

	private void updateVelocity() {
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
		double speed = switch (mode) {
		case WALK -> WALK_SPEED;
		case RUN -> RUN_SPEED;
		case IDLE, GOTHIT -> 0;
		default -> Math.hypot(speedX, speedY);
		};

		this.speedX = (float) (vector.x * speed);
		this.speedY = (float) (vector.y * speed);
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		updateVelocity();
		if (listener != null) {
			listener.onModeChanged(mode);
		}
	}

	public void setDirection(Direction direction) {
		freeze = true;
		this.direction = direction;
		updateVelocity();
		if (listener != null)
			listener.onDirectionChanged(direction);

		Timer t = new Timer(350, _ -> freeze = false);
		t.setRepeats(false);
		t.start();
	}

	public void updateEnd() {
		if (mode == Mode.GOTHIT)
			setMode(Mode.IDLE);
		if (mode == Mode.DEATH)
			speedX = speedY = 0;
	}

	public void attachModelListener(ModelListener modelListener) {
		this.listener = modelListener;

	}

}
