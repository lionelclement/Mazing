package fr.ubordeaux.ao.project0.gameloop;

import fr.ubordeaux.ao.project0.model.MazingModel;
import fr.ubordeaux.ao.project0.view.AnimationListener;
import fr.ubordeaux.ao.project0.view.MazingView;
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
            public void onTick(ICharacter<?> character) {
                if (character instanceof Crusader) {
                    model.updateCrusaderPosition();
                }
            }
        });
    }
	

}
