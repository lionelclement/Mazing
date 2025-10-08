package fr.ubordeaux.ao.project0.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import fr.ubordeaux.ao.project0.model.MazingModel;
import fr.ubordeaux.ao.project0.view.MazingView;

public class MazingController extends KeyAdapter {

	private final MazingModel model;
	private MazingView view;

	public MazingController(MazingModel model, MazingView view) {
		this.model = model;
		this.view = view;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();

		// Actions liées aux lettres
		switch (key) {
		case 'w' -> view.getCrusader().setMode("WALK");
		case 'r' -> view.getCrusader().setMode("RUN");
		case 'a' -> view.getCrusader().setMode("ATTACK");
		case 'g' -> view.getCrusader().setMode("GOTHIT");
		case 'j' -> view.getCrusader().setMode("JUMP");
		case 'd' -> view.getCrusader().setMode("DEATH");
		case 's' -> view.getCrusader().setMode("IDLE");
		}

		// Actions liées aux flèches et sortie
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP -> {
			view.getCrusader().setDirection("NORTH");
		}
		case KeyEvent.VK_DOWN -> {
			view.getCrusader().setDirection("SOUTH");
		}
		case KeyEvent.VK_LEFT -> {
			view.getCrusader().setDirection("WEST");
		}
		case KeyEvent.VK_RIGHT -> {
			view.getCrusader().setDirection("EAST");
		}
		case KeyEvent.VK_Q -> System.exit(0);
		}
	}

}
