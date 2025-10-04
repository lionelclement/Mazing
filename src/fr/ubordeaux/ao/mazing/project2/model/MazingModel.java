package fr.ubordeaux.ao.mazing.project2.model;

public class MazingModel {

        private float playerSpeedX = 0;
        private float playerSpeedY = 0;
        private float playerSpeedZ = 0;
        private String playerMode = "IDLE";
        private float playerPositionZ;
        private Maze maze;
        private ModelListener listener;
        private boolean onSteps;
        private boolean onTrapdoor;

        public MazingModel(int width, int height, int depth) {
                maze = new Maze(width, height, depth);
                onSteps = false;
        }

        public boolean isOnSteps() {
                return onSteps;
        }

        public boolean isOnTrapdoor() {
                return onTrapdoor;
        }

        public void setListener(ModelListener listener) {
                this.listener = listener;
        }

        public void setPlayerPositionZ(float z) {
                float dz = playerPositionZ - z;
                playerPositionZ = z;

                if (listener != null) {
                        if (dz * dz > 0) {
                                listener.onModelUpdated(new ModelEvent(ModelEvent.Type.PLAYER_POSITIONZ, z));
                                listener.onModelUpdated(new ModelEvent(ModelEvent.Type.MAZE, maze));
                        }
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

        public float getPlayerPositionZ() {
                listener.onModelUpdated(new ModelEvent(ModelEvent.Type.MAZE,
                                maze));
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
                maze.clear();
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

        public void gothit() {
                setPlayerMode("GOTHIT");
                setPlayerSpeed(0, 0, 0);

        }

        public float getMazeDepth() {
                return maze.getDepth();
        }

        public void setOnSteps(boolean b) {
                onSteps = b;
        }

        public void setOnTrapdoor(boolean b) {
                onTrapdoor = b;
        }
}
