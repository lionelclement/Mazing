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
                playerPositionX = x;
                playerPositionY = y;
                playerPositionZ = z;
                System.err.println(String.format("MazingMode@setPlayerPosition (%f, %f, %f)",
                                x, y, z));

                if (listener != null) {
                        Point position;
                        position = new Point(playerPositionX, playerPositionY, playerPositionZ);
                        if (dz * dz > 0) {
                                listener.onModelUpdated(new ModelEvent(ModelEvent.Type.PLAYER_POS, position));
                                listener.onModelUpdated(new ModelEvent(ModelEvent.Type.MAZE, maze));

                        }
                        if (checkCollision(position)) {
                                setPlayerSpeed(0, 0, 0);

                        }
                }

        }

        public boolean checkCollision(Point p) {
                float oldX = (float) p.getX();
                float oldY = (float) p.getY();

                // clamp la position dans les bornes du labyrinthe
                float clampedX = Math.max(0, Math.min(oldX, maze.getWidth() - 0.4f));
                float clampedY = Math.max(0, Math.min(oldY, maze.getHeight() - 0.4f));

                // si on a dû corriger, il y a collision
                boolean collided = (clampedX != oldX) || (clampedY != oldY);

                // mettre à jour la position du joueur si nécessaire
                playerPositionX = clampedX;
                playerPositionY = clampedY;

                return collided;
        }

        // Cell c = maze.getCell(x, y, z);
        // return c == null || c.isWall(Direction.NORTH) || c.isWall(Direction.SOUTH);
        // etc selon les règles de collision

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
