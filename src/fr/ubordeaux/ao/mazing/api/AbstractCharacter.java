package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

abstract class AbstractCharacter<T extends CharacterMode> implements Character<T> {

    protected BufferedImage[][][] images; // [state][direction][frame]
    protected BufferedImage[][][] shadow_images; // [state][direction][frame]
    protected Animation<? extends CharacterMode>[][] animations; // [state][direction]

    protected float x, y, z;
    protected float targetX, targetY, targetZ;
    protected float speedX, speedY, speedZ;
    protected Direction currentDirection;
    protected int offsetX;
    protected int offsetY;

    private Runnable onTileTrigger;
    private Runnable loopAnimationTrigger;
    private Runnable beginLoopAnimationTrigger;
    private Runnable midLoopAnimationTrigger;
    private Runnable endLoopAnimationTrigger;
    private Runnable tickAnimationTrigger;

    @SuppressWarnings("unchecked")
    public AbstractCharacter(int numberStates, int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        images = new BufferedImage[numberStates][Direction.values().length][];
        shadow_images = new BufferedImage[numberStates][Direction.values().length][];
        animations = (Animation<CharacterMode>[][]) new Animation[numberStates][Direction.values().length];
        currentDirection = Direction.SOUTH;
    }

    public float getSpeedX() {
        return speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public float getSpeedZ() {
        return speedZ;
    }

    @SuppressWarnings("unchecked")
    public Animation<T> getCurrentAnimation(int state) {
        return (Animation<T>) animations[state][currentDirection.ordinal()];
    }

    public void render(Graphics g, int state) {
        Animation<T> anim = getCurrentAnimation(state);
        BufferedImage image = anim.getCurrentFrame();
        TileMap.getInstance().drawImage(x, y, z, image, g, offsetX, offsetY, getRenderType(), 1.0f);
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
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    protected void turnPlayer(Direction targetDirection, int delayMs, Runnable onFinished) {

        Direction currentDirection = getCurrentDirection();
        int currentAngle = currentDirection.getAngle();
        int targetAngle = targetDirection.getAngle();

        int delta = ((targetAngle - currentAngle + 540) % 360) - 180;
        int steps = Math.abs(delta) / 45;
        int stepSign = (delta >= 0) ? 1 : -1;

        if (steps == 0) {
            if (onFinished != null)
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
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getZ() {
        return z;
    }

    public abstract void changeMode(String mode);

    public abstract void changeMode(CharacterMode mode);

    public abstract RenderType getRenderType();

    public abstract void render(Graphics g);

    public abstract int getRank();

    @Override
    public float getIsoDepth() {
        return getX() + getY() + 0.5f;
    }

    protected void callTickTrigger(int frameIndex) {
        x += speedX;
        y += speedY;
        z += speedZ;
        float epsilon = 0.05f; // tolérance pour dire "proche d'un entier"

        if (tickAnimationTrigger != null) {
            tickAnimationTrigger.run();
        }
    }

    public void callBeginTrigger() {
        if (beginLoopAnimationTrigger != null) {
            beginLoopAnimationTrigger.run();
        }
    }

    public void callMidTrigger() {
        if (midLoopAnimationTrigger != null) {
            midLoopAnimationTrigger.run();
        }
    }

    public void callEndTrigger() {
        if (endLoopAnimationTrigger != null) {
            endLoopAnimationTrigger.run();
        }
    }

    public void callLoopTrigger() {
        if (loopAnimationTrigger != null) {
            loopAnimationTrigger.run();
        }
    }

    @Override
    public void setTickAnimationTrigger(Runnable callback) {
        tickAnimationTrigger = callback;
    }

    @Override
    public void setBeginAnimationTrigger(Runnable callback) {
        beginLoopAnimationTrigger = callback;
    }

    @Override
    public void setMidAnimationTrigger(Runnable callback) {
        midLoopAnimationTrigger = callback;
    }

    @Override
    public void setEndAnimationTrigger(Runnable callback) {
        endLoopAnimationTrigger = callback;
    }

    @Override
    public void setLoopAnimationTrigger(Runnable callback) {
        loopAnimationTrigger = callback;
    }

}
