package fr.ubordeaux.ao.mazing.view.swing;

import fr.ubordeaux.ao.mazing.view.Renderable;
import fr.ubordeaux.ao.mazing.view.renderer.IsometricRenderer;
import fr.ubordeaux.ao.mazing.view.renderer.Renderer;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class ViewPanel extends JPanel {
	private int originX;
	private int originY;
	private final Renderer renderer;
	private final List<Renderable> renderables;
	private int animationTick;

	public ViewPanel(int width, int height, Renderer renderer) {
		this.originX = 0;
		this.originY = height / 2;
		this.renderer = renderer;
		this.renderables = new ArrayList<>();
		this.animationTick = 0;
	}

	public ViewPanel(int width, int height) {
		this.originX = 0;
		this.originY = height / 2;
		this.renderer = new IsometricRenderer(this.originX, this.originY, 256, 128, 60);
		this.renderables = new ArrayList<>();
		this.animationTick = 0;
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		this.originY = height / 2;
		renderer.setOrigin(this.originX, this.originY);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		for (Renderable renderable : this.renderables.stream().sorted().toList()) {
			renderer.renderInIsoGrid(g2d, renderable, this.animationTick);
		}
	}

	public boolean addRenderables(List<Renderable> renderables) {
		return this.renderables.addAll(renderables);
	}
	public boolean addRenderable(Renderable renderable) {
		return this.renderables.add(renderable);
	}
	public boolean removeRenderable(Renderable renderable) {
		return this.renderables.remove(renderable);
	}
	public Renderable removeRenderable(int i) {
		return this.renderables.remove(i);
	}
	public void clearRenderables() {
		this.renderables.clear();
	}
	public Renderable getRenderable(int i) {
		return this.renderables.get(i);
	}

	public void animationTick() {
		this.animationTick++;
	}
}
