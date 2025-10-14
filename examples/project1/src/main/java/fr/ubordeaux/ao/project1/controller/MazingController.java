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
			case 'h' -> {
				model.setSpeedCrusader(0, 0, 0.01f);
				model.setMode(Crusader.Mode.TURN_LEFT);
			}

			case 'H' -> model.setRelativePositionCrusader(0, 0, -1);

			case 'm' -> model.generateMaze();
			case 'M' -> model.clearMaze();
			case 'w' -> model.setMode(Crusader.Mode.WALK);
			case 'r' -> model.setMode(Crusader.Mode.RUN);
			case 'a' -> model.setMode(Crusader.Mode.ATTACK);
			case 'g' -> model.setMode(Crusader.Mode.GOTHIT);
			case 'j' -> model.setMode(Crusader.Mode.JUMP);
			case 't' -> model.setModeSpiders(Crusader.Mode.TURN_LEFT);
			case 'T' -> model.setMode(Crusader.Mode.TURN_RIGHT);
			case 'd' -> model.setMode(Crusader.Mode.DEATH);
			case 's' -> model.setMode(Crusader.Mode.IDLE);
			case 'W' -> model.setModeSpiders(Spider.Mode.WALK);
			case 'A' -> model.setModeSpiders(Spider.Mode.ATTACK);
			case 'D' -> model.setModeSpiders(Spider.Mode.DEATH);
			case 'S' -> model.setModeSpiders(Spider.Mode.IDLE);
		}

		// Actions liées aux flèches et sortie
		switch (e.getKeyCode()) {
			case KeyEvent.VK_SPACE -> {
			}
			case KeyEvent.VK_UP -> {
				if (shiftDown)
					view.scroll(0, 1);
				else if (altDown)
					model.setDirectionSpiders(Direction.NORTH);
				else {
					model.setDirectionCrusader(Direction.NORTH);
					model.setMode(Crusader.Mode.WALK);
				}
			}
			case KeyEvent.VK_DOWN -> {
				if (shiftDown)
					view.scroll(0, -1);
				else if (altDown)
					model.setDirectionSpiders(Direction.SOUTH);
				else {
					model.setDirectionCrusader(Direction.SOUTH);
					model.setMode(Crusader.Mode.WALK);
				}
			}
			case KeyEvent.VK_LEFT -> {
				if (shiftDown)
					view.scroll(1, 0);
				else if (altDown)
					model.setDirectionSpiders(Direction.WEST);
				else {
					model.setDirectionCrusader(Direction.WEST);
					model.setMode(Crusader.Mode.WALK);
				}
			}
			case KeyEvent.VK_RIGHT -> {
				if (shiftDown)
					view.scroll(-1, 0);
				else if (altDown)
					model.setDirectionSpiders(Direction.EAST);
				else {
					model.setDirectionCrusader(Direction.EAST);
					model.setMode(Crusader.Mode.WALK);
				}
			}
			case KeyEvent.VK_Q -> System.exit(0);
		}
	}
}
