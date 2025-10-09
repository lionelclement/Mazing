package fr.ubordeaux.ao.mazing.api;

import javax.swing.*;

import java.awt.*;

public class BoardGameView extends JPanel {

    private static final long serialVersionUID = 1L;
	private final BoardGameModel model;

    public BoardGameView(BoardGameModel model, int width, int height) {
        this.model = model;
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dessin du plateau à partir du modèle
        int[][][] tiles = model.getTiles();
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                for (int z = 0; z < tiles[0][0].length; z++) {
                    int code = tiles[x][y][z];
                    if (code != 0) {
                        g.setColor(new Color(100 + code*10, 100, 100)); // Exemple
                        g.fillRect(x * 32, y * 32, 32, 32);
                    }
                }
            }
        }
    }
}
