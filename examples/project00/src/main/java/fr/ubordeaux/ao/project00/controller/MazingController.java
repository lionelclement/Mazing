package fr.ubordeaux.ao.project00.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.project00.model.MazingModel;

public class MazingController extends KeyAdapter {

	private final MazingModel model;

	public MazingController(MazingModel model) {
		this.model = model;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		switch (key) {
		case 'w' -> model.setMode(Crusader.Mode.WALK);
		case 'r' -> model.setMode(Crusader.Mode.RUN);
		case 'a' -> model.setMode(Crusader.Mode.ATTACK);
		case 'g' -> model.setMode(Crusader.Mode.GOTHIT);
		case 'j' -> model.setMode(Crusader.Mode.JUMP);
		case 'd' -> model.setMode(Crusader.Mode.DEATH);
		case 's' -> model.setMode(Crusader.Mode.IDLE);
		}

	}

}
