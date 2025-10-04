package fr.ubordeaux.ao.mazing.project2;

import fr.ubordeaux.ao.mazing.project2.controller.MazingController;
import fr.ubordeaux.ao.mazing.project2.model.MazingModel;
import fr.ubordeaux.ao.mazing.project2.view.MazingView;
import fr.ubordeaux.ao.mazing.project2.view.ViewEvent;
import fr.ubordeaux.ao.mazing.project2.view.ViewListener;

public class Main {
    public static void main(String[] args) {
        MazingView view = new MazingView();
        MazingModel model = new MazingModel(3, 3, 5);
        view.bindToModel(model);

        MazingController controller = new MazingController(model, view);
        view.addKeyListener(controller);

        // Vue notifie aussi le modèle
        view.setViewListener(new ViewListener() {
            @Override
            public void onViewEvent(ViewEvent event) {
                switch (event.type) {

                    case PLAYER_GOTHIT -> {
                        model.gothit();
                    }

                    case PLAYER_ON_STEPS -> {
                        Boolean b = (Boolean) event.data;
                        model.setOnSteps(b);
                    }

                    case PLAYER_ON_TRAPDOOR -> {
                        Boolean b = (Boolean) event.data;
                        model.setOnTrapdoor(b);
                    }

                }
            }
        });

        model.mazeGenerate();
        view.setVisible(true);
    }

}
