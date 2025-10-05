package fr.ubordeaux.ao.mazing.project2;

import fr.ubordeaux.ao.mazing.project2.controller.MazingController;
import fr.ubordeaux.ao.mazing.project2.model.CharacterModel;
import fr.ubordeaux.ao.mazing.project2.model.MazingModel;
import fr.ubordeaux.ao.mazing.project2.view.MazingView;
import fr.ubordeaux.ao.mazing.project2.view.ViewEvent;
import fr.ubordeaux.ao.mazing.project2.view.ViewListener;

public class Main {

    public static void main(String[] args) {

        CharacterModel player = new CharacterModel("crusader");
        CharacterModel[] spiders = new CharacterModel[3];
        spiders[0] = new CharacterModel("spider1");
        spiders[1] = new CharacterModel("spider2");
        spiders[2] = new CharacterModel("spider3");

        MazingModel model = new MazingModel(3, 3, 2, player, spiders);
        model.mazeGenerate();

        // View -> Model Listener
        ViewListener viewListener = (ViewEvent event) -> {

            // System.err.printf("Model receive notification: %s\n", event.type);

            switch (event.type) {

                case GOTHIT -> {
                    String character = (String) event.attribute;
                    Object value = (Object) event.value;
                    model.setGothit(character);
                }

                case ON_STEPS -> {
                    String character = (String) event.attribute;
                    Boolean b = (Boolean) event.value;
                    model.setOnSteps(character, b);
                }

                case ON_TRAPDOOR -> {
                    String character = (String) event.attribute;
                    Boolean b = (Boolean) event.value;
                    model.setOnTrapdoor(character, b);
                }

                default -> {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

            }
        };

        MazingView view = new MazingView(viewListener);
        view.bindToModel(model);

        MazingController controller = new MazingController(model, view);
        view.addKeyListener(controller);

        controller.mazeGenerate();

        view.setVisible(true);
    }

}
