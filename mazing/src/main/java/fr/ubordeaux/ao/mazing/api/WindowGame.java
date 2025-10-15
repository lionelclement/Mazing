package fr.ubordeaux.ao.mazing.api;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Fenêtre principale du jeu, gère le plateau isométrique.
 */
public class WindowGame extends JFrame implements IWindowGame {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_WIDTH = 1024;
    private static final int DEFAULT_HEIGHT = 665;
	/** Largeur d’une tuile en pixels */
	private final static int DEFAULT_TILE_WIDTH = 100;
	/** Hauteur d’une tuile en pixels */
	private final static int DEFAULT_TILE_HEIGHT = 100;
	private static final int DEFAULT_FRAMES_PER_SECOND = 50;

    private final BoardGame boardGame;

    public WindowGame() {
        super("Isometric 3D Game");

        // Création du plateau
        boardGame = new BoardGame(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TILE_WIDTH, DEFAULT_TILE_HEIGHT, DEFAULT_WIDTH / 2, DEFAULT_HEIGHT / 4, DEFAULT_FRAMES_PER_SECOND);

        // Ajouter le plateau au content pane
        setContentPane(boardGame);

        // Définir la taille exacte de la zone de dessin
        super.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        pack(); // Ajuste la JFrame à la taille du contentPane

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
    }
    

    @Override
    public void setTileSize(int tile_width, int tile_height) {
		TileMap.getInstance();
		TileMap.getInstance();
		TileMap.initInstance(tile_width, tile_height, TileMap.originX, TileMap.originY);
		boardGame.initImagesSheet();
    }
    
    @Override
    public void setFPS(int fps) {
		boardGame.startGameLoop(fps);
    }
    
    @Override
    public void setSize(int width, int height) {
		boardGame.setSize(width, height);
		super.setSize(width, height);
    }
    
    @Override
    public void setTitle(String s) {
    	super.setTitle(s);
    }
    
    @Override
    public void setBackground(Color c) {
		if (boardGame != null)
			boardGame.setBackground(c);
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
	public void fillTilesBackground(int code, int x, int y, int width, int height, float alpha){
		boardGame.fillTilesBackground(code, x, y, width, height, alpha);
	}

	@Override
	public void fillTilesBackground(int code, int x, int y, int width, int height){
		boardGame.fillTilesBackground(code, x, y, width, height, 1);
	}

	@Override
	public void fillCheckerboardBackground(int x, int y, int width, int height) {
		boardGame.fillCheckerboardBackground(x, y, width, height);
	}

	@Override
	public void add(ICharacter<? extends CharacterMode> character) {
		boardGame.add(character);
	}

	@Override
	public void add(int code, int x, int y, int z, float alpha) {
		boardGame.add(code, x, y, z, alpha);
	}

	@Override
	public void add(int code, int x, int y, int z) {
		boardGame.add(code, x, y, z, 1);
	}

	@Override
	public void add(int[][][] matrix) {
		boardGame.add(matrix, 1);
	}

	@Override
	public void add(int[][][] matrix, float alpha) {
		boardGame.add(matrix, alpha);
	}


	@Override
	public void clear() {
		boardGame.clear();
	}

	@Override
	public Set<Integer> getCodesFromPoint(int x, int y, int z) {
		return boardGame.getCodesFromPoint(x, y, z);
	}

	@Override
	public void scroll(int x, int y) {
		TileMap.scroll(x, y);
	}


	@Override
	public void drawTile(Color color, int x, int y, int z) {
		boardGame.drawTile(color, x, y, z);
	}


}
