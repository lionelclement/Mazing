package fr.ubordeaux.ao.project1.model;

import java.util.UUID;

import fr.ubordeaux.ao.mazing.api.CharacterMode;
import fr.ubordeaux.ao.mazing.api.Direction;

public interface ModelListener {

	public void onDirectionChanged(UUID id, Direction direction) ;

    void onMazeChanged(Cell[][][] grid);

    void onModeChanged(UUID id, CharacterMode mode);

    public void updatePosition(UUID id, float x, float y, float z);

}
