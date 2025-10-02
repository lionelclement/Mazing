package fr.ubordeaux.ao.mazing.project1;

import fr.ubordeaux.ao.mazing.project1.controller.MazingController;
import fr.ubordeaux.ao.mazing.project1.model.MazingModel;
import fr.ubordeaux.ao.mazing.project1.view.MazingView;

public class Main {
    public static void main(String[] args) {
        MazingModel model = new MazingModel();
        MazingView view = new MazingView();
        MazingController controller = new MazingController(model, view); // le contrôleur a tout ce qu'il lui faut
        view.addKeyListener(controller); // on branche le clavier
        view.setVisible(true);
    }

}
