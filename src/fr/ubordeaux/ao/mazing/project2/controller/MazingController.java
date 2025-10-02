package fr.ubordeaux.ao.mazing.project2.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import fr.ubordeaux.ao.mazing.project2.model.MazingModel;
import fr.ubordeaux.ao.mazing.project2.view.MazingView;

public class MazingController extends KeyAdapter {
    private MazingModel model;

    public MazingController(MazingModel model, MazingView view) {
        this.model = model;
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyChar() == 'a') {
            model.setPlayerMode("ATTACK");
        }

        if (e.getKeyChar() == 'j') {
            model.setPlayerMode("JUMP");
        }

        if (e.getKeyChar() == 'd') {
            model.setPlayerMode("DEATH");
            model.setPlayerSpeed(0, 0, 0);
        }

        if (e.getKeyChar() == 's') {
            model.setPlayerMode("IDLE");
            model.setPlayerSpeed(0, 0, 0);
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> {
                model.setPlayerMode("WALK");
                model.setPlayerSpeed(0, -0.05f, 0);
            }
            case KeyEvent.VK_DOWN -> {
                model.setPlayerMode("WALK");
                model.setPlayerSpeed(0, 0.05f, 0);
            }
            case KeyEvent.VK_LEFT -> {
                model.setPlayerMode("WALK");
                model.setPlayerSpeed(-0.05f, 0, 0);
            }
            case KeyEvent.VK_RIGHT -> {
                model.setPlayerMode("WALK");
                model.setPlayerSpeed(0.05f, 0, 0);
            }
            case KeyEvent.VK_Q -> {
                System.exit(0);
            }
        }
    }

}
