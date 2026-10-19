package fr.ubordeaux.ao.mazing;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.Renderable;
import fr.ubordeaux.ao.mazing.view.swing.DebugWindow;
import fr.ubordeaux.ao.mazing.view.swing.ViewWindow;
import fr.ubordeaux.ao.mazing.view.entity.AnimatedEntity;
import fr.ubordeaux.ao.mazing.view.entity.Entity;
import fr.ubordeaux.ao.mazing.view.renderer.DebugIsometricRenderer;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedSprites;
import fr.ubordeaux.ao.mazing.view.sprite.IsometricSprites;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;
import fr.ubordeaux.ao.mazing.view.texture.TextureAtlases;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Main {
	static void main(String[] args) throws IOException {
		TextureAtlases.writeAllToDisk("atlases-test");
		// System.out.println(Sprites.BARREL);

		// BufferedImage test = new BufferedImage(2048, 2048, BufferedImage.TYPE_INT_ARGB);
		// Graphics2D g = test.createGraphics();
		// g.setComposite(AlphaComposite.Clear);
		// g.fillRect(0, 0, 2048, 2048);
		// g.setComposite(AlphaComposite.SrcOver);
		// g.setColor(Color.BLACK);
		List<Renderable> floor = new ArrayList<>();
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				var sprite = IsometricSprites.DIRT;
				if ((x + y) % 2 == 0) {
					sprite = IsometricSprites.STONE;
				}
				floor.add(new Entity(sprite, Orientation.NORTH, x, y, 0));
			}
		}
		System.out.println(floor.size());
		int tick = 0;
		// Renderer renderer = new IsometricRenderer(10, 1000, 256, 128, 60);
		// renderer.setDebug(true);
		floor.add(new Entity(IsometricSprites.STAIRS, Orientation.SOUTH, 1, 2, 0));
		floor.add(new Entity(IsometricSprites.STONE_MISSING_TILES, Orientation.NORTH, 2, 1, 1));
		floor.add(new Entity(IsometricSprites.STAIRS, Orientation.SOUTH, 2, 2, 1));
		floor.add(new Entity(IsometricSprites.STONE_WALL, Orientation.EAST, 2, 4, 0));
		floor.add(new Entity(IsometricSprites.CHEST_CLOSED, Orientation.SOUTH, 2, 3, 1));
		// for (Renderable renderable : floor.stream().sorted().toList()) {
		// 	renderer.renderInIsoGrid(g, renderable, tick);
		// }
		// g.dispose();
		// ImageIO.write(test, "png", new File("test.png"));
		var crusader = new AnimatedEntity(AnimatedSprites.CRUSADER_ATTACK, Orientation.NORTH, 1, 2, 0);

		ViewWindow w = new ViewWindow();
		w.addKeyListener(new KeyAdapter() {
			boolean toggle = false;
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				switch (e.getKeyCode()) {
					case KeyEvent.VK_UP:
						crusader.setY(crusader.getY() - 1);
						break;
					case KeyEvent.VK_DOWN:
						crusader.setY(crusader.getY() + 1);
						break;
					case KeyEvent.VK_LEFT:
						crusader.setX(crusader.getX() - 1);
						break;
					case KeyEvent.VK_RIGHT:
						crusader.setX(crusader.getX() + 1);
						break;
					case KeyEvent.VK_Q, KeyEvent.VK_ESCAPE:
						w.dispatchEvent(new WindowEvent(w, WindowEvent.WINDOW_CLOSING));
						break;
					case KeyEvent.VK_SPACE:
						if (toggle) {
							crusader.setSprite(AnimatedSprites.CRUSADER_BLOCK);
						} else {
							crusader.setSprite(AnimatedSprites.CRUSADER_ATTACK);
						}
						toggle = !toggle;
						break;

				}
			}
		});

		Timer repaintLoop = new Timer(1000 / 60, e -> {
			w.panel.repaint();
		});
		repaintLoop.start();
		Timer animationLoop = new Timer(1000 / 20, e -> {
			w.panel.animationTick();
			// w.panel.repaint();
			// w.panel.forEachRenderable(r -> r.animationTick());
		});
		animationLoop.start();
		int[] t = {0};
		Timer testLoop = new Timer(1000 , e -> {
			switch (t[0]) {
				case 0 -> crusader.setSprite(AnimatedSprites.CRUSADER_ATTACK);
				case 1 -> crusader.setSprite(AnimatedSprites.CRUSADER_BLOCK);
				case 2 -> crusader.setSprite(AnimatedSprites.CRUSADER_DEATH);
				case 3 -> crusader.setSprite(AnimatedSprites.CRUSADER_HIT);
				case 4 -> crusader.setSprite(AnimatedSprites.CRUSADER_IDLE);
				case 5 -> crusader.setSprite(AnimatedSprites.CRUSADER_JUMP);
				case 6 -> crusader.setSprite(AnimatedSprites.CRUSADER_RUN);
				case 7 -> crusader.setSprite(AnimatedSprites.CRUSADER_WALK);
			}
			t[0]++;
			if (t[0] > 7) {
				t[0] = 0;
			}
			// w.panel.repaint();
			// w.panel.forEachRenderable(r -> r.animationTick());
		});
		testLoop.start();

		w.panel.addRenderables(floor);
		w.panel.addRenderable(crusader);

		var dw = new DebugWindow();

		// printDebugScene();
	}


	public static void printDebugScene() throws IOException {
		BufferedImage test = new BufferedImage(2200, 2650, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = test.createGraphics();
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, 2048, 2048);
		g.setComposite(AlphaComposite.SrcOver);
		g.setColor(Color.BLACK);
		int size = 2048 / 256 - 1;
		int startX = size / 2;
		int startY = -size / 2;
		int x = startX;
		int y = startY;
		DebugIsometricRenderer renderer = new DebugIsometricRenderer(2, 1250, 256, 128, 60);
		for (Map.Entry<String, Sprite> sprite : IsometricSprites.REGISTRY.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.toList()) {
			Renderable renderable = new Entity(sprite.getValue(), Orientation.NORTH, x, y, 0);
			Point p = renderer.renderInIsoGrid(g, renderable, 0);
			g.setColor(Color.RED);
			g.setStroke(new BasicStroke(2));
			g.drawPolygon(new int[]{p.x(), p.x() + renderer.halfTileDiagX, p.x() + renderer.tileDiagX, p.x() + renderer.halfTileDiagX},
					new int[]{p.y(), p.y() - renderer.halfTileDiagY, p.y(), p.y() + renderer.halfTileDiagY},
					4);
			p = renderer.renderInIsoGrid(g, renderable, 0);
			g.setFont(g.getFont().deriveFont(16f).deriveFont(Font.BOLD));
			var bounds = g.getFont().getStringBounds(sprite.getKey().toUpperCase(), g.getFontRenderContext());
			g.setColor(Color.BLACK);
			g.drawString(sprite.getKey().toUpperCase(), (int) (p.x() + 128 - bounds.getWidth() / 2), (int) (p.y() + 60 + bounds.getHeight()));
			x++;
			y++;
			if (x - startX > size) {
				startX--;
				startY++;
				x = startX;
				y = startY;
			}
		}
		g.dispose();
		ImageIO.write(test, "png", new File("debug.png"));

	}

}
