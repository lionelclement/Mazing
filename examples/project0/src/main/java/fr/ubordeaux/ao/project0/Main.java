package fr.ubordeaux.ao.project0;

import fr.ubordeaux.ao.mazing.api.view.GameWindow;
import fr.ubordeaux.ao.project0.controller.MazingController;
import fr.ubordeaux.ao.project0.model.MazingModel;
import fr.ubordeaux.ao.project0.view.MazingView;

public class Main {
    public static void main(String[] args) {
    	GameWindow gameWindow = new GameWindow();

        MazingModel model = new MazingModel();
        MazingView view = new MazingView(gameWindow, model);
        //gameWindow.getBoardGame()
        MazingController controller = new MazingController(model, view);
    }

}
