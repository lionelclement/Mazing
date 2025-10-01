package fr.ubordeaux.ao.maze.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseManager extends MouseAdapter {

	private static boolean mouseButtonPressed;
	private static int x, y;

	public MouseManager() {
		mouseButtonPressed = false;
		x = 0;
		y = 0;

	}

	public static boolean isMouseButtonPressed() {
		return mouseButtonPressed;
	}

	public static int getX() {
		return x;
	}

	public static int getY() {
		return y;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			mouseButtonPressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			mouseButtonPressed = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}
}
