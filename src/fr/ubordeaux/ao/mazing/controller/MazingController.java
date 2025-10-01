package fr.ubordeaux.ao.mazing.controller;

import fr.ubordeaux.ao.mazing.model.MazingModel;
import fr.ubordeaux.ao.mazing.view.MazingView;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MazingController extends KeyAdapter {
    private MazingModel model;
    private MazingView view;

    public MazingController(MazingModel model, MazingView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> model.setPlayerSpeed(0, -0.05f, 0);
            case KeyEvent.VK_DOWN -> model.setPlayerSpeed(0, 0.05f, 0);
            case KeyEvent.VK_LEFT -> model.setPlayerSpeed(-0.05f, 0, 0);
            case KeyEvent.VK_RIGHT -> model.setPlayerSpeed(0.05f, 0, 0);
        }
        // après modification du modèle → demander à la vue de se rafraîchir
        view.refresh(model);
    }

}
