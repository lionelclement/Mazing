package fr.ubordeaux.ao.project1.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import fr.ubordeaux.ao.project1.model.MazingModel;
import fr.ubordeaux.ao.project1.view.MazingView;

public class MazingController extends KeyAdapter {
    private final MazingModel model;

    public MazingController(MazingModel model, MazingView view) {
        this.model = model;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char key = e.getKeyChar();

        System.err.println("KEYPRESSED: CONTROLLER -> MODEL");
        // Actions liées aux lettres
        switch (key) {
            case 'a' -> model.setPlayerMode("ATTACK");
            case 'g' -> model.setPlayerMode("GOTHIT");
            case 'j' -> model.setPlayerMode("JUMP");
            case 'd' -> {
                model.setPlayerMode("DEATH");
                model.setPlayerSpeed(0, 0, 0);
            }
            case 's' -> {
                model.setPlayerMode("IDLE");
                model.setPlayerSpeed(0, 0, 0);
            }
        }

        // Actions liées aux flèches et sortie
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
            case KeyEvent.VK_Q -> System.exit(0);
        }
    }

}
