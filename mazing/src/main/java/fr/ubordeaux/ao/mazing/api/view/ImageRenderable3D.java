package fr.ubordeaux.ao.mazing.api.view;

import fr.ubordeaux.ao.mazing.api.RenderType;
import fr.ubordeaux.ao.mazing.api.TileMap;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Représente un objet 3D à afficher sur le plateau de jeu.
 * Implémente {@link IRenderable} pour être trié et rendu correctement selon
 * sa profondeur et son rang.
 */
public class ImageRenderable3D implements IRenderable {

    private final BufferedImage bufferedImage;
    private final float x;
    private final float y;
    private final float z;
    private final RenderType renderType;
    private final int rank;
    private final int offsetX;
    private final int offsetY;
    private final float alpha;

    public ImageRenderable3D(BufferedImage bufferedImage, float x, float y, float z, int offsetX, int offsetY,
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.bufferedImage);
        hash = 67 * hash + Float.floatToIntBits(this.x);
        hash = 67 * hash + Float.floatToIntBits(this.y);
        hash = 67 * hash + Float.floatToIntBits(this.z);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImageRenderable3D other = (ImageRenderable3D) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        if (Float.floatToIntBits(this.z) != Float.floatToIntBits(other.z)) {
            return false;
        }
        return Objects.equals(this.bufferedImage, other.bufferedImage);
    }

}
