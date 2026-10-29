package fr.ubordeaux.ao.mazing.view.swing;

import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.debug.DebugCube;
import fr.ubordeaux.ao.mazing.view.Renderable;
import fr.ubordeaux.ao.mazing.view.entity.AnimatedEntity;
import fr.ubordeaux.ao.mazing.view.entity.FloorTile;
import fr.ubordeaux.ao.mazing.view.entity.FreeEntity;
import fr.ubordeaux.ao.mazing.view.entity.OccludingEntity;
import fr.ubordeaux.ao.mazing.view.sprite.IsometricSprites;
import fr.ubordeaux.ao.mazing.view.renderer.IsometricRenderer;
import fr.ubordeaux.ao.mazing.view.renderer.IsometricOcclusion;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowGeometry;
import fr.ubordeaux.ao.mazing.view.renderer.Renderer;
import fr.ubordeaux.ao.mazing.view.renderer.StaticShadows;
import fr.ubordeaux.ao.mazing.view.shadow.DirectionalLight;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.IdentityHashMap;
import java.util.Map;

public class ViewPanel extends JPanel {
	private int originX;
	private int originY;
	private final Renderer renderer;
	private final boolean ownsRenderer;
	private final List<Renderable> renderables;
	private final List<DebugCube> debugCubes = new ArrayList<>();
	// The public scene retains one logical corner. Its two wall faces are
	// stable renderables so the occlusion and static-shadow caches can reuse them.
	private final Map<OccludingEntity, List<OccludingEntity>> cornerFaces =
			new IdentityHashMap<>();
	private int animationTick;
	private final StaticShadows staticShadows = new StaticShadows();
	private final IsometricOcclusion occlusion = new IsometricOcclusion();
	private boolean staticShadowsEnabled;

	public ViewPanel(int width, int height, Renderer renderer) {
		this.originX = 0;
		this.originY = height / 2;
		this.renderer = renderer;
		this.ownsRenderer = false;
		this.renderables = new ArrayList<>();
		this.animationTick = 0;
	}

	public ViewPanel(int width, int height) {
		this.originX = 0;
		this.originY = height / 2;
		this.renderer = new IsometricRenderer(this.originX, this.originY);
		this.ownsRenderer = true;
		this.renderables = new ArrayList<>();
		this.animationTick = 0;
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		// Preserve the camera offset when the panel uses an external renderer.
		if (ownsRenderer) {
			this.originY = height / 2;
			renderer.setOrigin(this.originX, this.originY);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics = (Graphics2D) g;
		List<Renderable> ordered = expandedRenderables().stream().sorted().toList();

		if (!(renderer instanceof IsometricRenderer isoRenderer)) {
			for (Renderable renderable : ordered) {
				renderer.renderInIsoGrid(graphics, renderable, animationTick);
			}
			return;
		}

		// Never move a fixed structure to the foreground based on an actor's
		// position. In particular, walls always keep the same mutual order.
		List<AnimatedEntity> actors = new ArrayList<>();
		for (Renderable renderable : ordered) {
			if (renderable instanceof AnimatedEntity actor) {
				actors.add(actor);
			} else if (staticShadowsEnabled && renderable instanceof FloorTile floor) {
				staticShadows.paintFloor(graphics, isoRenderer, floor, ordered, animationTick);
			} else {
				renderer.renderInIsoGrid(graphics, renderable, animationTick);
			}
		}

		// Draw moving actors over the static scene, but remove the pixels
		// actually hidden by foreground structures. The original wall pixels
		// stay intact, including the overlap between adjacent walls.
		for (AnimatedEntity actor : actors) {
			renderActorWithOcclusion(graphics, isoRenderer, actor, ordered);
		}

		// Debug wireframes are overlays, not scene objects or shadow casters.
		for (DebugCube cube : debugCubes) {
			cube.paint(graphics, isoRenderer);
		}
	}

	/**
	 * Resolves registered wall corners into independent faces without changing
	 * the logical scene or the user's add/remove/getRenderables semantics.
	 */
	private List<Renderable> expandedRenderables() {
		List<Renderable> expanded = new ArrayList<>(renderables.size());
		for (Renderable renderable : renderables) {
			if (!(renderable instanceof OccludingEntity corner)) {
				expanded.add(renderable);
				continue;
			}
			var faces = IsometricSprites.cornerFaces(
					corner.getSprite(), corner.getOrientation());
			if (faces.isEmpty()) {
				expanded.add(corner);
				continue;
			}
			List<OccludingEntity> parts = cornerFaces.get(corner);
			if (parts == null || parts.size() != faces.size()
					|| parts.get(0).getSprite() != faces.get(0).sprite()
					|| parts.get(0).getOrientation() != faces.get(0).orientation()
					|| parts.get(1).getOrientation() != faces.get(1).orientation()) {
				parts = new ArrayList<>(faces.size());
				for (var face : faces) {
					parts.add(new OccludingEntity(face.sprite(), face.orientation(),
							corner.getPreciseX(), corner.getPreciseY(),
							corner.getPreciseZ(), corner.getScale()));
				}
				cornerFaces.put(corner, parts);
			}
			for (OccludingEntity part : parts) {
				part.setPrecisePosition(corner.getPreciseX(), corner.getPreciseY());
				part.setPreciseZ(corner.getPreciseZ());
				part.setScale(corner.getScale());
				part.setRenderOffset(corner.getRenderOffsetX(), corner.getRenderOffsetY());
				expanded.add(part);
			}
		}
		return expanded;
	}

	/** Draws an actor without changing the order of any fixed sprites. */
	private void renderActorWithOcclusion(Graphics2D graphics,
			IsometricRenderer isoRenderer, AnimatedEntity actor,
			List<Renderable> ordered) {
		Point actorPoint = isoRenderer.project(actor);
		int zoom = isoRenderer.getZoom();
		Rectangle bounds = actor.getScreenBounds(actorPoint.x(), actorPoint.y(), zoom);
		if (bounds == null) {
			isoRenderer.renderInIsoGrid(graphics, actor, animationTick);
			return;
		}

		Rectangle visible = new Rectangle(0, 0, getWidth(), getHeight());
		Rectangle clip = graphics.getClipBounds();
		if (clip != null) {
			visible = visible.intersection(clip);
		}
		Rectangle region = bounds.intersection(visible);
		if (region.isEmpty()) {
			actor.updateAnimation(animationTick);
			return;
		}

        // Depth-test real static PNG alpha against world-space geometry,
        // rather than guessing from the tile center's X-Y ordering.
        BufferedImage actorImage = new BufferedImage(
                region.width, region.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D layer = actorImage.createGraphics();
        try {
            layer.translate(-region.x, -region.y);
            actor.renderInScreen(layer, actorPoint.x(), actorPoint.y(),
                    animationTick, zoom);
        } finally {
            layer.dispose();
        }

        // The animated sprite is a vertical billboard. Its depth is given by
        // the world-space contact point of its feet.
        double actorDepth = actor.getPreciseX() - actor.getPreciseY();

        // Traverse the static scene in REVERSE painter order. A foreground
        // sprite covers pixels of an earlier sprite, even if that earlier
        // sprite's PNG is opaque at those pixels. Only a genuinely visible
        // portion of a wall or railing may erase the actor.
        byte[] laterStaticCoverage = new byte[region.width * region.height];
        for (int i = ordered.size() - 1; i >= 0; i--) {
            Renderable candidate = ordered.get(i);
            if (candidate instanceof AnimatedEntity) {
                continue;
            }
            Rectangle candidateBounds = occlusion.visibleBounds(
                    candidate, isoRenderer, animationTick);
            if (candidateBounds == null || !candidateBounds.intersects(region)) {
                continue;
            }

            ShadowGeometry geometry = occlusion.geometryFor(candidate,
                    isoRenderer, animationTick);
            boolean physicallyInFront = geometry != null
                    && candidate.getPreciseZ() + geometry.height()
                            > actor.getPreciseZ() + 1e-6
                    && !(candidate instanceof FloorTile
                            && candidate.getPreciseZ()
                                    <= actor.getPreciseZ() + 1e-6)
                    && (geometry.isThinBarrier()
                            || occlusion.mayBeInFront(
                                    geometry, candidate, actorDepth));

            // A sprite can conceal an earlier wall even when it does not
            // conceal the actor. Keep its alpha in laterStaticCoverage.
            occlusion.subtractVisibleOccluder(actorImage, region, candidate,
                    candidateBounds, physicallyInFront ? geometry : null,
                    laterStaticCoverage, actorDepth,
                    actor.getPreciseX(), actor.getPreciseY(),
                    isoRenderer, animationTick);
        }
        graphics.drawImage(actorImage, region.x, region.y, null);
    }

	/** Enables cached directional shadows on floor sprites only. */
	public void setStaticShadowsEnabled(boolean enabled) {
		staticShadowsEnabled = enabled;
		staticShadows.invalidate();
		occlusion.invalidate();
		repaint();
	}

	/** Configures the direction and opacity of the cached static shadows. */
	public void setStaticShadowLight(DirectionalLight light) {
		staticShadows.setLight(light);
		repaint();
	}

	/** Precomputes the static shadows after all scene objects have been added. */
	public void prepareStaticShadows() {
		if (staticShadowsEnabled && renderer instanceof IsometricRenderer isoRenderer) {
			staticShadows.prepare(isoRenderer, expandedRenderables());
		}
	}

	/** Rebuilds shadows after moving a static wall, column or platform. */
	public void invalidateStaticShadows() {
		staticShadows.invalidate();
		occlusion.invalidate();
		repaint();
	}

	/** Adds a blue 1 x 1 x 1 world-space wireframe cube for debugging. */
	public void addDebugCube(double x, double y, double z) {
		debugCubes.add(new DebugCube(x, y, z));
		repaint();
	}

	public boolean addRenderables(List<Renderable> renderables) {
		staticShadows.invalidate();
		occlusion.invalidate();
		return this.renderables.addAll(renderables);
	}
	public boolean addRenderable(Renderable renderable) {
		staticShadows.invalidate();
		occlusion.invalidate();
		return this.renderables.add(renderable);
	}
	public boolean removeRenderable(Renderable renderable) {
		staticShadows.invalidate();
		occlusion.invalidate();
		if (renderable instanceof OccludingEntity corner) {
			cornerFaces.remove(corner);
		}
		return this.renderables.remove(renderable);
	}
	public Renderable removeRenderable(int i) {
		staticShadows.invalidate();
		occlusion.invalidate();
		Renderable removed = this.renderables.remove(i);
		if (removed instanceof OccludingEntity corner) {
			cornerFaces.remove(corner);
		}
		return removed;
	}
	public void clearRenderables() {
		staticShadows.invalidate();
		occlusion.invalidate();
		this.renderables.clear();
		cornerFaces.clear();
	}
	public Renderable getRenderable(int i) {
		return this.renderables.get(i);
	}

	public void animationTick() {
		this.animationTick++;
	}
}
