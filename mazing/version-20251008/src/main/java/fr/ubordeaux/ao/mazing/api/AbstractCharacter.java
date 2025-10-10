package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;
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

    protected BufferedImage[][][] images; // [state][direction][frame]
    protected BufferedImage[][][] shadow_images; // [state][direction][frame]
    protected Animation<T>[][] animations; // [state][direction]

    protected float x, y, z;
    protected Direction currentDirection;
    protected int offsetX;
    protected int offsetY;
    protected float scale;
    protected UUID id;

    private Consumer<ICharacter<?>> loopAnimationTrigger;
    private Consumer<ICharacter<?>> beginLoopAnimationTrigger;
    private Consumer<ICharacter<?>> midLoopAnimationTrigger;
    private Consumer<ICharacter<?>> endLoopAnimationTrigger;
    private Consumer<ICharacter<?>> tickAnimationTrigger;

    @SuppressWarnings("unchecked")
    AbstractCharacter(int numberStates, int offsetX, int offsetY) {
        id = UUID.randomUUID();
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        images = new BufferedImage[numberStates][Direction.values().length][];
        shadow_images = new BufferedImage[numberStates][Direction.values().length][];
        animations = new Animation[numberStates][Direction.values().length];
        currentDirection = Direction.SOUTH;
    }

    Animation<T> getCurrentAnimation(int state) {
        return animations[state][currentDirection.ordinal()];
    }

    void render(Graphics g, int state) {
        Animation<T> anim = getCurrentAnimation(state);
        BufferedImage image = anim.getCurrentFrame();
        TileMap.getInstance().drawImage(x, y, z, image, g, offsetX, offsetY, getRenderType(), 1.0f);
    }

    abstract RenderType getRenderType();

    // Déplace le personnage vers une case et ajuste la direction progressivement
    @Override
    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

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

    @Override
    public void setDirection(Direction newDirection) {
        turnPlayer(newDirection, 125, () -> {
            currentDirection = newDirection;
        });
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

    void callTickTrigger(int frameIndex) {
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
    public void setOffset(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        initAnimations(this.scale);
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

}