package fr.ubordeaux.ao.project0.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.project0.model.MazingModel;
import fr.ubordeaux.ao.project0.view.MazingView;

public class MazingController extends KeyAdapter {

	private final MazingModel model;
	private final MazingView view;

	public MazingController(MazingModel model, MazingView view) {
		this.model = model;
		this.view = view;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		//System.out.println("Controller@keyPressed");
		
		
		char key = e.getKeyChar();
		boolean shiftDown = e.isShiftDown();
		
		// Actions liées aux lettres
		switch (key) {
		case 'w' -> model.setMode(Crusader.Mode.WALK);
		case 'r' -> model.setMode(Crusader.Mode.RUN);
		case 'a' -> model.setMode(Crusader.Mode.ATTACK);
		case 'g' -> model.setMode(Crusader.Mode.GOTHIT);
		case 'j' -> model.setMode(Crusader.Mode.JUMP);
		case 'd' -> model.setMode(Crusader.Mode.DEATH);
		case 's' -> model.setMode(Crusader.Mode.IDLE);
		}

		// Actions liées aux flèches et sortie
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SPACE -> {
		}
		case KeyEvent.VK_UP -> {
			if (shiftDown)
				view.scrollUp();
			else
				model.setDirection(Direction.NORTH);
		}
		case KeyEvent.VK_DOWN -> {
			if (shiftDown)
				view.scrollDown();
			else
				model.setDirection(Direction.SOUTH);
		}
		case KeyEvent.VK_LEFT -> {
			if (shiftDown)
				view.scrollLeft();
			else
				model.setDirection(Direction.WEST);
		}
		case KeyEvent.VK_RIGHT -> {
			if (shiftDown)
				view.scrollRight();
			else
				model.setDirection(Direction.EAST);
		}
		case KeyEvent.VK_Q -> System.exit(0);
		}
	}

}
