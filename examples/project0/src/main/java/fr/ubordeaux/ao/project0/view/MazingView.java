package fr.ubordeaux.ao.project0.view;

import fr.ubordeaux.ao.mazing.api.view.GameWindow;
import fr.ubordeaux.ao.mazing.api.view.board.BoardGameComponent;
import fr.ubordeaux.ao.mazing.impl.view.entity.CrusaderRenderer;
import fr.ubordeaux.ao.project0.model.MazingModel;

public class MazingView {

	private GameWindow gameWindow;
	private CrusaderRenderer crusaderRenderer;
	private BoardGameComponent boardGame;


	public MazingView(GameWindow gameWindow, MazingModel model) {
		this.gameWindow = gameWindow;
		boardGame = new BoardGameComponent(model.getBoardGame());
		boardGame.fillTilesBackground(72, 1.0f);
		boardGame.addToSceneMap(540, 2, 2, 0, 1.0f);
		crusaderRenderer = new CrusaderRenderer(model.getCrusader());
		
		//boardGame.getBoardGame().assignPlayer(crusader);
		
	}	



}
