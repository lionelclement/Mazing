package fr.ubordeaux.ao.mazing.project2.model;

import java.util.HashMap;
import java.util.Map;

public class MazingModel {

        private Maze maze;
        private ModelListener listener;
        private CharacterModel player;
        private Map<String, CharacterModel> characters = new HashMap<>();

        public MazingModel(int width, int height, int depth, CharacterModel player, CharacterModel[] spiders) {
                maze = new Maze(width, height, depth);
                this.player = player;
                characters.put(player.toString(), player);
                for (CharacterModel characterModel : spiders) {
                        characters.put(characterModel.toString(), characterModel);
                }
        }

        public void setListener(ModelListener listener) {
                this.listener = listener;
        }

        public void setPlayerPositionZ(float z) {
                double dz = player.getPosition().getZ() - z;
                player.getPosition().setZ(z);
                if (dz * dz > 0) {
                        notifyView(new ModelEvent(ModelEvent.Type.PLAYER_POSITIONZ, z));
                        notifyView(new ModelEvent(ModelEvent.Type.MAZE, maze));
                }

        }

        public void setPlayerSpeed(float playerSpeedX, float playerSpeedY, float playerSpeedZ) {
                Point speed = new Point(playerSpeedX, playerSpeedY, playerSpeedZ);
                notifyView(new ModelEvent(ModelEvent.Type.PLAYER_SPEED, speed));
        }

        public double getPlayerPositionZ() {
                notifyView(new ModelEvent(ModelEvent.Type.MAZE,
                                maze));
                System.err.println(player.getPosition());
                return player.getPosition().getZ();
        }

        public double getPlayerSpeedX() {
                return player.getSpeed().getX();
        }

        public double getPlayerSpeedY() {
                return player.getSpeed().getY();
        }

        public double getPlayerSpeedZ() {
                return player.getSpeed().getZ();
        }

        public String getPlayerMode() {
                return player.getCurrentMode();
        }

        public void mazeGenerate() {
                maze.clear();
                maze.generate();
                notifyView(new ModelEvent(ModelEvent.Type.MAZE,
                                maze));

        }

        public void setGothit(String character) {
                CharacterModel characterModel = characters.get(character);
                if (characterModel != null) {
                        if (character.startsWith("spider")) {
                                notifyView(new ModelEvent(ModelEvent.Type.CHANGE_MODE,
                                                characterModel.toString(),
                                                Mode.IDLE));
                        } else {
                                notifyView(new ModelEvent(ModelEvent.Type.CHANGE_MODE,
                                                characterModel.toString(),
                                                Mode.GOTHIT));
                        }
                }
        }

        public float getMazeDepth() {
                return maze.getDepth();
        }

        public void setOnSteps(String character, Boolean b) {
                CharacterModel characterModel = characters.get(character);
                if (characterModel != null) {
                        characterModel.setOnSteps(b);
                }
        }

        public void setOnTrapdoor(String character, Boolean b) {
                CharacterModel characterModel = characters.get(character);
                if (characterModel != null) {
                        characterModel.setOnTrapdoor(b);
                }
        }

        public boolean isPlayerOnSteps() {
                return player.getOnSteps();
        }

        public boolean isPlayerOnTrapdoor() {
                return player.getOnTrapdoor();
        }

        public void changeMode(String characterId, Mode mode) {
                CharacterModel c = characters.get(characterId);
                if (c != null) {
                        c.setCurrentMode(mode.toString());
                        notifyView(new ModelEvent(ModelEvent.Type.CHANGE_MODE, characterId, mode));
                }
        }

        private void notifyView(ModelEvent event) {
                if (listener != null)
                        listener.onModelUpdated(event);
        }

        public void changePlayerMode(Mode mode) {
                player.setCurrentMode(mode.toString());
                notifyView(new ModelEvent(ModelEvent.Type.CHANGE_MODE, "crusader", mode));
        }

}
