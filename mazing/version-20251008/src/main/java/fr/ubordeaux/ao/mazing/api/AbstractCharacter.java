package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Classe abstraite représentant un personnage dans le jeu.
 * 
 * Cette classe gère les informations de position, de vitesse,
 * d'animations et de direction du personnage. Elle fournit
 * également des méthodes de rendu et des hooks pour les triggers
 * (début, fin, milieu, tick) lors de l'animation.
 * 
 *
 * @param <T> le type d'état du personnage, qui doit implémenter
 *            {@link CharacterMode}
 */
 abstract class AbstractCharacter<T extends CharacterMode> extends AbstractRenderable implements ICharacter<T> {

    protected BufferedImage[][][] images; // [mode][direction][frame]
    protected BufferedImage[][][] shadow_images; // [mode][direction][frame]
    protected Animation<T>[][] animations; // [mode][direction]

    protected float x, y, z;
    protected Direction currentDirection;
    protected float scale;
    protected UUID id;

    private Consumer<ICharacter<?>> loopAnimationTrigger;
    private Consumer<ICharacter<?>> beginLoopAnimationTrigger;
    private Consumer<ICharacter<?>> midLoopAnimationTrigger;
    private Consumer<ICharacter<?>> endLoopAnimationTrigger;
    private Consumer<ICharacter<?>> tickAnimationTrigger;

    @SuppressWarnings("unchecked")
    AbstractCharacter(UUID id, int numberStates) {
        this.id = id;
        images = new BufferedImage[numberStates][Direction.values().length][];
        shadow_images = new BufferedImage[numberStates][Direction.values().length][];
        animations = new Animation[numberStates][Direction.values().length];
        currentDirection = Direction.SOUTH;
    }

    Animation<T> getCurrentAnimation(int mode) {
        return animations[mode][currentDirection.ordinal()];
    }

    void render(Graphics g, int mode) {
        Animation<T> anim = getCurrentAnimation(mode);
        BufferedImage image = anim.getCurrentFrame();
        TileMap.getInstance().drawImage(x, y, z, image, g, getRenderType(), 1.0f);
    }

    abstract RenderType getRenderType();

    // Déplace le personnage vers une case et ajuste la direction progressivement
    protected void setPosition(float x, float y, float z, float offsetX, float offsetY) {
        this.x = x + offsetX;
        this.y = y + offsetY;
        this.z = z;
    }

    // Déplace le personnage vers une case et ajuste la direction progressivement
    abstract public void setPosition(float x, float y, float z);
    
    void turnPlayer(Direction targetDirection, int delayMs, Runnable onFinished) {

        Direction direction = currentDirection;
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

        rotationTimer.addActionListener(_-> {
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
                setDirection(targetDirection);
                rotationTimer.stop();
                if (onFinished != null)
                    onFinished.run();
            }
        });

        rotationTimer.setRepeats(true);
        rotationTimer.start();
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    float getIsoDepth() {
        return x + y + 0.5f;
    }

    void callTickTrigger(float frameIndex) {
        if (tickAnimationTrigger != null) {
            tickAnimationTrigger.accept(this);
        }
    }

    void callBeginTrigger() {
        if (beginLoopAnimationTrigger != null) {
            beginLoopAnimationTrigger.accept(this);
        }
    }

    void callMidTrigger() {
        if (midLoopAnimationTrigger != null) {
            midLoopAnimationTrigger.accept(this);
        }
    }

    void callEndTrigger() {
        if (endLoopAnimationTrigger != null) {
            endLoopAnimationTrigger.accept(this);
        }
    }

    void callLoopTrigger() {
        if (loopAnimationTrigger != null) {
            loopAnimationTrigger.accept(this);
        }
    }

    @Override
    public void setTickAnimationTrigger(Consumer<ICharacter<?>> callback) {
        tickAnimationTrigger = callback;
    }

    @Override
    public void setBeginAnimationTrigger(Consumer<ICharacter<?>> callback) {
        beginLoopAnimationTrigger = callback;
    }

    @Override
    public void setMidAnimationTrigger(Consumer<ICharacter<?>> callback) {
        midLoopAnimationTrigger = callback;
    }

    @Override
    public void setEndAnimationTrigger(Consumer<ICharacter<?>> callback) {
        endLoopAnimationTrigger = callback;
    }

    @Override
    public void setLoopAnimationTrigger(Consumer<ICharacter<?>> callback) {
        loopAnimationTrigger = callback;
    }

	abstract CharacterMode getCurrentMode();

    @Override
    public void setScale(float scale) {
        this.scale = scale;
        initAnimations(scale);
    }

    @Override
    protected float getX() {
        return x;
    }

    @Override
    protected float getY() {
        return y;
    }

    @Override
    protected float getZ() {
        return z;
    }


    protected abstract void initAnimations(float scale);

    public UUID getId(){
        return id;
    }

        @Override
    public void setSpeed(T mode, float speed) {
        
        for (Direction d : Direction.values()){
            animations[mode.ordinal()][d.ordinal()].setSpeed(speed);
        }
    }

    @Override
public void setDirection(Direction newDirection) {
    turnPlayer(newDirection, 125, () -> {
        currentDirection = newDirection;

        // Calculer la direction unitaire pour X, Y et Z selon la direction
        float dirX = 0, dirY = 0;

        switch (newDirection) {
            case NORTH -> { dirX = 0; dirY = -1; }
            case SOUTH -> { dirX = 0; dirY = 1; }
            case EAST ->  { dirX = 1; dirY = 0; }
            case WEST ->  { dirX = -1; dirY = 0; }

            case NORTHEAST -> { dirX = 1; dirY = -1;  }
            case NORTHWEST -> { dirX = -1; dirY = -1; }
            case SOUTHEAST -> { dirX = 1; dirY = 1;  }
            case SOUTHWEST -> { dirX = -1; dirY = 1;  }

        }

        // Normaliser si diagonale dans XY
        if (dirX != 0 && dirY != 0) {
            float invSqrt2 = 1 / (float) Math.sqrt(2);
            dirX *= invSqrt2;
            dirY *= invSqrt2;
        }

    });
}


}