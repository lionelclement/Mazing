package fr.ubordeaux.ao.project0.model;

import java.util.HashMap;
import java.util.Map;

import fr.ubordeaux.ao.mazing.api.Crusader.Mode;
import fr.ubordeaux.ao.mazing.api.Direction;

public class MazingModel {

    private final Map<String, CharacterModel> characters = new HashMap<>();
	private CharacterModel crusader;

	public MazingModel() {
		crusader = new CharacterModel(5, 5, Direction.SOUTH);
	}
	
    public void addCharacter(String id, CharacterModel character) {
        characters.put(id, character);
    }

    public CharacterModel getCharacter(String id) {
        return characters.get(id);
    }

    public void updateAllPositions() {
        for (CharacterModel c : characters.values()) {
            c.updatePosition();
        }
    }

    public void updateAllEnd() {
        for (CharacterModel c : characters.values()) {
            c.updateEnd();
        }
    }

	public void setMode(Mode mode) {
		crusader.setMode(mode);
	}

	public void updateCrusaderPosition() {
		crusader.updatePosition();
	}

	public float getX() {
		return crusader.getX();
	}

	public float getY() {
		return crusader.getY();
	}

	public float getZ() {
		return crusader.getZ();
	}

	public void updateCrusaderBegin() {
	}

	public void updateCrusaderEnd() {
		crusader.updateEnd();
	}

	public void updateCrusaderMid() {
	}

	public void setListener(ModelListener modelListener) {
		crusader.setListener(modelListener);
	}

	public void setDirection(Direction direction) {
		crusader.setDirection(direction);
	}

	public void init() {
		crusader.init();
	}
}
