package fr.ubordeaux.ao.mazing.project2;

import fr.ubordeaux.ao.mazing.project2.controller.MazingController;
import fr.ubordeaux.ao.mazing.project2.model.MazingModel;
import fr.ubordeaux.ao.mazing.project2.view.MazingView;

public class Main {
    public static void main(String[] args) {
        MazingView view = new MazingView();
        MazingModel model = new MazingModel();
        view.bindToModel(model);

        MazingController controller = new MazingController(model, view);
        view.addKeyListener(controller);
        view.setVisible(true);
    }

}
