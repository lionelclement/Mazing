package fr.ubordeaux.ao.project1.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fr.ubordeaux.ao.mazing.api.CharacterMode;

public class MazingModel {

	private ModelListener modelListener;

	private CharacterModel crusader;
	private final Map<UUID, CharacterModel> charactersById = new HashMap<>();
	private final Map<UUID, CharacterModel> crusadersById = new HashMap<>();
	private final Map<UUID, CharacterModel> spidersById = new HashMap<>();

	private Maze maze;

	public MazingModel() {
		this.maze = new Maze(6, 6, 1);
		crusader = new CharacterModel(2.0f, 2.0f, 0, -0.6f, -0.6f, Direction.SOUTH, maze);
		crusadersById.put(crusader.getId(), crusader);
		charactersById.put(crusader.getId(), crusader);

		for (int i = 1; i <= 0; i++) {
			CharacterModel characterModel = new CharacterModel(i, i + 1, 0, 0, 0,
					Direction.SOUTH, maze);
			System.err.println(characterModel);
			characterModel.speedX = 0.1f * i;
			characterModel.speedY = 0.1f * i;
			characterModel.init();
			spidersById.put(characterModel.getId(), characterModel);
			charactersById.put(characterModel.getId(), characterModel);
		}

	}

	// La vue écoute le modèle (pour les changements de mode / direction)
	public void attachModelListener(ModelListener listener) {
		this.modelListener = listener;
		for (UUID id : charactersById.keySet()) {
			charactersById.get(id).attachModelListener(listener);
		}
	}

	public void init() {
		crusader.init();
		for (UUID id : charactersById.keySet()) {
			charactersById.get(id).init();
		}
	}

	public void generateMaze() {
		if (this.modelListener != null) {
			this.modelListener.onMazeChanged(maze.generate());
		}
	}

    public void clearMaze() {
		if (this.modelListener != null) {
			this.modelListener.onMazeChanged(maze.clear());
		}
    }

	public CharacterModel getCharacterById(UUID id) {
		return this.charactersById.get(id);
	}

	public void setMode(CharacterMode mode) {
		crusader.setMode(mode);
		if (this.modelListener != null) {
			this.modelListener.onModeChanged(crusader.getId(), mode);
		}
	}

	public void setDirection(Direction direction) {
		crusader.setDirection(direction);
		if (this.modelListener != null) {
			this.modelListener.onDirectionChanged(crusader.getId(), direction);
		}
	}

	public UUID getCrusaderId() {
		return crusader.getId();
	}

	public Set<UUID> getCrusadersIds() {
		return this.crusadersById.keySet();
	}

	public Set<UUID> getSpidersIds() {
		return this.spidersById.keySet();
	}

	public void setModeSpider(CharacterMode mode) {
		for (UUID id : spidersById.keySet()) {
			CharacterModel spider = spidersById.get(id);

			spider.setMode(mode);
			if (this.modelListener != null) {
				this.modelListener.onModeChanged(id, mode);
			}
		}
	}

}
