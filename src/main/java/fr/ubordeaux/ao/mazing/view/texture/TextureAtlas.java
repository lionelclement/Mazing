package fr.ubordeaux.ao.mazing.view.texture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TextureAtlas {
	private BufferedImage atlas;
	public final int atlasWidth;
	public final int atlasHeight;
	public final int textureWidth;
	public final int textureHeight;

	private TextureAtlas(BufferedImage atlas, int atlasWidth, int atlasHeight, int textureWidth, int textureHeight) {
		this.atlas = atlas;
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	public static TextureAtlas loadFromImage(Path path, int textureWidth, int textureHeight) throws IOException {
		BufferedImage img = ImageIO.read(path.toFile());
		return new TextureAtlas(img, img.getWidth(), img.getHeight(), textureWidth, textureHeight);
	}

	public static TextureAtlas loadFromResourceImage(String path, int textureWidth, int textureHeight) {
		URL resource = TextureAtlas.class.getResource(path);
		BufferedImage img = null;
		try {
			img = ImageIO.read(resource);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new TextureAtlas(img, img.getWidth(), img.getHeight(), textureWidth, textureHeight);
	}

	public static TextureAtlas stitchFromResourceDir(String path) {
		URL resource = TextureAtlas.class.getResource(path);
		return stitchFromDir(Path.of(resource.getPath()));
	}

	public static TextureAtlas stitchFromDir(Path dirPath) {
		File dir = new File(dirPath.toString());
		if (!dir.exists() || !dir.isDirectory()) {
			throw new IllegalArgumentException("Path is not a directory");
		}
		ArrayList<File> files = new ArrayList<>();
		Collections.addAll(files, dir.listFiles((_, name) -> name.toLowerCase().endsWith(".png")));
		files.sort(Comparator.comparing(File::getName));
		var size = files.size();
		var textureHeight = 0;
		var textureWidth = 0;

		ArrayList<BufferedImage> images = new ArrayList<>();
		for (File f : files) {
			BufferedImage img = null;
			try {
				img = ImageIO.read(f);
			} catch (IOException e) {
				System.err.println("Failed to load image: " + f.toPath().toAbsolutePath());
				// throw new RuntimeException(e);
				continue;
			}
			images.add(img);
			int height = img.getHeight();
			int width = img.getWidth();
			textureWidth = Math.max(textureWidth, width);
			textureHeight = Math.max(textureHeight, height);
		}
		var xamount = (int) Math.sqrt(size);
		var yamount = (int) Math.ceil((double) size / xamount);
		var atlasWidth = textureWidth * xamount;
		var atlasHeight = textureHeight * yamount;
		BufferedImage atlas = new BufferedImage(atlasWidth, atlasHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = atlas.createGraphics();
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, atlasWidth, atlasHeight);
		g.setComposite(AlphaComposite.SrcOver);
		int x = 0;
		int y = 0;
		for (BufferedImage image : images) {
			g.drawImage(image, x, y, null);
			x += textureWidth;
			if (x >= atlasWidth) {
				x = 0;
				y += textureHeight;
			}
		}
		g.dispose();
		return new TextureAtlas(atlas, atlasWidth, atlasHeight, textureWidth, textureHeight);

	}

	public void writeToDisk(String directory, String name) throws IOException {
		if (!new File(directory).exists()) {
			Files.createDirectories(Path.of(directory));
		}
		Path path = Path.of(directory, String.format("atlas_%s.png", name));
		ImageIO.write(this.atlas, "png", path.toFile());
	}

	public void writeToDisk(String name) throws IOException {
		writeToDisk(".", name);
	}

	private void blitAbsolute(Graphics2D g, int x, int y, int textureX, int textureY) {
		// int dx2 = x + textureWidth;
		// int dy2 = y + textureHeight;
		g.drawImage(atlas, x, y, x + textureWidth, y + textureHeight, textureX, textureY, textureX + this.textureWidth, textureY + this.textureHeight, null);
	}

	private void blitAbsolute(Graphics2D g, int x, int y, int textureX, int textureY, int zoom) {
		g.drawImage(atlas, x, y, x + textureWidth / zoom, y + textureHeight / zoom, textureX, textureY, textureX + textureWidth, textureY + textureHeight, null);
		// g.drawRect(x, y, textureWidth / zoom, textureHeight / zoom);
	}


	/// Draw a texture from the atlas at the specified position.
	///
	/// If you have an atlas of 3\*2 textures:
	/// ```
    /// TextureAtlas atlas = createAtlas("3*2");
    /// // draws the top left texture, in the top left corner of the screen
    /// atlas.blit(g, 0, 0, 0, 0);
    /// // draw the second texture in the top row of the atlas in the top left corner of the screen
    /// atlas.blit(g, 0, 0, 0, 1);
    /// ```
	///
	/// @param g        the graphics context to draw onto
	/// @param x        the x position to draw the texture
	/// @param y        the y position to draw the texture
	/// @param textureX the x index of the texture in the atlas
	/// @param textureY the y index of the texture in the atlas
	public void blit(Graphics2D g, int x, int y, int textureX, int textureY) {
		this.blitAbsolute(g, x, y, textureX * this.textureWidth, textureY * this.textureHeight);
	}

	/// Draw a texture from the atlas at the specified position.
	///
	/// If you have an atlas of 3\*2 textures:
	/// ```
    /// TextureAtlas atlas = createAtlas("3*2");
    /// // draws the top left texture, in the top left corner of the screen
    /// atlas.blit(g, 0, 0, 0, 0);
    /// // draw the second texture in the top row of the atlas in the top left corner of the screen
    /// atlas.blit(g, 0, 0, 0, 1);
    /// ```
	///
	/// @param g        the graphics context to draw onto
	/// @param x        the x position to draw the texture
	/// @param y        the y position to draw the texture
	/// @param textureX the x index of the texture in the atlas
	/// @param textureY the y index of the texture in the atlas
	public void blit(Graphics2D g, int x, int y, int textureX, int textureY, int zoom) {
		this.blitAbsolute(g, x, y, textureX * this.textureWidth, textureY * this.textureHeight,  zoom);
	}

	@Override
	public String toString() {
		return "TextureAtlas{" +
				// "atlas=" + atlas +
				", atlasWidth=" + atlasWidth +
				", atlasHeight=" + atlasHeight +
				", textureWidth=" + textureWidth +
				", textureHeight=" + textureHeight +
				'}';
	}
}
