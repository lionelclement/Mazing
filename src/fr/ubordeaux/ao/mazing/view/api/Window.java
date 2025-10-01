package fr.ubordeaux.ao.mazing.view.api;

import javax.swing.JFrame;

public class Window extends JFrame implements IWindow {

	private BoardGame boardGame;
	private static final int WIDTH = 1366;
	private static final int HEIGHT = 738;

	public Window() {
		super("Isometric 3D Game");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		boardGame = new BoardGame(WIDTH, HEIGHT);
		add(boardGame);
		setVisible(true);
		setFocusable(true);
		requestFocusInWindow();
	}

	@Override
	public IBoardGame getBoardGame() {
		return boardGame;
	}

}
