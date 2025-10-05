package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Représente un objet 3D à afficher sur le plateau de jeu.
 * Implémente {@link IRenderable3D} pour être trié et rendu correctement selon
 * sa profondeur et son rang.
 */
public class ImageRendarable3D implements IRenderable3D {

    private BufferedImage bufferedImage;
    private float x;
    private float y;
    private float z;
    private RenderType renderType;
    private int rank;
    private int offsetX;
    private int offsetY;
    private float alpha;

    public ImageRendarable3D(BufferedImage bufferedImage, float x, float y, float z, int offsetX, int offsetY,
            int rank,
            RenderType renderType, float alpha) {
        this.bufferedImage = bufferedImage;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.rank = rank;
        this.renderType = renderType;
        this.alpha = alpha;
    }

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

    public int getHeight() {
        return rank;
    }

    @Override
    public RenderType getRenderType() {
        return renderType;
    }

    @Override
    public void render(Graphics g) {
        TileMap.getInstance().drawImage(x, y, z, bufferedImage, g, offsetX, offsetY, renderType, alpha);
    }

    @Override
    public int getRank() {
        return rank;
    }

    @Override
    public float getIsoDepth() {
        return getX() + getY();
    }

}
