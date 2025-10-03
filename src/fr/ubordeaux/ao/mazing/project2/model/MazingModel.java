package fr.ubordeaux.ao.mazing.project2.model;

public class MazingModel {

        private float playerSpeedX = 0;
        private float playerSpeedY = 0;
        private float playerSpeedZ = 0;
        private String playerMode = "IDLE";
        private float playerPositionX;
        private float playerPositionY;
        private float playerPositionZ;

        // Interface pour le callback
        public interface ModelListener {
                void onModelUpdated(ModelEvent event);
        }

        private ModelListener listener;

        // Méthode pour enregistrer la vue
        public void setListener(ModelListener listener) {
                this.listener = listener;
        }

        public void setPlayerPosition(float x, float y, float z) {
                this.playerPositionX = x;
                this.playerPositionY = y;
                this.playerPositionZ = z;
                if (listener != null) {
                        Point position;
                        position = new Point(playerPositionX, playerPositionY, playerPositionZ);
                        listener.onModelUpdated(new ModelEvent(ModelEvent.Type.PLAYER_POS, position));
                }

        }

        public void setPlayerSpeed(float playerSpeedX, float playerSpeedY, float playerSpeedZ) {
                this.playerSpeedX = playerSpeedX;
                this.playerSpeedY = playerSpeedY;
                this.playerSpeedZ = playerSpeedZ;

                if (listener != null) {
                        Point speed = new Point(playerSpeedX, playerSpeedY, playerSpeedZ);
                        listener.onModelUpdated(new ModelEvent(ModelEvent.Type.PLAYER_SPEED, speed));
                }
        }

        public float getPlayerPositionX() {
                return playerPositionX;
        }

        public float getPlayerPositionY() {
                return playerPositionY;
        }

        public float getPlayerPositionZ() {
                return playerPositionZ;
        }

        public float getPlayerSpeedX() {
                return playerSpeedX;
        }

        public float getPlayerSpeedY() {
                return playerSpeedY;
        }

        public float getPlayerSpeedZ() {
                return playerSpeedZ;
        }

        public String getPlayerMode() {
                return playerMode;
        }

        public void mazeGenerate() {
                Maze maze = new Maze(3, 3, 3);
                maze.generate();
                if (listener != null) {
                        listener.onModelUpdated(new ModelEvent(ModelEvent.Type.MAZE,
                                        maze));
                }

        }

        public void setPlayerMode(String playerMode) {
                this.playerMode = playerMode;
                if (listener != null) {
                        listener.onModelUpdated(new ModelEvent(ModelEvent.Type.PLAYER_MODE,
                                        playerMode));
                }
        }

}
