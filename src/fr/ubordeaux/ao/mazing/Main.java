package fr.ubordeaux.ao.mazing;

import fr.ubordeaux.ao.mazing.controller.MazingController;
import fr.ubordeaux.ao.mazing.model.MazingModel;
import fr.ubordeaux.ao.mazing.view.MazingView;
import fr.ubordeaux.ao.mazing.view.api.Crusader;
import fr.ubordeaux.ao.mazing.view.api.IBoardGame;

public class Main {
    public static void main(String[] args) {
        MazingModel model = new MazingModel();
        MazingView view = new MazingView();
        MazingController controller = new MazingController(model, view); // le contrôleur a tout ce qu'il lui faut
        view.addKeyListener(controller); // on branche le clavier
        view.setVisible(true);
        initView(view);
    }

    private static void initView(MazingView view) {
        IBoardGame boardGame = view.getBoardGame();
        Crusader crusader = new Crusader();
        boardGame.assignPlayer(crusader);
        boardGame.addToSceneMap(crusader);
    }
}
