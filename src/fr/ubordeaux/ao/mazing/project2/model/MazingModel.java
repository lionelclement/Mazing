package fr.ubordeaux.ao.mazing.project2.model;

public class MazingModel {

        private float playerSpeedX = 0;
        private float playerSpeedY = 0;
        private float playerSpeedZ = 0;
        private String playerMode = "IDLE";
        private float playerPositionX;
        private float playerPositionY;
        private float playerPositionZ;
        private Maze maze;
        private ModelListener listener;

        public MazingModel(int width, int height, int depth) {
                maze = new Maze(width, height, depth);
        }

        public void setListener(ModelListener listener) {
                this.listener = listener;
        }

        public void setPlayerPosition(float x, float y, float z) {
                float dx = playerPositionX - x;
                float dy = playerPositionY - y;
                float dz = playerPositionZ - z;
                this.playerPositionX = x;
                this.playerPositionY = y;
                this.playerPositionZ = z;
                Point position;
                position = new Point(playerPositionX, playerPositionY, playerPositionZ);
                System.err.println(String.format("MazingMode@setPlayerPosition (%f, %f, %f)", x, y, z));

                if (listener != null) {
                        if (dz * dz > 0) {
                                listener.onModelUpdated(new ModelEvent(ModelEvent.Type.MAZE, maze));
                        }
                        if (checkCollision(position)) {
                                listener.onModelUpdated(new ModelEvent(ModelEvent.Type.PLAYER_MODE, "GOTHIT"));
                                setPlayerSpeed(0, 0, 0);

                        }
                }
        }

        public boolean checkCollision(Point p) {
                // Ici tu testes si (x,y,z) est un mur, ou hors du labyrinthe
                if (p.getX() < 0 || p.getX() >= maze.getWidth()
                                || p.getY() < 0 || p.getY() >= maze.getHeight()
                                || p.getZ() < 0 || p.getZ() >= maze.getDepth()) {
                        return true;
                }
                return false;
                // Cell c = maze.getCell(x, y, z);
                // return c == null || c.isWall(Direction.NORTH) || c.isWall(Direction.SOUTH);
                // etc selon les règles de collision
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
                System.err.println("MazingModel@mazeGenerate()");
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

}
