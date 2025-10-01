package fr.ubordeaux.ao.maze.view;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class ImageRenderable implements Renderable {

    private final DiscretePoint3D discretePoint3D;
    private final Image image;

    public ImageRenderable(DiscretePoint3D discretePoint3D, Image image) {
        this.discretePoint3D = discretePoint3D;
        this.image = image;
    }

    @Override
    public float getX() {
        return discretePoint3D.getX();
    }

    @Override
    public float getY() {
        return discretePoint3D.getY();
    }

    @Override
    public float getZ() {
        return discretePoint3D.getZ();
    }

    @Override
    public int getIndex() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getIndex'");
    }

    @Override
    public void render(Graphics g) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }

    @Override
    public int getLayer() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLayer'");
    }

    @Override
    public Image getImage() {
        return image;
    }

    // @Override
    // public void render(Graphics g, BufferedImage[][] images) {
    // // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'render'");
    // }

}
