package fr.ubordeaux.ao.project0;


import fr.ubordeaux.ao.mazing.api.WindowGame;

import fr.ubordeaux.ao.project0.controller.MazingController;
import fr.ubordeaux.ao.project0.model.MazingModel;
import fr.ubordeaux.ao.project0.view.MazingView;
import fr.ubordeaux.ao.project0.gameloop.MazingGameLoop;


public class Main {
    public static void main(String[] args) {
    	
    	WindowGame window = new WindowGame();

    	MazingModel model = new MazingModel();
    	MazingGameLoop gameLoop = new MazingGameLoop(model);
    	MazingView view = new MazingView(window, model);
    	MazingController controller = new MazingController(model, view);
    	
    	window.addKeyListener(controller);
    	
    	view.attachModelListener(model);
    	gameLoop.attachViewListener(view);

    	model.init();
    	
    	
    }

}
