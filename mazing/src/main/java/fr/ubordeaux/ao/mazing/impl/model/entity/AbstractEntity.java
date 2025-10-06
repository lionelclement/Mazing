package fr.ubordeaux.ao.mazing.impl.model.entity;

import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.model.entity.Entity;

/**
 * Classe abstraite représentant un personnage dans le jeu.
 * 
 * Cette classe gère les informations de position, de vitesse,
 * d'animations et de direction du personnage. Elle fournit
 * également des méthodes de rendu et des hooks pour les triggers
 * (début, fin, milieu, tick) lors de l'animation.
 * 
 */
public abstract class AbstractEntity implements Entity {

    protected float x, y, z;
    protected float targetX, targetY, targetZ;
    protected float speedX, speedY, speedZ;
    protected Direction currentDirection;

    public AbstractEntity() {
        currentDirection = Direction.SOUTH;
    }

    @Override
    public Direction getCurrentDirection() {
        return currentDirection;
    }

    @Override
    public void setSpeed(float speedX, float speedY, float speedZ) {
        Direction lastDirection = getCurrentDirection();
        Direction newDirection = getCurrentDirection();
        if (speedX > 0 && speedY == 0)
            newDirection = Direction.EAST;
        else if (speedX < 0 && speedY == 0)
            newDirection = Direction.WEST;
        else if (speedX == 0 && speedY > 0)
            newDirection = Direction.SOUTH;
        else if (speedX == 0 && speedY < 0)
            newDirection = Direction.NORTH;
        else if (speedX > 0 && speedY > 0)
            newDirection = Direction.SOUTHEAST;
        else if (speedX > 0 && speedY < 0)
            newDirection = Direction.NORTHEAST;
        else if (speedX < 0 && speedY > 0)
            newDirection = Direction.SOUTHWEST;
        else if (speedX < 0 && speedY < 0)
            newDirection = Direction.NORTHWEST;

        this.speedX = this.speedY = this.speedZ = 0;

        if (newDirection != lastDirection) {
            // Change la direction et lance le déplacement
            turnPlayer(newDirection, 75, () -> {
                this.speedX = speedX;
                this.speedY = speedY;
                this.speedZ = speedZ;
            });
        } else {
            this.speedX = speedX;
            this.speedY = speedY;
            this.speedZ = speedZ;
        }
    }

    // Déplace le personnage vers une case et ajuste la direction progressivement
    @Override
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public float getZ() {
        return this.z;
    }

    protected void turnPlayer(Direction targetDirection, int delayMs, Runnable onFinished) {

        Direction direction = getCurrentDirection();
        int currentAngle = direction.getAngle();
        int targetAngle = targetDirection.getAngle();

        int delta = ((targetAngle - currentAngle + 540) % 360) - 180;
        int steps = Math.abs(delta) / 45;
        int stepSign = (delta >= 0) ? 1 : -1;

        if (steps == 0) {
            if (null != onFinished)
                onFinished.run();
            return;
        }

        javax.swing.Timer rotationTimer = new javax.swing.Timer(delayMs, null);
        final int[] stepCount = { 0 };

        rotationTimer.addActionListener(_ -> {
            int newAngle = (currentAngle + stepSign * 45 * stepCount[0] + 360) % 360;

            // Choisir la direction la plus proche
            Direction nearest = Direction.values()[0];
            int minDiff = 360;
            for (Direction dir : Direction.values()) {
                int diff = Math.abs(((dir.getAngle() - newAngle + 180) % 360) - 180);
                if (diff < minDiff) {
                    minDiff = diff;
                    nearest = dir;
                }
            }
            this.currentDirection = nearest;

            stepCount[0]++;
            if (stepCount[0] > steps) {
                changeDirection(targetDirection);
                rotationTimer.stop();
                if (onFinished != null)
                    onFinished.run();
            }
        });

        rotationTimer.setRepeats(true);
        rotationTimer.start();
    }

    @Override
    public void setDirection(Direction newDirection) {
        // Calculer la vitesse scalaire actuelle dans le plan XY
        float currentSpeedXY = (float) Math.sqrt(speedX * speedX + speedY * speedY);
        float currentSpeedZ = speedZ; // on conserve Z

        // On met à zéro les vitesses pendant la rotation visuelle
        speedX = speedY = speedZ = 0;

        turnPlayer(newDirection, 125, () -> {
            currentDirection = newDirection;

            // Vecteur direction brute pour X et Y
            float dirX = 0, dirY = 0;
            switch (newDirection) {
                case NORTH -> {
                    dirX = 0;
                    dirY = -1;
                }
                case SOUTH -> {
                    dirX = 0;
                    dirY = 1;
                }
                case EAST -> {
                    dirX = 1;
                    dirY = 0;
                }
                case WEST -> {
                    dirX = -1;
                    dirY = 0;
                }
                case NORTHEAST -> {
                    dirX = 1;
                    dirY = -1;
                }
                case NORTHWEST -> {
                    dirX = -1;
                    dirY = -1;
                }
                case SOUTHEAST -> {
                    dirX = 1;
                    dirY = 1;
                }
                case SOUTHWEST -> {
                    dirX = -1;
                    dirY = 1;
                }
            }

            // Normaliser automatiquement (aucun if nécessaire)
            float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
            if (length > 0) {
                dirX /= length;
                dirY /= length;
            }

            // Appliquer la vitesse existante dans la nouvelle direction XY
            speedX = dirX * currentSpeedXY;
            speedY = dirY * currentSpeedXY;
            speedZ = currentSpeedZ; // conserver la vitesse sur Z
        });
    }

    @Override
    public abstract void stop();


    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void setZ(float z) {
        this.z = z;
    }

    public void onTick(int frameIndex) {
        x += speedX;
        y += speedY;
        z += speedZ;
    }

    @Override
    public float getSpeedX() {
        return speedX;
    }

    @Override
    public float getSpeedY() {
        return speedY;
    }

    @Override
    public float getSpeedZ() {
        return speedZ;
    }

}