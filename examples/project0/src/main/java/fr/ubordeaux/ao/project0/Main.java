package fr.ubordeaux.ao.project0;


import fr.ubordeaux.ao.mazing.api.WindowGame;
import fr.ubordeaux.ao.project0.controller.MazingController;
import fr.ubordeaux.ao.project0.model.MazingModel;
import fr.ubordeaux.ao.project0.view.MazingView;

public class Main {
    public static void main(String[] args) {
    	
    	MazingModel model = new MazingModel();
    	WindowGame window = new WindowGame();
    	MazingView view = new MazingView(window, model);
    	
    	MazingController controller = new MazingController(model, view);
    	
    	window.addKeyListener(controller);
    	
    	
    	
    }

}
