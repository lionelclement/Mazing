package fr.ubordeaux.ao.mazing.project2;

import fr.ubordeaux.ao.mazing.project2.controller.MazingController;
import fr.ubordeaux.ao.mazing.project2.model.MazingModel;
import fr.ubordeaux.ao.mazing.project2.model.Point;
import fr.ubordeaux.ao.mazing.project2.view.MazingView;

public class Main {
    public static void main(String[] args) {
        MazingView view = new MazingView();
        MazingModel model = new MazingModel(3, 3, 2);
        view.bindToModel(model);

        MazingController controller = new MazingController(model, view);
        view.addKeyListener(controller);

        // Vue notifie aussi le modèle
        view.setViewListener(event -> {
            switch (event.type) {
                case PLAYER_MOVE -> {
                    System.err.println("Receive view -> model");
                    Point p = (Point) event.data;
                    model.setPlayerPosition((float) p.getX(), (float) p.getY(), (float) p.getZ());
                }
                case OTHER_ACTION -> {
                    // tu pourrais rajouter d'autres actions (tir, ramasser un objet, etc.)
                }
            }
        });

        model.mazeGenerate();
        view.setVisible(true);
    }

}
