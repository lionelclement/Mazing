package fr.ubordeaux.ao.project1.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.Spider;
import fr.ubordeaux.ao.project1.model.MazingModel;
import fr.ubordeaux.ao.project1.view.MazingView;

public class MazingController extends KeyAdapter {

	private final MazingModel model;
	private final MazingView view;

	public MazingController(MazingModel model, MazingView view) {
		this.model = model;
		this.view = view;
	}

	@Override
	public void keyPressed(KeyEvent e) {

		// System.out.println("Controller@keyPressed");

		char key = e.getKeyChar();
		boolean shiftDown = e.isShiftDown();
		boolean altDown = e.isAltDown();

		// Actions liées aux lettres
		switch (key) {
			case 'h' -> model.crusaderClimbsTheStairs();
			case 'H' -> model.crusaderDescendsTheStairs();
			case 'm' -> model.generateMaze();
			case 'M' -> model.clearMaze();
			case 'w' -> model.setMode(Crusader.Mode.WALK.name());
			case 'r' -> model.setMode(Crusader.Mode.RUN.name());
			case 'a' -> model.setMode(Crusader.Mode.ATTACK.name());
			case 'g' -> model.setMode(Crusader.Mode.GOTHIT.name());
			case 'j' -> model.setMode(Crusader.Mode.JUMP.name());
			case 't' -> model.setModeSpiders(Crusader.Mode.TURN_LEFT.name());
			case 'T' -> model.setMode(Crusader.Mode.TURN_RIGHT.name());
			case 'd' -> model.setMode(Crusader.Mode.DEATH.name());
			case 's' -> model.setMode(Crusader.Mode.IDLE.name());
			case 'W' -> model.setModeSpiders(Spider.Mode.WALK.name());
			case 'A' -> model.setModeSpiders(Spider.Mode.ATTACK.name());
			case 'D' -> model.setModeSpiders(Spider.Mode.DEATH.name());
			case 'S' -> model.setModeSpiders(Spider.Mode.IDLE.name());
		}

		// Actions liées aux flèches et sortie
		switch (e.getKeyCode()) {
			case KeyEvent.VK_SPACE -> {
			}
			case KeyEvent.VK_UP -> {
				if (shiftDown)
					view.scroll(0, 1);
				else if (altDown)
					model.setDirectionSpiders(Direction.NORTH.name());
				else {
					model.setDirectionCrusader(Direction.NORTH.name());
					model.setMode(Crusader.Mode.WALK.name());
				}
			}
			case KeyEvent.VK_DOWN -> {
				if (shiftDown)
					view.scroll(0, -1);
				else if (altDown)
					model.setDirectionSpiders(Direction.SOUTH.name());
				else {
					model.setDirectionCrusader(Direction.SOUTH.name());
					model.setMode(Crusader.Mode.WALK.name());
				}
			}
			case KeyEvent.VK_LEFT -> {
				if (shiftDown)
					view.scroll(1, 0);
				else if (altDown)
					model.setDirectionSpiders(Direction.WEST.name());
				else {
					model.setDirectionCrusader(Direction.WEST.name());
					model.setMode(Crusader.Mode.WALK.name());
				}
			}
			case KeyEvent.VK_RIGHT -> {
				if (shiftDown)
					view.scroll(-1, 0);
				else if (altDown)
					model.setDirectionSpiders(Direction.EAST.name());
				else {
					model.setDirectionCrusader(Direction.EAST.name());
					model.setMode(Crusader.Mode.WALK.name());
				}
			}
			case KeyEvent.VK_Q -> System.exit(0);
		}
	}
}
