package fr.ubordeaux.ao.mazing.impl.view.entity;

import fr.ubordeaux.ao.mazing.api.Direction;
import fr.ubordeaux.ao.mazing.api.Loader;
import fr.ubordeaux.ao.mazing.api.RenderType;
import fr.ubordeaux.ao.mazing.api.TileMap;
import fr.ubordeaux.ao.mazing.api.model.entity.Entity;
import fr.ubordeaux.ao.mazing.api.view.entity.AbstractEntityRenderer;
import fr.ubordeaux.ao.mazing.api.view.entity.Animation;
import fr.ubordeaux.ao.mazing.impl.model.entity.Spider;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SpiderRenderer extends AbstractEntityRenderer {

    private static final int DELAY = 60;
    private static final float SCALE = 2.5f;
    private static final int OFFSET_X = 0;
    private static final int OFFSET_Y = 0;

    public SpiderRenderer(Entity entity) {
        super(Spider.Mode.values().length, entity, OFFSET_X, OFFSET_Y);
    }

    @Override
    protected void loadAnimations() {
        Spider.Mode[] modes = Spider.Mode.values();
        Direction[] directions = Direction.values();

        for (Spider.Mode mode : modes) {
            int numFrames = mode.getNumFrames();
            for (Direction direction : directions) {
                images[mode.ordinal()][direction.ordinal()] = Loader.loadAnimations(numFrames,
                        (direction.ordinal() + 6) % 8,
                        String.format("/bw_spider/%s/%s-BW_Spider_",
                                mode.getName(),
                                mode.getName()),
                        SCALE);

                shadow_images[mode.ordinal()][direction.ordinal()] = Loader.loadAnimations(numFrames,
                        (direction.ordinal() + 6) % 8,
                        String.format("/bw_spider/%s/_shadow/%s-BW_Spider-shadow_",
                                mode.getName(),
                                mode.getName()),
                        SCALE);

                animations[mode.ordinal()][direction.ordinal()] = new Animation(
                        images[mode.ordinal()][direction.ordinal()],
                        shadow_images[mode.ordinal()][direction.ordinal()],
                        DELAY, mode.isLoop());

            }
        }
    }

    @Override
    public RenderType getRenderType() {
        return RenderType.OBJECT;
    }

    @Override
    public void render(Graphics g) {
        Animation anim = getCurrentAnimation();
        BufferedImage image = anim.getCurrentFrame();
        BufferedImage shadow = anim.getCurrentShadow();
        float x = this.getRenderX();
        float y = this.getRenderY();
        float z = this.getRenderZ();
        TileMap.getInstance().drawImage(x, y, z, shadow, g, offsetX, offsetY, RenderType.OBJECT, 1.0f);
        TileMap.getInstance().drawImage(x, y, z, image, g, offsetX, offsetY, RenderType.OBJECT, 1.0f);

        super.render(g);
    }

    @Override
    public int getRank() {
        return 1;
    }

    @Override
    public void onEndAnimation() {
        if (entity.getCurrentMode() == Spider.Mode.FALL) {
            animations[entity.getCurrentMode().ordinal()][entity.getCurrentDirection().ordinal()].lastFrame();
        }
        super.onEndAnimation();
    }

    @Override
    public float getIsoDepth() {
        return getRenderX() + getRenderY() + 0.6f;
    }

}
