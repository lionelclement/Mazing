package fr.ubordeaux.ao.project1.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.project1.view.MazingView;

public class MazingModel {

	private ModelListener modelListener;

	private CharacterModel crusader;
	private final Map<UUID, CharacterModel> charactersById = new HashMap<>();
	private final Map<UUID, CharacterModel> crusadersById = new HashMap<>();
	private final Map<UUID, CharacterModel> spidersById = new HashMap<>();

	private Maze maze;

	public MazingModel() {
		this.maze = new Maze(6, 6, 4);
		crusader = new CharacterModel(CharacterModel.Type.CRUSADER, 1.0f, 5.0f, 0, 0.2f, 0.2f, Direction.SOUTH.name(), maze);
		crusadersById.put(crusader.getId(), crusader);
		charactersById.put(crusader.getId(), crusader);

		for (int i = 1; i <= 1; i++) {
			CharacterModel characterModel = new CharacterModel(CharacterModel.Type.SPIDER, 1 * i, 1 * i, 0,
					0.3f, 0.2f, Direction.SOUTH.name(), maze);
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
		maze.generate();
		if (this.modelListener != null) {
			this.modelListener.onMazeChanged(maze, crusader.getZ());
		}
	}

	public void clearMaze() {
		maze.clear();
		if (this.modelListener != null) {
			this.modelListener.onMazeChanged(maze, crusader.getZ());
		}
	}

	public CharacterModel getCharacterById(UUID id) {
		return this.charactersById.get(id);
	}

	public void setMode(String mode) {
		crusader.setMode(mode);
		if (this.modelListener != null) {
			this.modelListener.onModeChanged(crusader.getId(), mode);
		}
	}

	public void setDirectionCrusader(String direction) {
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

	public void setModeSpiders(String mode) {
		for (UUID id : spidersById.keySet()) {
			CharacterModel spider = spidersById.get(id);

			spider.setMode(mode);
			if (this.modelListener != null) {
				this.modelListener.onModeChanged(id, mode);
			}
		}
	}

	public void setDirectionSpiders(String north) {
		for (UUID id : spidersById.keySet()) {
			CharacterModel spider = spidersById.get(id);

			spider.setDirection(north);
			if (this.modelListener != null) {
				this.modelListener.onDirectionChanged(id, north);
			}
		}
	}

	public void printMaze() {
		System.out.println(maze);
	}

	public void setRelativePositionCrusader(int x, int y, int z) {
		crusader.setPosition(crusader.getX() + x, crusader.getY() + y, crusader.getZ() + z);
		if (this.modelListener != null) {
			this.modelListener.onMazeChanged(maze, crusader.getZ());
		}

	}

	public void setSpeedCrusader(float x, float y, float z) {
		crusader.setSpeed(x, y, z);
	}

	public void crusaderClimbsTheStairs() {
		int cellX = (int) (crusader.getX() / MazingView.CELL_WIDTH);
		int cellY = (int) (crusader.getY() / MazingView.CELL_HEIGHT);
		int cellZ = (int) (crusader.getZ() / MazingView.CELL_DEPTH);

		Cell cell = maze.getCell(cellX, cellY, cellZ);

		if (cell.isPath(Direction3D.UP)) {
			crusader.climbsTheStairs();
		}
	}

	public void crusaderDescendsTheStairs() {
		int cellX = (int) (crusader.getX() / MazingView.CELL_WIDTH);
		int cellY = (int) (crusader.getY() / MazingView.CELL_HEIGHT);
		int cellZ = (int) (crusader.getZ() / MazingView.CELL_DEPTH);

		Cell cell = maze.getCell(cellX, cellY, cellZ);

		if (cell.isPath(Direction3D.DOWN)) {
			crusader.crusaderDescendsTheStairs();
		}
	}

}
