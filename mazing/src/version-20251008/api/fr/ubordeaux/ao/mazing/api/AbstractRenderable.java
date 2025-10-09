package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;

abstract class AbstractRenderable {

    abstract float getX();

    abstract float getY();

    abstract float getZ();

    abstract RenderType getRenderType();

    abstract void render(Graphics g);

    abstract int getRank();

    abstract float getIsoDepth();
}
