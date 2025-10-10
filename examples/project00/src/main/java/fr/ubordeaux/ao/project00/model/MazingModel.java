package fr.ubordeaux.ao.project00.model;

import fr.ubordeaux.ao.mazing.api.Crusader.Mode;

public class MazingModel {

	private float x;
	private float y;
	private float z;
	private float vx;
	private float vy;
	private float vz;
	private ModelListener modelListener;

	public void updateCrusaderPosition() {
		x += vx;
		y += vy;
		z += vz;
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

	public void setListener(ModelListener modelListener) {
		this.modelListener = modelListener;
	}

	public void setMode(Mode mode) {
		if (mode == Mode.WALK)
			vx = 0.01f;
		else if (mode == Mode.RUN)
			vx = 0.04f;
		else if (mode == Mode.IDLE)
			vx = 0;
        if (modelListener != null) modelListener.onModeChanged(mode);

	}

}
