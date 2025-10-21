package fr.ubordeaux.ao.project03;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.event.*;
import java.awt.geom.Point2D;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.WindowGame;

public class Main {
    static Integer x;
    static Integer y;

    public static void main(String[] args) {


        IWindowGame window = new WindowGame();

        ((JFrame)window).setBackground(Color.BLACK);

        window.fillArea(20072, 0, 0, 0, 5, 5, 1f, 0.3f);

        window.add(new int[][][] {{
            {20138, 20110, 20110, 20110, 20139},
            {20108, -1, -1, -1, 20111},
            {20108, -1, -1, -1, 20111},
            {20108, -1, -1, -1, 20111},
            {20136, 20109, 20109, 20109, 20137},
        }},
        1f);
        

        Crusader crusader = new Crusader();
        crusader.setBrightness(0.4f);
        
        window.add(crusader);
        

        // Affiche le windowGame
        window.setVisible(true);

        JFrame frame = (JFrame) window;
        frame.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Point2D point = window.getIsoCoordinatesFromScreen(e.getX(), e.getY());

                if (x != null && y != null) {
                    window.remove(72, x, y, 0);
                }
                x = (int)(point.getX() - 0.5f);
                y = (int)(point.getY() - 0.5f);

                crusader.setPosition(x, y, 0);
                crusader.setBrightness(1f);
                window.add(72, x, y, 0);

            }
        });

        // Exemple optionnel : écoute du mouvement
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            private int oldx;
            private int oldy;

            @Override
            public void mouseMoved(MouseEvent e) {
                Point2D point = window.getIsoCoordinatesFromScreen(e.getX(), e.getY());

                int x = (int)(point.getX() - 0.5f);
                int y = (int)(point.getY() - 0.5f);
                
                window.addTileBackground(Color.BLACK, oldx, oldy, 0);
                window.addTileBackground(Color.CYAN, x, y, 0);
                oldx = x;
                oldy = y;
            }
        });

    }
}
