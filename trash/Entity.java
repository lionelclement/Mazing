package fr.ubordeaux.ao.maze.view;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

class Entity implements Renderable {
    private float x, y, z;
    private final int index;
    private int layer;

    public Entity(float x, float y, float z, int index, int layer) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.index = index;
        this.layer = layer;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public void render(Graphics g, BufferedImage[][] images) {

        int code = index;

        if (code != -1) {
            int indexTable = (code <= 100) ? 0
                    : (code <= 200) ? 1
                            : 2;
            int newCode = index % 100;

            TileMap.getInstance().drawImage(
                    x, y, z,
                    images[indexTable][newCode], g, 0, 0);

        }
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

    @Override
    public int getLayer() {
        // return layer;
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLayer'");
    }

    @Override
    public void render(Graphics g) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }

    @Override
    public Image getImage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getImage'");
    }

}