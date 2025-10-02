package fr.ubordeaux.ao.mazing.view.api;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

abstract class AbstractCharacter<T extends CharacterMode> implements Character<T> {

    protected BufferedImage[][][] images; // [state][direction][frame]
    protected BufferedImage[][][] shadow_images; // [state][direction][frame]
    protected Animation<? extends CharacterMode>[][] animations; // [state][direction]

    protected float x, y, z; // case actuelle
    protected float speedX, speedY, speedZ;
    protected Direction currentDirection;
    protected int offsetX;
    protected int offsetY;

    private Runnable onTileTrigger;
    private Runnable loopTrigger;
    private Runnable beginLoopTrigger;
    private Runnable midLoopTrigger;
    private Runnable endLoopTrigger;

    @SuppressWarnings("unchecked")
    public AbstractCharacter(int numberStates, int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        images = new BufferedImage[numberStates][Direction.values().length][];
        shadow_images = new BufferedImage[numberStates][Direction.values().length][];
        animations = (Animation<CharacterMode>[][]) new Animation[numberStates][Direction.values().length];
        currentDirection = Direction.SOUTH;
    }

    @SuppressWarnings("unchecked")
    public Animation<T> getCurrentAnimation(int state) {
        return (Animation<T>) animations[state][currentDirection.ordinal()];
    }

    public void render(Graphics g, int state) {
        Animation<T> anim = getCurrentAnimation(state);
        BufferedImage image = anim.getCurrentFrame();
        TileMap.getInstance().drawImage(x, y, z, image, g, offsetX, offsetY, getRenderType());
    }

    @Override
    public Direction getCurrentDirection() {
        return currentDirection;
    }

    protected void tick(int frameIndex) {
        x += speedX;
        y += speedY;
        z += speedZ;
        float epsilon = 0.05f; // tolérance pour dire "proche d'un entier"

        boolean onTileX = Math.abs(x - Math.round(x)) < epsilon;
        boolean onTileY = Math.abs(y - Math.round(y)) < epsilon;
        boolean onTileZ = Math.abs(z - Math.round(z)) < epsilon;

        if (onTileX && onTileY && onTileZ) {
            if (onTileTrigger != null)
                onTileTrigger.run();
        }
    }

    @Override
    public void setSpeed(float speedX, float speedY, float speedZ) {
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

        // Change la direction et lance le déplacement
        turnPlayer(newDirection, 75, () -> {
            this.speedX = speedX;
            this.speedY = speedY;
            this.speedZ = speedZ;
        });

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

    public abstract void changeMode(String state);

    public abstract void changeMode(CharacterMode state);

    public abstract RenderType getRenderType();

    public abstract void render(Graphics g);

    public abstract int getRank();

    @Override
    public float getIsoDepth() {
        return getX() + getY() + 0.5f;
    }

    public void callTikTrigger() {
        if (endLoopTrigger != null) {
            endLoopTrigger.run();
        }
    }

    public void callBeginLoopTrigger() {
        if (beginLoopTrigger != null) {
            beginLoopTrigger.run();
        }
    }

    public void callMidLoopTrigger() {
        if (midLoopTrigger != null) {
            midLoopTrigger.run();
        }
    }

    public void callEndLoopTrigger() {
        if (endLoopTrigger != null) {
            endLoopTrigger.run();
        }
    }

    public void callLoopTrigger() {
        if (loopTrigger != null) {
            loopTrigger.run();
        }
    }

    public void callOnTileTrigger() {
        if (onTileTrigger != null) {
            onTileTrigger.run();
        }
    }

    @Override
    public void setLoopTrigger(Runnable callback) {
        loopTrigger = callback;
    }

    @Override
    public void setBeginLoopTrigger(Runnable callback) {
        endLoopTrigger = callback;
    }

    @Override
    public void setMidLoopTrigger(Runnable callback) {
        midLoopTrigger = callback;
    }

    @Override
    public void setEndLoopTrigger(Runnable callback) {
        endLoopTrigger = callback;
    }

    @Override
    public void setOnTileTrigger(Runnable callback) {
        onTileTrigger = callback;
    }
}
