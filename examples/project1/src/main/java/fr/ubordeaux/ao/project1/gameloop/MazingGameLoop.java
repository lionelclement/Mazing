package fr.ubordeaux.ao.project1.gameloop;

import fr.ubordeaux.ao.project1.model.CharacterModel;
import fr.ubordeaux.ao.project1.model.MazingModel;
import fr.ubordeaux.ao.project1.view.MazingView;

import java.util.UUID;

import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.ICharacter;

public class MazingGameLoop {

    private MazingModel model;

    public MazingGameLoop(MazingModel model) {
        this.model = model;
    }

    public void attachViewListener(MazingView view) {
        view.attachAnimationListener(new AnimationListener() {

            @Override
            public void checkCollision(ICharacter<?> character) {
                CharacterModel characterModel = model.getCharacterById(character.getId());
                characterModel.checkCollision();
            }

            @Override
            public void updatePosition(ICharacter<?> character) {
                CharacterModel characterModel = model.getCharacterById(character.getId());
                characterModel.updatePosition();
            }

            @Override
            public void updateMode(ICharacter<?> e) {
                UUID id = e.getId();
                CharacterModel characterModel = model.getCharacterById(id);
                String mode = characterModel.getMode();
                switch (mode) {
                    case "GOTHIT" -> {
                            characterModel.setMode(Crusader.Mode.IDLE.name());
                    }
                    default -> {
                    }
                }

            }
        });
    }

}
