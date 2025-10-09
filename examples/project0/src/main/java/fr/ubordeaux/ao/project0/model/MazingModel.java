package fr.ubordeaux.ao.project0.model;

import java.awt.geom.Point2D;
import java.util.Map;

import javax.swing.Timer;

import fr.ubordeaux.ao.mazing.api.Crusader.Mode;
import fr.ubordeaux.ao.mazing.api.Direction;

public class MazingModel {
	
	// Crusader
    private static final float WALK_SPEED = 0.03f;
    private static final float RUN_SPEED = 0.08f;
	

	private float x;
	private float y;
	private float z;
	private float speedX;
	private float speedY;
	private float speedZ;
	private Mode mode;
	private Direction direction;
    private ModelListener listener;
	private boolean freeze;
	private Mode savedMode;

	
	public MazingModel() {
		mode = Mode.IDLE;
		setDirection(Direction.SOUTH);
	}

    public void setListener(ModelListener listener) {
        this.listener = listener;
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

	public void updateCrusaderPosition() {
		if (!freeze) {
		x += speedX;
		y += speedY;
		z += speedZ;
		}
	}

	private void updateVelocity() {
	    // Mapping direction → vecteur
	    Map<Direction, Point2D.Double> dirVectors = Map.of(
	        Direction.EAST,      new Point2D.Double(1, 0),
	        Direction.NORTH,     new Point2D.Double(0, -1),
	        Direction.NORTHEAST, new Point2D.Double(1, -1),
	        Direction.NORTHWEST, new Point2D.Double(-1, -1),
	        Direction.SOUTH,     new Point2D.Double(0, 1),
	        Direction.SOUTHEAST, new Point2D.Double(1, 1),
	        Direction.SOUTHWEST, new Point2D.Double(-1, 1),
	        Direction.WEST,      new Point2D.Double(-1, 0)
	    );

	    // Mapping mode → vitesse
	    double speed = switch (mode) {
	        case WALK -> WALK_SPEED;
	        case RUN  -> RUN_SPEED;
	        case IDLE -> 0;
	        default   -> Math.hypot(speedX, speedY); // conserve la vitesse précédente
	    };

	    // Récupère le vecteur pour la direction ou garde le vecteur précédent si direction inconnue
	    Point2D.Double vector = dirVectors.getOrDefault(direction, new Point2D.Double(speedX, speedY));

	    // Normalisation pour diagonales
	    double length = Math.hypot(vector.x, vector.y);
	    if (length != 0) {
	        vector.x /= length;
	        vector.y /= length;
	    }

	    // Applique la vitesse
	    this.speedX = (float) (vector.x * speed);
	    this.speedY = (float) (vector.y * speed);
	}

	public void setMode(Mode mode) {
		savedMode = this.mode;
		this.mode = mode;
		updateVelocity();
	    if (listener != null) 
	    	listener.onModeChanged(mode);
	}
	    

	public void setDirection(Direction direction) {
		freeze = true;
		this.direction = direction;	
		updateVelocity();
		if (listener != null) listener.onDirectionChanged(direction);
		Timer t = new Timer(350, e -> {
			freeze = false;
		});
		t.setRepeats(false);
		t.start();
		
	}

	public void updateCrusaderBegin() {
	}

	public void updateCrusaderEnd() {
	}

	public void updateCrusaderMid() {
	}

}
