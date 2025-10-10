package fr.ubordeaux.ao.project0.gameloop;

import fr.ubordeaux.ao.mazing.api.ICharacter;

public interface GameLoopListener {
	
	void updatePosition(ICharacter<?> character);

}
