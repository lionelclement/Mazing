package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;

enum RenderType {
    FLOOR, OBJECT
}

interface IRenderable3D {
    float getX();

    float getY();

    float getZ();

    RenderType getRenderType();

    void render(Graphics g);

    int getRank();

    float getIsoDepth();

}
