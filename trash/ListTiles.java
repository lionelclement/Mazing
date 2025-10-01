package fr.ubordeaux.ao.maze.view;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class ListTiles {
    private List<Entity> tiles = new ArrayList<>();
    private BufferedImage[][] images;

    public ListTiles(BufferedImage[][] images) {
        this.images = images;
    }

    public void addTile(Entity t) {
        tiles.add(t);
    }

    protected void paintComponent(Graphics g) {

        // Tri isométrique : d’abord z, puis x+y
        tiles.sort(Comparator
                .comparing(Entity::getZ)
                .thenComparing(t -> t.getX() + t.getY()));

        for (Entity t : tiles) {
            t.render(g, images);
        }
    }
}
