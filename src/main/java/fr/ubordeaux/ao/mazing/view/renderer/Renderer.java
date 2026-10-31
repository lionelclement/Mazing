package fr.ubordeaux.ao.mazing.view.renderer;

import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.Renderable;

import java.awt.Graphics2D;

/**
 * Represents Renderer in the rendering system.
 */
public interface Renderer {
	/**
	 * Renders an object using isometric grid coordinates.
	  * @param g graphics context
	  * @param renderable object to render
	  * @param tick animation tick
	  * @return the value or object described above
	 */
	Point renderInIsoGrid(Graphics2D g, Renderable renderable, int tick);
	/**
	 * Renders an object in screen coordinates.
	  * @param g graphics context
	  * @param renderable object to render
	  * @param tick animation tick
	 */
	void renderInScreen(Graphics2D g, Renderable renderable, int tick);
	/**
	 * Sets origin.
	  * @param originX horizontal rendering origin
	  * @param originY vertical rendering origin
	 */
	void setOrigin(int originX, int originY);
	/**
	 * Sets debug.
	  * @param debug whether debugging is enabled
	 */
	void setDebug(boolean debug);
}
