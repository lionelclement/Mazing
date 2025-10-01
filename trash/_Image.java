package fr.ubordeaux.ao.maze.view;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class _Image implements Renderable {

    private BufferedImage bufferedImage;

    public _Image(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    @Override
    public void render(Graphics g, float x, float y, float z) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'render'");
    }

    @Override
    public void render(Graphics g, int x, int y, int z) {
        Point2D point = TileMap.getInstance().gridToScreen(x, y, z, true);
        g.drawImage(bufferedImage, (int) point.getX() - bufferedImage.getWidth() / 2,
                (int) point.getY() - bufferedImage.getHeight() / 2, null);
    }

}
