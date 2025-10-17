package fr.ubordeaux.ao.project00;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.Crusader.Mode;
import fr.ubordeaux.ao.mazing.api.IWindowGame;
import fr.ubordeaux.ao.mazing.api.WindowGame;

public class Main {
    public static void main(String[] args) {

        IWindowGame window = new WindowGame();
        window.setFPS(65);

        Crusader crusader;

        // Ajoute un nouveau personnage
        crusader = new Crusader();

        // L'insère dans le windowGame
        window.add(crusader);

        // Affiche le windowGame
        window.setVisible(true);

        // Illustration que WindowGame est effectivement un javax.swing.JFrame
        JFrame frame = (JFrame) window;
        JPanel panel = (JPanel) frame.getContentPane();
        JLabel label = new JLabel("Un Label !");
        JButton button = new JButton("Un Bouton !");
        button.addActionListener(e -> {
            label.setText("Tu as appuyé !");
            crusader.setMode(Mode.ATTACK);
        });
        panel.add(label);
        panel.add(button);
        panel.revalidate();



    }
}
