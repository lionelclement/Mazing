package fr.ubordeaux.ao.project1.gameloop;

import fr.ubordeaux.ao.project1.model.CharacterModel;
import fr.ubordeaux.ao.project1.model.MazingModel;
import fr.ubordeaux.ao.project1.view.AnimationListener;
import fr.ubordeaux.ao.project1.view.MazingView;

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
                //System.out.println("checkCollision");
                //model.checkCollision(character.getId());
            }

            @Override
            public void updatePosition(ICharacter<?> character) {
                CharacterModel characterModel = model.getCharacterById(character.getId());
                characterModel.updatePosition();
            }
        });
    }
	

}
