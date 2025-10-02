package fr.ubordeaux.ao.mazing.project2.model;

public class MazingModel {

        private float playerSpeedX = 0;
        private float playerSpeedY = 0;
        private float playerSpeedZ = 0;
        private String playerMode = "IDLE";

        // Interface pour le callback
        public interface ModelListener {
                void onModelUpdated(ModelEvent event);
        }

        private ModelListener listener;

        // Méthode pour enregistrer la vue
        public void setListener(ModelListener listener) {
                this.listener = listener;
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
                MazeGenerator mg = new MazeGenerator(5, 5);
                Cell[][] maze = mg.generate();

                int width = maze[0].length;
                int height = maze.length;

                int[][][] walls1 = new int[1][height][width];
                int[][][] walls2 = new int[1][height][width];

                // Affichage du labyrinthe
                for (int y = 0; y < height; y++) {
                        // Ligne du haut des cellules
                        for (int x = 0; x < width; x++) {
                                System.out.print(maze[y][x].walls.get(Direction.UP) ? "+---" : "+   ");
                                if (maze[y][x].walls.get(Direction.UP))
                                        walls1[0][y][x] = 110;
                                else
                                        walls1[0][y][x] = -1;

                        }
                        System.out.println("+");

                        // Ligne du corps des cellules
                        for (int x = 0; x < width; x++) {
                                System.out.print(maze[y][x].walls.get(Direction.LEFT) ? "|   " : "    ");
                                if (maze[y][x].walls.get(Direction.LEFT))
                                        walls2[0][y][x] = 108;
                                else
                                        walls2[0][y][x] = -1;

                        }
                        // Mur droit de la dernière cellule
                        System.out.println("|");
                        // walls2[0][y][width - 1] = 111;
                }

                // Ligne du bas de la dernière rangée
                for (int x = 0; x < width; x++) {
                        System.out.print("+---");
                        // walls1[0][height - 1][x] = 110;
                }

                int[][][] walls = new int[][][] {
                                {
                                                { 138, 110, 110, 110, 110, 110, 110, 110, 139 },
                                                { 108, -1, -1, -1, -1, -1, -1, -1, 111 },
                                                { 108, -1, -1, -1, -1, -1, -1, -1, 111 },
                                                { 108, -1, -1, -1, -1, -1, -1, -1, 111 },
                                                { 108, -1, -1, -1, -1, -1, -1, -1, 111 },
                                                { 108, -1, -1, -1, -1, -1, -1, -1, 111 },
                                                { 108, -1, -1, -1, -1, -1, -1, -1, 111 },
                                                { 108, -1, -1, -1, -1, -1, -1, -1, 111 },
                                                { 136, 109, 109, 109, 109, 109, 109, 109, 137 }
                                },
                };

                if (listener != null) {
                        listener.onModelUpdated(new ModelEvent(ModelEvent.Type.WALLS,
                                        walls));
                        // listener.onModelUpdated(new ModelEvent(ModelEvent.Type.WALLS,
                        // walls2));
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
