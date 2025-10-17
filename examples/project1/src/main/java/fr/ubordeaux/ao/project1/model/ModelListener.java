package fr.ubordeaux.ao.project1.model;

import java.util.UUID;

public interface ModelListener {

	public void onDirectionChanged(UUID id, String direction) ;

    void onModeChanged(UUID id, String mode);

    void updatePosition(UUID id, float x, float y, float z);

    void onMazeChanged(Maze maze, float z);

}
