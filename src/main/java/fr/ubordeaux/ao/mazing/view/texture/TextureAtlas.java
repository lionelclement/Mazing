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

/**
 * Represents TextureAtlas in the rendering system.
 */
public class TextureAtlas {
	private BufferedImage atlas;
	private final int atlasWidth;
	private final int atlasHeight;
	private final int textureWidth;
	private final int textureHeight;
	private final int xOffset;
	private final int yOffset;

	private TextureAtlas(BufferedImage atlas, int atlasWidth, int atlasHeight, int textureWidth, int textureHeight, int xOffset, int yOffset) {
		this.atlas = atlas;
		this.atlasWidth = atlasWidth;
		this.atlasHeight = atlasHeight;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}

	private TextureAtlas(BufferedImage atlas, int atlasWidth, int atlasHeight, int textureWidth, int textureHeight) {
		this(atlas, atlasWidth, atlasHeight, textureWidth, textureHeight, 0, 0);
	}

	/**
	 * Performs the loadFromImage operation.
	 * @param path path value
	 * @param textureWidth texture width value
	 * @param textureHeight texture height value
	 * @return the resulting value
	 * @throws java.io.IOException if the operation cannot be completed
	 */
	public static TextureAtlas loadFromImage(Path path, int textureWidth, int textureHeight) throws IOException {
		BufferedImage img = ImageIO.read(path.toFile());
		return new TextureAtlas(img, img.getWidth(), img.getHeight(), textureWidth, textureHeight);
	}

	/**
	 * Performs the loadFromResourceImage operation.
	 * @param path path value
	 * @param textureWidth texture width value
	 * @param textureHeight texture height value
	 * @return the resulting value
	 */
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

	/**
	 * Performs the stitchFromResourceDir operation.
	 * @param path path value
	 * @param xOffset x offset value
	 * @param yOffset y offset value
	 * @return the resulting value
	 */
	public static TextureAtlas stitchFromResourceDir(String path, int xOffset, int yOffset) {
		URL resource = TextureAtlas.class.getResource(path);
		return stitchFromDir(Path.of(resource.getPath()), xOffset, yOffset);
	}

	/**
	 * Performs the stitchFromResourceDir operation.
	 * @param path path value
	 * @return the resulting value
	 */
	public static TextureAtlas stitchFromResourceDir(String path) {
		URL resource = TextureAtlas.class.getResource(path);
		return stitchFromDir(Path.of(resource.getPath()), 0, 0);
	}

	/**
	 * Performs the stitchFromDir operation.
	 * @param dirPath dir path value
	 * @param xOffset x offset value
	 * @param yOffset y offset value
	 * @return the resulting value
	 */
	public static TextureAtlas stitchFromDir(Path dirPath, int xOffset, int yOffset) {
		File dir = new File(dirPath.toString());
		if (!dir.exists() || !dir.isDirectory()) {
			throw new IllegalArgumentException("Path is not a directory");
		}
		ArrayList<File> files = new ArrayList<>();
		Collections.addAll(files, dir.listFiles((directory, name) -> name.toLowerCase().endsWith(".png")));
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
		return new TextureAtlas(atlas, atlasWidth, atlasHeight, textureWidth, textureHeight, xOffset, yOffset);

	}

	/**
	 * Performs the writeToDisk operation.
	 * @param directory directory value
	 * @param name name value
	 * @throws java.io.IOException if the operation cannot be completed
	 */
	public void writeToDisk(String directory, String name) throws IOException {
		if (!new File(directory).exists()) {
			Files.createDirectories(Path.of(directory));
		}
		Path path = Path.of(directory, String.format("atlas_%s.png", name));
		ImageIO.write(this.atlas, "png", path.toFile());
	}

	/**
	 * Performs the writeToDisk operation.
	 * @param name name value
	 * @throws java.io.IOException if the operation cannot be completed
	 */
	public void writeToDisk(String name) throws IOException {
		writeToDisk(".", name);
	}

	private void blitAbsolute(Graphics2D g, int x, int y, int textureX, int textureY) {
		blitAbsolute(g, x, y, textureX, textureY, 1);
	}

	private void blitAbsolute(Graphics2D g, int x, int y, int textureX, int textureY, int zoom) {
		g.drawImage(atlas, x + xOffset / zoom, y + yOffset / zoom, x + textureWidth / zoom + xOffset / zoom, y + textureHeight / zoom + yOffset / zoom, textureX, textureY, textureX + textureWidth, textureY + textureHeight, null);
		// g.drawRect(x, y, textureWidth / zoom, textureHeight / zoom);
	}

	/**
	 * Draws an atlas tile at the specified screen coordinates.
	 * @param g destination graphics context
	 * @param x horizontal screen position
	 * @param y vertical screen position
	 * @param textureX atlas tile column
	 * @param textureY atlas tile row
	 */
	public void blit(Graphics2D g, int x, int y, int textureX, int textureY) {
		this.blitAbsolute(g, x, y, textureX * this.textureWidth, textureY * this.textureHeight);
	}

	/**
	 * Draws an atlas tile at the specified screen coordinates.
	 * @param g destination graphics context
	 * @param x horizontal screen position
	 * @param y vertical screen position
	 * @param textureX atlas tile column
	 * @param textureY atlas tile row
	 * @param zoom rendering zoom factor
	 */
	public void blit(Graphics2D g, int x, int y, int textureX, int textureY, int zoom) {
		this.blitAbsolute(g, x, y, textureX * this.textureWidth, textureY * this.textureHeight, zoom);
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
	/**
	 * Returns the atlaswidth.
	 * @return the resulting value
	 */
	public int getAtlasWidth() {
		return atlasWidth;
	}
	/**
	 * Returns the atlasheight.
	 * @return the resulting value
	 */
	public int getAtlasHeight() {
		return atlasHeight;
	}
	/**
	 * Returns the texturewidth.
	 * @return the resulting value
	 */
	public int getTextureWidth() {
		return textureWidth;
	}
	/**
	 * Returns the textureheight.
	 * @return the resulting value
	 */
	public int getTextureHeight() {
		return textureHeight;
	}
	/**
	 * Returns the xoffset.
	 * @return the resulting value
	 */
	public int getXOffset() {
		return xOffset;
	}
	/**
	 * Returns the yoffset.
	 * @return the resulting value
	 */
	public int getYOffset() {
		return yOffset;
	}
}
