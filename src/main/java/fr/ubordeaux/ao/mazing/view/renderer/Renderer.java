package fr.ubordeaux.ao.mazing.view.renderer;

import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.Renderable;

import java.awt.Graphics2D;

public interface Renderer {
	Point renderInIsoGrid(Graphics2D g, Renderable renderable, int tick);
	void renderInScreen(Graphics2D g, Renderable renderable, int tick);
	void setOrigin(int originX, int originY);
	void setDebug(boolean debug);
}
