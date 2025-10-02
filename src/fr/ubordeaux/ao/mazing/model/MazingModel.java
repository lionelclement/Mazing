package fr.ubordeaux.ao.mazing.model;

public class MazingModel {

        private float playerSpeedX = 0;
        private float playerSpeedY = 0;
        private float playerSpeedZ = 0;

        public void setPlayerSpeed(float playerSpeedX, float playerSpeedY, float playerSpeedZ) {
                this.playerSpeedX = playerSpeedX;
                this.playerSpeedY = playerSpeedY;
                this.playerSpeedZ = playerSpeedZ;
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

}
