package fr.ubordeaux.ao.mazing.api;

import javax.swing.*;

public class GameWindow extends JFrame {

    private static final long serialVersionUID = 1L;
	private final BoardGameModel model;
    private final BoardGameView view;
    private final BoardGameController controller;

    private static final int WIDTH = 1366;
    private static final int HEIGHT = 738;

    public GameWindow() {
        super("Isometric 3D Game");

        model = new BoardGameModel(WIDTH, HEIGHT);
        view = new BoardGameView(model, WIDTH, HEIGHT);
        controller = new BoardGameController(model, view);

        setContentPane(view);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public BoardGameController getController() {
        return controller;
    }
}
