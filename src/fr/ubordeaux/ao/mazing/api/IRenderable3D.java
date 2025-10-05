package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;

interface IRenderable3D {
    float getX();

    float getY();

    float getZ();

    RenderType getRenderType();

    void render(Graphics g);

    int getRank();

    float getIsoDepth();

}
