package fr.ubordeaux.ao.mazing.api;

import java.awt.image.BufferedImage;

/**
 * Représente une image du jeu avec ses informations de rendu.
 * Contient un {@link BufferedImage}, des offsets et un {@link RenderType} pour
 * la gestion de l'affichage.
 */
public class Image {

    private BufferedImage bufferedImage;
    private int offsetX;
    private int offsetY;
    private RenderType renderType;

    public Image(BufferedImage scaled, int offsetX, int offsetY, RenderType renderType) {
        this.bufferedImage = scaled;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.renderType = renderType;
    }

    public BufferedImage getBufferedImage() {
        return this.bufferedImage;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public RenderType getRenderType() {
        return renderType;
    }

}
