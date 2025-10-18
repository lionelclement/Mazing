package fr.ubordeaux.ao.project03;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.event.*;
import java.awt.geom.Point2D;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.WindowGame;

public class Main {
    public static void main(String[] args) {

        IWindowGame window = new WindowGame();

        ((JFrame)window).setBackground(Color.WHITE);

        window.fillCheckerboardBackground(-40, -40, 80, 80);

        window.fillArea(72, 0, 0, 0, 5, 5);

        window.add(new int[][][] {{
            {138, 110, 110, 110, 139},
            {108, -1, -1, -1, 111},
            {108, -1, -1, -1, 111},
            {108, -1, -1, -1, 111},
            {136, 109, 109, 109, 137},
        }} );
        

        Crusader crusader = new Crusader();
        window.add(crusader);
        
        // Affiche le windowGame
        window.setVisible(true);

        JFrame frame = (JFrame) window;
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Clic détecté à : (" + e.getX() + ", " + e.getY() + ")");
                Point2D point = window.getIsoCoordinatesFromScreen(e.getX(), e.getY());

                int x = (int)(point.getX() - 0.5f);
                int y = (int)(point.getY() - 0.5f);

                crusader.setPosition(x, y, 0);

            }
        });

        // Exemple optionnel : écoute du mouvement
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            private int oldx;
            private int oldy;

            @Override
            public void mouseMoved(MouseEvent e) {
                System.out.println("Souris déplacée à : (" + e.getX() + ", " + e.getY() + ")");

                Point2D point = window.getIsoCoordinatesFromScreen(e.getX(), e.getY());

                int x = (int)(point.getX() - 0.5f);
                int y = (int)(point.getY() - 0.5f);
                
                window.addTileBackground(Color.WHITE, oldx, oldy, 0);
                window.addTileBackground(Color.CYAN, x, y, 0);
                oldx = x;
                oldy = y;
            }
        });

    }
}
