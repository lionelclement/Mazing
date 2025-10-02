package fr.ubordeaux.ao.mazing.project3.model;

public class MazingModel {

        private Point actualPosition = new Point(0, 0, 0);
        private Point targetPosition = new Point(0, 0, 0);
        private Point speed = new Point(0, 0, 0);

        private static final int[][][] GROUNDS = new int[][][] {
                        {
                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                                        { 42, 42, 42, 42, 42, 42, 42, 42, 42 },
                        },

        };

        private static final int[][][] WALLS = new int[][][] {
                        {
                                        { 180, 102, 102, 102, 102, 102, 102, 102, 182 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 203 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 303 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 403 },
                                        { 181, 101, 101, 101, 101, 101, 101, 101, 183 }
                        },
                        {
                                        { 180, 102, 102, 102, 102, 102, 102, 102, 182 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 203 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 303 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 403 },
                                        { 181, 101, 101, 101, 101, 101, 101, 101, 183 }
                        },
                        {
                                        { 180, 102, 102, 102, 102, 102, 102, 102, 182 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 103 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 203 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 303 },
                                        { 108, -1, -1, -1, -1, -1, -1, -1, 403 },
                                        { 181, 101, 101, 101, 101, 101, 101, 101, 183 }
                        }
        };

        public void translate(double x, double y, double z) {
                targetPosition.translate(x, y, z);
        }

        public void interpolatePosition() {
                // Paramètre d'interpolation (entre 0 et 1, plus proche de 1 = plus rapide)
                double interpolationFactor = 0.1;
                // Calcul de la différence entre la cible et l'actuelle
                double dx = targetPosition.getX() - actualPosition.getX();
                double dy = targetPosition.getY() - actualPosition.getY();
                double dz = targetPosition.getZ() - actualPosition.getZ();

                // On interpole la vitesse (proportionnelle à la différence)
                speed = new Point(dx * interpolationFactor,
                                dy * interpolationFactor,
                                dz * interpolationFactor);
        }
}
