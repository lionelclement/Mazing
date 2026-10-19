package fr.ubordeaux.ao.mazing.view.entity;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.Renderable;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

import java.awt.*;

/// An entity in the 3D Isometric grid
public class Entity implements Renderable {

	private Sprite sprite;
	private Orientation orientation;
	private int x;
	private int y;
	private int z;

	public Entity(Sprite sprite, Orientation orientation, int x, int y, int z) {
		this.sprite = sprite;
		this.orientation = orientation;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Entity(Sprite sprite) {
		this(sprite, Orientation.NORTH, 0, 0, 0);
	}

	public Entity(Sprite sprite, int x, int y, int z) {
		this(sprite, Orientation.NORTH, x, y, z);
	}

	public Entity(Sprite sprite, Orientation orientation) {
		this(sprite, orientation, 0, 0, 0);
	}


	@Override
	public void renderInScreen(Graphics2D g, int x, int y, int animationTick, int zoom) {
		// g.drawRect(x, y, 20, 20);
		this.sprite.render(g, x, y, orientation, animationTick, zoom);
		// g.draw(new Ellipse2D.Double(x-2, y-2, 4, 4));
	}

	public Sprite getSprite() {
		return sprite;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public void moveX(int x) {
		this.x += x;
	}

	public void moveY(int y) {
		this.y += y;
	}

	public void moveZ(int z) {
		this.z += z;
	}

	public void move(int x, int y) {
		this.x += x;
		this.y += y;
	}

	public void move(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}

	@Override
	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}
}
