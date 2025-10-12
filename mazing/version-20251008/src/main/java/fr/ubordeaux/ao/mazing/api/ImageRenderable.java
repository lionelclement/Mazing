package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;

class ImageRenderable extends AbstractRenderable {

    private final BufferedImage bufferedImage;
    private final float x;
    private final float y;
    private final float z;
    private final RenderType renderType;
    private final int rank;
    private final float alpha;

    public ImageRenderable(BufferedImage bufferedImage, float x, float y, float z,
            int rank,
            RenderType renderType, float alpha) {
        this.bufferedImage = bufferedImage;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rank = rank;
        this.renderType = renderType;
        this.alpha = alpha;
    }

    public ImageRenderable(BufferedImage bufferedImage, int offsetX, int offsetY, RenderType renderType) {
		this(bufferedImage, 0, 0, 0, 0, renderType, 0);
	}

    public int getHeight() {
        return rank;
    }

    public RenderType getRenderType() {
        return renderType;
    }

    void render(Graphics g) {
        TileMap.getInstance().drawImage(x, y, z, bufferedImage, g, renderType, alpha);
    }

    int getRank() {
        return rank;
    }

    float getIsoDepth() {
        return x + y;
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
        final ImageRenderable other = (ImageRenderable) obj;
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

	public BufferedImage getBufferedImage() {
		return bufferedImage;
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

}
