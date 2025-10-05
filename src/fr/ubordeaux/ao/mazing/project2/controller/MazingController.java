package fr.ubordeaux.ao.mazing.project2.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import fr.ubordeaux.ao.mazing.project2.model.MazingModel;
import fr.ubordeaux.ao.mazing.project2.model.Mode;
import fr.ubordeaux.ao.mazing.project2.view.MazingView;

public class MazingController extends KeyAdapter {
    private final MazingModel model;
    private static final float SPEED_WALK = 0.05f;

    public MazingController(MazingModel model, MazingView view) {
        this.model = model;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        char c = e.getKeyChar();

        // --- Modes directs (touches simples)
        switch (c) {
            case 'a' -> model.changeMode("crusader", Mode.ATTACK);
            case 'g' -> model.changeMode("crusader", Mode.GOTHIT);
            case 'j' -> model.changeMode("crusader", Mode.JUMP);
            case 'd' -> stopPlayer(Mode.DEATH);
            case 's' -> stopPlayer(Mode.IDLE);
            case 'm' -> model.mazeGenerate();
            case 'D' -> model.changeMode("spider1", Mode.DEATH);
            case 'h' -> moveVertical(1);
            case 'H' -> moveVertical(-1);
        }

        // --- Déplacements (touches directionnelles)
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> move(0, -SPEED_WALK, 0);
            case KeyEvent.VK_DOWN -> move(0, SPEED_WALK, 0);
            case KeyEvent.VK_LEFT -> move(-SPEED_WALK, 0, 0);
            case KeyEvent.VK_RIGHT -> move(SPEED_WALK, 0, 0);
            case KeyEvent.VK_Q -> System.exit(0);
        }
    }

    /** Déplace le joueur dans le plan XY et active le mode WALK */
    private void move(float dx, float dy, float dz) {
        model.changePlayerMode(Mode.WALK);
        model.setPlayerSpeed(dx, dy, dz);
    }

    /** Monte ou descend d’un étage si possible */
    private void moveVertical(int direction) {
        if (direction > 0 && model.isPlayerOnSteps() && model.getPlayerPositionZ() < model.getMazeDepth() - 1) {
            model.setPlayerPositionZ((float) (model.getPlayerPositionZ() + 1));
        } else if (direction < 0 && model.isPlayerOnTrapdoor() && model.getPlayerPositionZ() > 0) {
            model.setPlayerPositionZ((float) (model.getPlayerPositionZ() - 1));
        }
    }

    /** Met le joueur dans un mode et stoppe son déplacement */
    private void stopPlayer(Mode mode) {
        model.changeMode("crusader", mode);
        model.setPlayerSpeed(0, 0, 0);
    }

    public void mazeGenerate() {
        model.mazeGenerate();
    }
}
