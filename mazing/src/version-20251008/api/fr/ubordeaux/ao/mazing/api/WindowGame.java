package fr.ubordeaux.ao.mazing.api;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Fenêtre principale du jeu, gère le plateau isométrique.
 */
public class WindowGame extends JFrame implements IWindowGame {

    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 1366;
    private static final int HEIGHT = 738;

    private final BoardGame boardGame;

    public WindowGame() {
        super("Isometric 3D Game");

        // Création du plateau
        boardGame = new BoardGame(WIDTH, HEIGHT);

        // Ajouter le plateau au content pane
        setContentPane(boardGame);

        // Définir la taille exacte de la zone de dessin
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack(); // Ajuste la JFrame à la taille du contentPane

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer
    }
    
    @Override
    public void setTitle(String s) {
    	super.setTitle(s);
    }
    
    @Override
    public void setBackground(Color c) {
    	super.setBackground(c);
    }
    
    @Override
    public void setResizable(boolean b) {
    	super.setResizable(b);
    }
    
    @Override
    public void setVisible(boolean b) {
    	super.setVisible(b);
    }
    

	@Override
	public void fillTilesBackground(int code, float alpha) {
		this.boardGame.fillTilesBackground(code, alpha);
	}

	@Override
	public void fillCheckerboardBackground(int size) {
		this.boardGame.fillCheckerboardBackground(size);
	}

	@Override
	public void add(ICharacter<? extends CharacterMode> character) {
		this.boardGame.add(character);
	}

	@Override
	public void add(int code, int x, int y, int z, float alpha) {
		this.boardGame.add(code, x, y, z, alpha);
	}

	@Override
	public void add(int[][][] matrix) {
		this.boardGame.add(matrix);
	}

	@Override
	public void clear() {
		this.boardGame.clear();
	}

	@Override
	public Set<Integer> getCodesFromPoint(int x, int y, int z) {
		return this.boardGame.getCodesFromPoint(x, y, z);
	}

	@Override
	public void centerWindow(int x, int y, int z) {
		// TODO Auto-generated method stub
		
	}

}
