package fr.ubordeaux.ao.mazing.api.view.entity;

import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.TileMap;
import fr.ubordeaux.ao.mazing.api.model.entity.Entity;
import fr.ubordeaux.ao.mazing.api.model.entity.EntityMode;
import fr.ubordeaux.ao.mazing.api.view.IRenderable;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class AbstractEntityRenderer implements EntityRenderer, IRenderable {

    protected final Entity entity;

    protected int offsetX;
    protected int offsetY;

    protected BufferedImage[][][] images; // [state][direction][frame]
    protected BufferedImage[][][] shadow_images; // [state][direction][frame]
    protected Animation[][] animations; // [state][direction]

    private Runnable loopAnimationTrigger;
    private Runnable beginLoopAnimationTrigger;
    private Runnable midLoopAnimationTrigger;
    private Runnable endLoopAnimationTrigger;
    private Runnable tickAnimationTrigger;

    public AbstractEntityRenderer(int numberStates, Entity entity, int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        images = new BufferedImage[numberStates][Direction.values().length][];
        shadow_images = new BufferedImage[numberStates][Direction.values().length][];
        animations = new Animation[numberStates][Direction.values().length];
        this.entity = entity;
        this.loadAnimations();
    }

    /**
     * Charge les animations de cette entité dans l'attribut {@link AbstractEntityRenderer#images}, {@link AbstractEntityRenderer#shadow_images}, et {@link AbstractEntityRenderer#animations}.
     * Ces attributs ont déjà été correctement initialisé avant l'appel de cette méthode.
     */
    protected abstract void loadAnimations();

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public Animation getCurrentAnimation() {
        return animations[entity.getCurrentMode().ordinal()][entity.getCurrentDirection().ordinal()];
    }

    @Override
    public void render(Graphics g) {
        Animation anim = getCurrentAnimation();
        BufferedImage image = anim.getCurrentFrame();
        float x = this.getRenderX();
        float y = this.getRenderY();
        float z = this.getRenderZ();
        TileMap.getInstance().drawImage(x, y, z, image, g, offsetX, offsetY, getRenderType(), 1.0f);
    }

    @Override
    public float getRenderX() {
        return this.entity.getX();
    }

    @Override
    public float getRenderY() {
        return this.entity.getY();
    }

    @Override
    public float getRenderZ() {
        return this.entity.getZ();
    }

    @Override
    public float getIsoDepth() {
        return getRenderX() + getRenderY() + 0.5f;
    }

    @Override
    public void changeMode(EntityMode mode) {
        this.entity.changeMode(mode);
        animations[entity.getCurrentMode().ordinal()][entity.getCurrentDirection().ordinal()].reset();
    }

    @Override
    public void changeMode(String mode) {
        this.entity.changeMode(mode);
    }

    @Override
    public void onTick(int frameIndex) {
        this.entity.onTick(frameIndex);

        if (tickAnimationTrigger != null) {
            tickAnimationTrigger.run();
        }
    }

    public void onBeginAnimation() {
        if (beginLoopAnimationTrigger != null) {
            beginLoopAnimationTrigger.run();
        }
    }

    public void onMiddleAnimation() {
        if (midLoopAnimationTrigger != null) {
            midLoopAnimationTrigger.run();
        }
    }

    public void onEndAnimation() {
        if (endLoopAnimationTrigger != null) {
            endLoopAnimationTrigger.run();
        }
    }

    public void onLoopAnimation() {
        if (loopAnimationTrigger != null) {
            loopAnimationTrigger.run();
        }
    }

    @Override
    public void setTickAnimationCallback(Runnable callback) {
        tickAnimationTrigger = callback;
    }

    @Override
    public void setBeginAnimationCallback(Runnable callback) {
        beginLoopAnimationTrigger = callback;
    }

    @Override
    public void setMidAnimationCallback(Runnable callback) {
        midLoopAnimationTrigger = callback;
    }

    @Override
    public void setEndAnimationCallback(Runnable callback) {
        endLoopAnimationTrigger = callback;
    }

    @Override
    public void setLoopAnimationCallback(Runnable callback) {
        loopAnimationTrigger = callback;
    }

}
