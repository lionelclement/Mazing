package fr.ubordeaux.ao.mazing.api.view;

import fr.ubordeaux.ao.mazing.api.board.IBoardGame;

import javax.swing.JFrame;

/**
 * Représente la fenêtre principale du jeu.
 * Étend {@link JFrame} pour fournir une interface graphique
 * et expose l'accès au plateau de jeu ({@link IBoardGame}).
 */
public class GameWindow extends JFrame {

	private IBoardGame boardGame;
	public static final int WIDTH = 1366;
	public static final int HEIGHT = 738;

	public GameWindow(IBoardGame boardGame) {
		super("Isometric 3D Game");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		this.boardGame = boardGame;
		add(new BoardGameComponent(boardGame));
		setVisible(true);
		setFocusable(true);
		requestFocusInWindow();
	}

	/**
	 * Retourne le plateau de jeu associé à cette fenêtre.
	 *
	 * @return l'instance d'IBoardGame affichée dans cette fenêtre.
	 */
	public IBoardGame getBoardGame() {
		return boardGame;
	}

}
