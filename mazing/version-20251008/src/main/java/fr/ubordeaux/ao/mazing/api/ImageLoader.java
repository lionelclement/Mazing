package fr.ubordeaux.ao.mazing.api;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

class ImageLoader {

	private static ImageLoader instance;

	private ImageLoader() {
	}

	public static ImageLoader getInstance() {
		if (instance == null) {
			instance = new ImageLoader();
		}
		return instance;
	}

	public static BufferedImage loadImage(String path) {
		try {
			return ImageIO.read(ImageLoader.class.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Charge une série d'images selon le pattern :
	 * crusader_run_x00Y.png
	 * x = 1,3,5,7 (directions)
	 * y = 00..16 (frames)
	 */
	public static BufferedImage[] loadAnimations(int numFrames, int direction, String prefix, float scale) {
		BufferedImage[] animations = new BufferedImage[numFrames];

		for (int frame = 0; frame < numFrames; frame++) {
			String frameNumber = String.format("%04d", frame); // 0000,0001,...
			String directionNumber = String.format("%01d", direction);
			String path = String.format("%s%s%s.png", prefix, directionNumber, frameNumber);
			
			BufferedImage img = loadImage(path);
			if (img != null) {
				// Scale l'image
				int newWidth = (int) (img.getWidth() / scale);
				int newHeight = (int) (img.getHeight() / scale);
				BufferedImage scaled = new BufferedImage(newWidth, newHeight,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = scaled.createGraphics();
				g2d.drawImage(img, 0, 0, newWidth, newHeight, null);
				g2d.dispose();
				animations[frame] = scaled;
			}

		}

		return animations; // tableau d'images réduites

	}

		/**
	 * Charge une série d'images selon le pattern :
	 * crusader_run_x00Y.png
	 * x = 1,3,5,7 (directions)
	 * y = 00..16 (frames)
	 */
	public static BufferedImage[] loadTurnAnimations(int numFrames, String prefix, float scale, boolean left) {
		BufferedImage[] animations = new BufferedImage[numFrames];
		for (int frame = 0; frame < numFrames; frame++) {
			Direction d = Direction.values()[frame % Direction.values().length];
			int direction = d.ordinal();

			String frameNumber = String.format("%04d", frame); // 0000,0001,...
			String directionNumber = String.format("%01d", direction);
			String path = String.format("%s%s%s.png", prefix, directionNumber, frameNumber);

			BufferedImage img = loadImage(path);
			if (img != null) {
				// Scale l'image
				int newWidth = (int) (img.getWidth() / scale);
				int newHeight = (int) (img.getHeight() / scale);
				BufferedImage scaled = new BufferedImage(newWidth, newHeight,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = scaled.createGraphics();
				g2d.drawImage(img, 0, 0, newWidth, newHeight, null);
				g2d.dispose();
				animations[left ? frame : numFrames - frame - 1] = scaled;
			}

		}

		return animations; // tableau d'images réduites

	}

}