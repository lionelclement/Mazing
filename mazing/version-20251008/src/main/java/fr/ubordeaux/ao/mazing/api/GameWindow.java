package fr.ubordeaux.ao.mazing.api;

import javax.swing.*;

public class GameWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private final BoardGameJPanel view;

    private static final int WIDTH = 1366;
    private static final int HEIGHT = 738;

    public GameWindow() {
        super("Isometric 3D Game");

        view = new BoardGameJPanel(/*model, */WIDTH, HEIGHT);

        setContentPane(view);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

}
