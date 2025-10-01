package fr.ubordeaux.ao.mazing.model;

public class MazingModel {

        private float playerSpeedX = 0;
        private float playerSpeedY = 0;
        private float playerSpeedZ = 0;
        private String mode;

        public void setPlayerMode(String mode) {
                this.mode = mode;
        }

        public void setPlayerSpeed(float x, float y, float z) {
                this.playerSpeedX = x;
                this.playerSpeedY = y;
                this.playerSpeedZ = z;
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

        public String getMode() {
                return mode;
        }

}
