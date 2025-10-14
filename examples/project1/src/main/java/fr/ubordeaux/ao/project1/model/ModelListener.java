package fr.ubordeaux.ao.project1.model;

import java.util.UUID;

import fr.ubordeaux.ao.mazing.api.CharacterMode;
import fr.ubordeaux.ao.mazing.api.Direction;

public interface ModelListener {

	public void onDirectionChanged(UUID id, Direction direction) ;

    void onModeChanged(UUID id, CharacterMode mode);

    void updatePosition(UUID id, float x, float y, float z);

    void onMazeChanged(Maze maze, float z);

}
