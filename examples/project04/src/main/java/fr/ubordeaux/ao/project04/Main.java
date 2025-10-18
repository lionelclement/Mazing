package fr.ubordeaux.ao.project04;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.Spider;
import fr.ubordeaux.ao.mazing.api.WindowGame;

public class Main {
    public static void main(String[] args) {

        IWindowGame window = new WindowGame();  

        Crusader crusader = new Crusader();
        crusader.setScale(1f);
        crusader.setOffset(1.2f, 1.2f);
        crusader.setPosition(0, 0, 0);
        crusader.setFrameRate(Crusader.Mode.GOTHIT, 0.5f);
        //window.add(108, 0, 0, 0);
        //window.add(109, 0, 0, 0);
        //window.add(110, 0, 0, 0);
        //window.add(111, 0, 0, 0);

        // L'insère dans le windowGame
        window.add(crusader);
        window.add(12, 0, 0, 0);
        
        Spider spider = new Spider();
        spider.setPosition(1, 0, 0);
        spider.setDirection(Direction.WEST);

        // L'insère dans le windowGame
        window.add(spider);

        // Affiche le windowGame
        window.setVisible(true);

        // Illustration que WindowGame est effectivement un javax.swing.JFrame
        JFrame frame = (JFrame) window;
        JPanel panel = (JPanel) frame.getContentPane();
        JLabel label = new JLabel("Un Label !");
        JButton button = new JButton("Un Bouton !");
        button.addActionListener(_ -> {
            label.setText("Tu as appuyé !");
            spider.setMode(Spider.Mode.ATTACK);
            crusader.setMode(Crusader.Mode.ATTACK);
        });
        panel.add(label);
        panel.add(button);
        panel.revalidate();



    }
}
