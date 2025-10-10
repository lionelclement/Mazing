package fr.ubordeaux.ao.mazing.api;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

class BoardGameJPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public BoardGameJPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
