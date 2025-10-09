package fr.ubordeaux.ao.project0.model;

import fr.ubordeaux.ao.project0.view.MazingView;

public class MazingModel {
	
	// Crusader
    private static final float WALK_SPEED = 0.03f;
    //private static final float RUN_SPEED = 0.08f;
    //private static final float JUMP_SPEED = 1.5f;
	
    // Spider
    //private static final float WALK_SPEED = 0.03f;


	private float x;
	private float y;
	private float z;
	private float speedX;
	private float speedY;
	private float speedZ;
	
	public MazingModel() {
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
		x += speedX;
		y += speedY;
		z += speedZ;
	}

    public void updateSpiderPosition() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	
    public void updateSpiderDirection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	
	
	

}
