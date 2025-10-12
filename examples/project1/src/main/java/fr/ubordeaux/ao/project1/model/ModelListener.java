package fr.ubordeaux.ao.project1.model;

import java.util.UUID;

import fr.ubordeaux.ao.mazing.api.CharacterMode;

public interface ModelListener {

	public void onDirectionChanged(UUID id, Direction direction) ;

    void onMazeChanged(Cell[][][] grid);

    void onModeChanged(UUID id, CharacterMode mode);

    void updatePosition(UUID id, float offsetX, float offsetY, float x, float y, float z);

}
