package fr.ubordeaux.ao.project0.gameloop;

import fr.ubordeaux.ao.project0.model.MazingModel;
import fr.ubordeaux.ao.project0.view.MazingView;
import fr.ubordeaux.ao.mazing.api.Crusader;
import fr.ubordeaux.ao.mazing.api.ICharacter;
import fr.ubordeaux.ao.mazing.api.Spider;

public class MazingGameLoop {
	
	private MazingModel model;
	
	public MazingGameLoop(MazingModel model) {
		this.model = model;
	}

	public void attachViewListener(MazingView view) {
	    view.attachGameLoopListener(new GameLoopListener() {
	        @Override
	        public void updatePosition(ICharacter<?> character) {
	            if (character instanceof Crusader) {
	            	//System.out.println("VIEW ==> GAMELOOP");
		            model.updateCrusaderPosition();
	            }
	        }
	    });
	}

}
