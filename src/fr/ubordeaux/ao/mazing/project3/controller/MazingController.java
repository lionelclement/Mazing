package fr.ubordeaux.ao.mazing.project3.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import fr.ubordeaux.ao.mazing.project3.model.MazingModel;
import fr.ubordeaux.ao.mazing.project3.view.MazingView;

public class MazingController extends KeyAdapter {
    private MazingModel model;
    private MazingView view;

    public MazingController(MazingModel model, MazingView view) {
        this.model = model;
        this.view = view;
        view.setPlayerLoopTrigger(() -> {
            model.interpolatePosition();
        });
        view.refresh(model);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> model.translate(0, -0.05, 0);
            case KeyEvent.VK_DOWN -> model.translate(0, 0.05, 0);
            case KeyEvent.VK_LEFT -> model.translate(-0.05, 0, 0);
            case KeyEvent.VK_RIGHT -> model.translate(0.05, 0, 0);
            case KeyEvent.VK_Q -> {
                System.exit(0);
            }
        }
        // après modification du modèle → demander à la vue de se rafraîchir
        view.refresh(model);
    }

}
