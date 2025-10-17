package fr.ubordeaux.ao.project1;

import fr.ubordeaux.ao.mazing.api.WindowGame;

import fr.ubordeaux.ao.project1.controller.MazingController;
import fr.ubordeaux.ao.project1.model.MazingModel;
import fr.ubordeaux.ao.project1.view.MazingView;
import fr.ubordeaux.ao.project1.gameloop.MazingGameLoop;


public class Main {
    public static void main(String[] args) {
    	
    	WindowGame window = new WindowGame();
		
    	MazingModel model = new MazingModel();
    	MazingView view = new MazingView(window);
    	MazingController controller = new MazingController(model, view);
    	MazingGameLoop gameLoop = new MazingGameLoop(model);
    	
    	window.addKeyListener(controller);
    	
    	view.attachModelListener(model);
    	gameLoop.attachViewListener(view);

		view.createCrusader(model.getCrusaderId());
		view.createSpiders(model.getSpidersIds());
    	
		model.init();
		model.generateMaze();
    	
    }

}
