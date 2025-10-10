package fr.ubordeaux.ao.project0.model;

import fr.ubordeaux.ao.mazing.api.Crusader.Mode;
import fr.ubordeaux.ao.mazing.api.Direction;

public interface ModelListener {

	public void onModeChanged(Mode mode) ;

	public void onDirectionChanged(Direction direction) ;

}
