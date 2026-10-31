package fr.ubordeaux.ao.mazing.view.swing;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.Renderable;
import fr.ubordeaux.ao.mazing.view.entity.Entity;
import fr.ubordeaux.ao.mazing.view.renderer.DebugIsometricRenderer;
import fr.ubordeaux.ao.mazing.view.sprite.IsometricSprites;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.util.Map;

/**
 * Provides DebugWindow functionality.
 */
public class DebugWindow extends JFrame {
	/**
	 * Provides the private final viewpanel panel; value.
	 */
	private final ViewPanel panel;
	/**
	 * Provides the private int zoom = 2; value.
	 */
	private int zoom = 2;
	/**
	 * Provides the private orientation orientation = orientation.north; value.
	 */
	private Orientation orientation = Orientation.NORTH;

	private enum Mode {
		FLOOR,
		CRUSADER
	}

	/**
	 * Creates an instance of DebugWindow.
	 */
	public DebugWindow() {
		super("Isometric 3D Game");

		DebugIsometricRenderer renderer = new DebugIsometricRenderer(5, 1000, 256, 128, 60);
		renderer.setDebug(true);
		this.panel = new ViewPanel(1600, 2000, renderer);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				renderer.setOrigin(5, panel.getHeight() / 2);
			}
		});
		panel.setBounds(0, 0, 1600, 2000);
		panel.setLayout(null);
		setupFloorPanel(panel, renderer);

		setContentPane(panel);

		setResizable(true);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(1600, 1000));
		setSize(new Dimension(1600, 1000));
		this.setLocationRelativeTo(null);
		pack();
		JButton btnNorth = new JButton("North");
		btnNorth.addActionListener(e -> {
			orientation = Orientation.NORTH;
			setupFloorPanel(panel, renderer);
		});
		JButton btnSouth = new JButton("South");
		btnSouth.addActionListener(e -> {
			orientation = Orientation.SOUTH;
			setupFloorPanel(panel, renderer);
		});
		JButton btnEast = new JButton("East");
		btnEast.addActionListener(e -> {
			orientation = Orientation.EAST;
			setupFloorPanel(panel, renderer);
		});
		JButton btnWest = new JButton("West");
		btnWest.addActionListener(e -> {
			orientation = Orientation.WEST;
			setupFloorPanel(panel, renderer);
		});
		btnNorth.setLayout(null);
		btnSouth.setLayout(null);
		btnWest.setLayout(null);
		btnEast.setLayout(null);
		btnNorth.setBounds(1700, 500, 100, 20);
		btnSouth.setBounds(1820, 450, 100, 20);
		btnWest.setBounds(1820, 500, 100, 20);
		btnEast.setBounds(1700, 450, 100, 20);
		add(btnNorth);
		add(btnSouth);
		add(btnWest);
		add(btnEast);
		Line2D.Double line = new Line2D.Double(1500, 500, 1650, 450);

		// panel.addRenderable(new Renderable() {
		// 	@Override
		// 	public void renderInScreen(Graphics2D g, int x, int y, int animationTick, int zoom) {
		// 		g.setColor(Color.RED);
		// 		g.draw(line);
		// 	}
		// })

	}
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawLine(1700, 450, 1820, 500);
		g2d.drawLine(1820, 450, 1700, 500);
	}


	private void setupFloorPanel(ViewPanel panel, DebugIsometricRenderer renderer) {
		panel.clearRenderables();
		// int size = (2048 / (256/zoom) - 1);
		int size = 10;
		int startX = 3;
		int startY = -2;
		int x = startX;
		int y = startY;
		for (Map.Entry<String, Sprite> sprite : IsometricSprites.REGISTRY.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.toList()) {
			Renderable renderable = new DebugEntity(new Entity(sprite.getValue(), orientation, x, y, 0), renderer.getTileDiagX(), renderer.getTileDiagY(), sprite.getKey().toUpperCase());
			panel.addRenderable(renderable);
			x++;
			y++;
			if (x - startX > size) {
				startX--;
				startY++;
				x = startX;
				y = startY;
			}
		}
		this.repaint();

	}


	private record DebugEntity(Entity inner, int diagX, int diagY, String name) implements Renderable {

		@Override
		public void renderInScreen(Graphics2D g, int x, int y, int animationTick, int zoom) {
			g.setColor(Color.RED);
			g.setStroke(new BasicStroke(2));
			g.drawPolygon(new int[]{x, x + diagX / 2 / zoom, x + diagX / zoom, x + diagX / zoom / 2},
					new int[]{y, y - diagY / zoom / 2, y, y + diagY / zoom / 2},
					4);
			g.setFont(g.getFont().deriveFont(16f/zoom).deriveFont(Font.BOLD));
			var bounds = g.getFont().getStringBounds(name, g.getFontRenderContext());
			g.setColor(Color.BLACK);
			g.drawString(name, (int) (x + 128/zoom - bounds.getWidth() / 2), (int) (y + 60/zoom + bounds.getHeight()+5));
			inner.renderInScreen(g, x, y, animationTick, zoom);
		}

		@Override
		public int getX() {
			return inner.getX();
		}

		@Override
		public int getY() {
			return inner.getY();
		}

		@Override
		public int getZ() {
			return inner.getZ();
		}

		@Override
		public void setSprite(Sprite sprite) {
			inner.setSprite(sprite);
		}
	}

	/**
	 * Returns panel.
	  * @return the value or object described above
	 */
	public ViewPanel getPanel() {
		return panel;
	}
}