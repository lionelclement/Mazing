package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics;

abstract class AbstractRenderable {

    abstract RenderType getRenderType();

    abstract void render(Graphics g);

    abstract int getRank();

    abstract float getIsoDepth();

    protected abstract float getX();

    protected abstract float getY();

    protected abstract float getZ();

}
