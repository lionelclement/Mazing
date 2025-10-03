package fr.ubordeaux.ao.mazing.api;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class BoardGame extends JPanel implements IBoardGame {

	private static final long serialVersionUID = 1L;
	private final static int TILE_WIDTH = 128, TILE_HEIGHT = 100;
	private final int width;
	private final int height;
	private final static int CROP_WIDTH = 256, CROP_HEIGHT = 512;
	private BufferedImage background;

	private final Map<Integer, Integer> codeToRankMap = new HashMap<>();
	List<IRenderable3D> _sceneObjects = new ArrayList<>();
	List<IRenderable3D> _sceneCharacters = new ArrayList<>();

	@SuppressWarnings("rawtypes")
	private List<Character> characters;
	private Timer gameLoop;
	private Map<Integer, Image> codeToImageMap;
	private Character<CharacterMode> player;

	public BoardGame(int width, int height) {
		this.width = width;
		this.height = height;
		TileMap.getInstance(TILE_WIDTH, TILE_HEIGHT, width, height, 8, 8);
		characters = new ArrayList<>();
		codeToImageMap = new HashMap<>();

		BufferedImage imagesSheet = Loader.loadImage("/Dungeon-Pack/Isometric/grounds/all_0.png");
		crop(imagesSheet, CROP_WIDTH, CROP_HEIGHT, 10, 100, 0, 1, 0, 0, RenderType.FLOOR);

		imagesSheet = Loader.loadImage("/Dungeon-Pack/Isometric/walls/all_100.png");
		crop(imagesSheet, CROP_WIDTH, CROP_HEIGHT, 10, 100, 100, 1, 0, 0, RenderType.OBJECT);

		imagesSheet = Loader.loadImage("/Dungeon-Pack/Isometric/walls/all_200.png");
		crop(imagesSheet, CROP_WIDTH, CROP_HEIGHT, 10, 100, 200, 1, 0, 0, RenderType.OBJECT);

		imagesSheet = Loader.loadImage("/Dungeon-Pack/Isometric/stairs/all_300.png");
		crop(imagesSheet, CROP_WIDTH, CROP_HEIGHT, 10, 100, 300, 1, 0, 0, RenderType.OBJECT);

		imagesSheet = Loader.loadImage("/Dungeon-Pack/Isometric/constructions/all_400.png");
		crop(imagesSheet, CROP_WIDTH, CROP_HEIGHT, 10, 100, 400, 1, 0, 0, RenderType.OBJECT);

		imagesSheet = Loader.loadImage("/Dungeon-Pack/Isometric/objects/all_500.png");
		crop(imagesSheet, CROP_WIDTH, CROP_HEIGHT, 10, 100, 500, 1.4f, 0, 0, RenderType.OBJECT);

		codeToRankMap.put(138, 0);
		codeToRankMap.put(136, 2);
		codeToRankMap.put(139, 2);
		codeToRankMap.put(137, 2);
		codeToRankMap.put(188, 0);
		codeToRankMap.put(189, 2);
		codeToRankMap.put(190, 2);
		codeToRankMap.put(191, 2);
		codeToRankMap.put(180, 0);
		codeToRankMap.put(181, 2);
		codeToRankMap.put(182, 2);
		codeToRankMap.put(183, 2);

		gameLoop = new Timer(1000 / 40, _ -> {
			updateAnimations();
		});
		gameLoop.start();
	}

	public BoardGame() {
		this(1024, 800);
	}

	private void crop(BufferedImage sheet, int width, int height, int colsPerRow,
			int maxSize, int offset, double scale, int offsetX, int offsetY, RenderType renderType) {
		int col = 0;
		int row = 0;
		for (int i = 0; i < maxSize; i++) {
			BufferedImage sub = sheet.getSubimage(col * width, row * height, width, height);

			// Calcul des dimensions après scale
			int scaledWidth = (int) (TILE_WIDTH * scale);
			int scaledHeight = (int) (TILE_HEIGHT * 2 * scale);

			// Créer l'image redimensionnée
			BufferedImage scaled = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = scaled.createGraphics();

			// Dessiner l'image en respectant le scale
			g2d.drawImage(sub, 0, 0, scaledWidth, scaledHeight, null);
			g2d.dispose();

			codeToImageMap.put(i + offset, new Image(scaled, offsetX, offsetY, renderType));

			col++;
			if (col == colsPerRow) {
				col = 0;
				row++;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateAnimations() {
		for (Character<CharacterMode> c : characters) {
			Animation<CharacterMode> animation = c.getCurrentAnimation(c.getCurrentMode().ordinal());
			animation.tick((AbstractCharacter<? extends CharacterMode>) c);
		}
		repaint();
	}

	private void renderScene(Graphics g, List<IRenderable3D> sceneObjects) {
		// Trier les objets par z, puis x+y, puis par type
		List<IRenderable3D> sorted = sceneObjects.stream()
				.sorted(Comparator
						.comparingInt(o -> ((int) ((IRenderable3D) o).getZ()))
						.thenComparingInt(o -> ((int) ((IRenderable3D) o).getIsoDepth()))
						.thenComparingInt(o -> ((IRenderable3D) o).getRenderType().ordinal())
						.thenComparingInt(o -> ((IRenderable3D) o).getRank())
						.thenComparing(o -> ((IRenderable3D) o).getIsoDepth()))
				.toList();

		for (IRenderable3D obj : sorted) {
			obj.render(g);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Dessine le fond pré-généré
		if (background != null) {
			g.drawImage(background, 0, 0, null);
		}

		// Centre de l'écran pour le rendu
		g.translate(width / 2, TILE_HEIGHT);

		List<IRenderable3D> renderableScene = new ArrayList<>(_sceneObjects.size() + _sceneCharacters.size());
		renderableScene.addAll(_sceneObjects);
		renderableScene.addAll(_sceneCharacters);
		renderScene(g, renderableScene);

	}

	@Override
	public void fillCheckerboardBackground(int size) {

		background = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = background.createGraphics();

		// centrer l'origine comme dans paintComponent
		g.translate(width / 2, TILE_HEIGHT);

		// dessiner la grille à partir des codes (exemple avec CODEBG = 72)
		for (int y = -size / 2; y < size - size / 2; y++) {
			for (int x = -size / 2; x < size - size / 2; x++) {
				if ((Math.abs(x) + Math.abs(y)) % 2 == 0) {
					TileMap.getInstance().drawTile(x, y, 0, g, Color.BLACK);
				} else {
					TileMap.getInstance().drawTile(x, y, 0, g, Color.WHITE);
				}
			}
		}
		g.dispose();
	}

	@Override
	public void fillTilesBackground(int code, float alpha) {

		background = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = background.createGraphics();

		// remplir par défaut
		g.setColor(new Color(0xEAD4AF));
		g.fillRect(0, 0, width, height);

		// centrer l'origine comme dans paintComponent
		g.translate(width / 2, TILE_HEIGHT);

		int XMINBG = -15;
		int XMAXBG = 15;
		int YMINBG = -15;
		int YMAXBG = 15;

		Image tileImg = codeToImageMap.get(code);
		if (tileImg != null) {
			for (int y = YMINBG; y < YMAXBG; y++) {
				for (int x = XMINBG; x < XMAXBG; x++) {
					TileMap.getInstance().drawImage(x, y, 0, tileImg.getBufferedImage(),
							g, tileImg.getOffsetX(), tileImg.getOffsetY(),
							RenderType.FLOOR, alpha);
				}
			}
		}

		g.dispose();
	}

	@Override
	public void updatePlayerPosition(float x, float y, float z) {
		player.setPosition(x, y, z);
	}

	public Character<?> getPlayer() {
		return player;
	}

	// les murs sont W, S, N, E
	// W, S : rank = 0
	// N, E : rank = 2
	// characters : rank = 1 (entre les deux)
	private int codeToRank(int code) {
		if (codeToRankMap.keySet().contains(code)) {
			return codeToRankMap.get(code);
		} else {
			if (code < 100)
				return 0;
			if (code % 4 == 0 || code % 4 == 2) {
				return 0;
			} else {
				return 2;
			}
		}
	}

	@Override
	public void addToSceneMap(int code, int x, int y, int z, float alpha) {
		if (code != -1) {
			Image image = codeToImageMap.get(code);
			int rank = codeToRank(code);
			if (image != null) {
				ImageRendarable3D imageRendarable3D = new ImageRendarable3D(
						image.getBufferedImage(), x, y, z, image.getOffsetX(),
						image.getOffsetY(), rank,
						image.getRenderType(), alpha);
				_sceneObjects.add(imageRendarable3D);
			}
		}
	}

	@Override
	public void addToSceneMap(int[][][] matrix) {
		for (int z = 0; z < matrix.length; z++) {
			for (int y = 0; y < matrix[z].length; y++) {
				for (int x = 0; x < matrix[z][y].length; x++) {

					// final float finalX = x;
					// final float finalY = y;
					// final float finalZ = z;
					int code = matrix[z][y][x];

					if (code != -1) {
						// int indexTable = (code <= 100) ? 0
						// : (code <= 200) ? 1
						// : 2;
						// int newCode = imageMatrix[z][y][x] % 100;
						Image image = codeToImageMap.get(code);
						int rank = codeToRank(code);
						if (image != null) {
							ImageRendarable3D imageRendarable3D = new ImageRendarable3D(
									image.getBufferedImage(), x, y, z, image.getOffsetX(), image.getOffsetY(), rank,
									image.getRenderType(), 1.0f);

							_sceneObjects.add(imageRendarable3D);

						}

					}

				}
			}
		}
	}

	@Override
	public void addToSceneMap(Character<?> character) {
		characters.add(character);
		_sceneCharacters.add(character);
	}

	// getter pour position actuelle du joueur en grille
	@Override
	public float getPlayerX() {
		return player.getX();
	}

	@Override
	public float getPlayerY() {
		return player.getY();
	}

	@Override
	public float getPlayerZ() {
		return player.getZ();
	}

	@Override
	public void updatePlayerSpeed(float vx, float vy, float vz) {
		player.setSpeed(vx, vy, vz);
	}

	@Override
	public void stopPlayer() {
		player.stop();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void assignPlayer(Character<? extends CharacterMode> player) {
		this.player = (Character<CharacterMode>) player;
	}

	@Override
	public void updatePlayerMode(String state) {
		player.changeMode(state);
	}

	@Override
	public void updatePlayerDirection(String direction) {
		player.changeDirection(direction);
	}

	@Override
	public void clearMap() {
		_sceneObjects.clear();
	}

}
