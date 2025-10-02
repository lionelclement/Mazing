package fr.ubordeaux.ao.mazing.project3;

import fr.ubordeaux.ao.mazing.project3.controller.MazingController;
import fr.ubordeaux.ao.mazing.project3.model.MazingModel;
import fr.ubordeaux.ao.mazing.project3.view.MazingView;

public class Main {
    public static void main(String[] args) {
        MazingModel model = new MazingModel();
        MazingView view = new MazingView();
        MazingController controller = new MazingController(model, view); // le contrôleur a tout ce qu'il lui faut
        view.addKeyListener(controller);
        view.setVisible(true);
    }

}
