package fr.ubordeaux.ao.mazing.api;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

/**
 * Représente le plateau de jeu isométrique et gère le rendu des tuiles, murs et
 * personnages.
 * 
 * Cette classe hérite de {@link JPanel} et implémente {@link IBoardGame}. Elle
 * gère :
 * 
 * - Le rendu des objets 3D et des personnages.
 * - Le fond du plateau et les textures des tuiles.
 * - Les animations des personnages.
 * - La gestion des positions et états du joueur.
 * 
 * 
 */
public class BoardGame extends JPanel implements IBoardGame {

	private static final long serialVersionUID = 1L;
	/** Largeur d’une tuile en pixels */
	private final static int TILE_WIDTH = 128;
	/** Hauteur d’une tuile en pixels */
	private final static int TILE_HEIGHT = 100;
	/** Largeur du plateau */
	private final int width;

	/** Hauteur du plateau */
	private final int height;

	/** Largeur d'une image à découper dans la spritesheet */
	private final static int CROP_WIDTH = 256;

	/** Hauteur d'une image à découper dans la spritesheet */
	private final static int CROP_HEIGHT = 512;

	/** Image de fond du plateau */
	private BufferedImage background;

	/** Mapping du code d’image vers son rang de rendu */
	private final Map<Integer, Integer> codeToRankMap = new HashMap<>();

	/** Liste des objets 3D présents dans la scène */
	List<IRenderable3D> sceneObjects = new ArrayList<>();

	/** Liste des personnages présents dans la scène */
	List<IRenderable3D> sceneCharacters = new ArrayList<>();

	/** Mapping des coordonnées vers les codes d’objets de scène */
	// Map<DiscretePoint, Set<Integer>> pointToSceneObjectsMap = new HashMap<>();
	Map<DiscretePoint, Set<IRenderable3D>> pointToSceneObjectsMap = new HashMap<>();

	/** Liste des personnages du plateau */
	@SuppressWarnings("rawtypes")
	private List<Character> characters;

	/** Boucle de jeu pour mettre à jour les animations */
	private Timer gameLoop;

	/** Mapping du code d’image vers l’objet Image correspondant */
	private Map<Integer, Image> codeToImageMap;

	/** Référence vers le joueur */
	private Character<CharacterMode> player;

	/**
	 * Constructeur principal.
	 * 
	 * @param width  largeur du plateau en pixels
	 * @param height hauteur du plateau en pixels
	 */
	public BoardGame(int width, int height) {
		this.width = width;
		this.height = height;

		// Initialisation du TileMap pour le rendu
		TileMap.getInstance(TILE_WIDTH, TILE_HEIGHT, width, height, 8, 8);
		characters = new ArrayList<>();
		codeToImageMap = new HashMap<>();

		// Chargement et découpe des spritesheets
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
		crop(imagesSheet, CROP_WIDTH, CROP_HEIGHT, 10, 100, 500, 1.4f, 0, 20, RenderType.OBJECT);

		// Initialisation des rangs de rendu des objets
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

		// Timer de mise à jour des animations (40 fps)
		gameLoop = new Timer(1000 / 40, _ -> {
			updateAnimations();
		});
		gameLoop.start();
	}

	/**
	 * Constructeur par défaut avec taille 1024x800.
	 */
	public BoardGame() {
		this(1024, 800);
	}

	/**
	 * Découpe une spritesheet et enregistre les images dans codeToImageMap.
	 * 
	 * @param sheet      spritesheet source
	 * @param width      largeur d’une tuile
	 * @param height     hauteur d’une tuile
	 * @param colsPerRow nombre de colonnes par ligne dans la spritesheet
	 * @param maxSize    nombre maximum d’images à découper
	 * @param offset     code de départ pour l’image
	 * @param scale      facteur de mise à l’échelle
	 * @param offsetX    décalage X appliqué à l’image
	 * @param offsetY    décalage Y appliqué à l’image
	 * @param renderType type de rendu de l’image
	 */
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

	/**
	 * Met à jour toutes les animations des personnages.
	 */
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

		List<IRenderable3D> renderableScene = new ArrayList<>(sceneObjects.size() + sceneCharacters.size());
		renderableScene.addAll(sceneObjects);
		renderableScene.addAll(sceneCharacters);
		renderScene(g, renderableScene);

	}

	/**
	 * Remplit le plateau avec un fond en damier.
	 * 
	 * @param size taille du damier (nombre de tuiles par côté)
	 */
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

	/**
	 * Remplit le plateau avec une image de tuile spécifique.
	 * 
	 * @param code  code de la tuile
	 * @param alpha transparence de la tuile
	 */
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

	/**
	 * Met à jour la position du joueur.
	 * 
	 * @param x coordonnée X
	 * @param y coordonnée Y
	 * @param z coordonnée Z
	 */
	@Override
	public void updatePlayerPosition(float x, float y, float z) {
		player.setPosition(x, y, z);
	}

	/**
	 * Retourne le joueur actuel.
	 * 
	 * @return joueur
	 */
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

	/**
	 * Ajoute une tuile ou un objet à la scène.
	 * 
	 * @param code  code de l’objet
	 * @param x     coordonnée X
	 * @param y     coordonnée Y
	 * @param z     coordonnée Z
	 * @param alpha transparence
	 */
	@Override
	public void addToSceneMap(int code, int x, int y, int z, float alpha) {
		if (code != -1) {
			Image image = codeToImageMap.get(code);
			int rank = codeToRank(code);
			if (image != null) {
				ImageRenderable3D imageRendarable3D = new ImageRenderable3D(
						image.getBufferedImage(), x, y, z, image.getOffsetX(),
						image.getOffsetY(), rank,
						image.getRenderType(), alpha);
				sceneObjects.add(imageRendarable3D);
				// pointToSceneObjectsMap
				// .computeIfAbsent(new DiscretePoint(x, y, z), k -> new HashSet<>())
				// .add(code);
				pointToSceneObjectsMap
						.computeIfAbsent(new DiscretePoint(x, y, z), k -> new HashSet<>())
						.add(imageRendarable3D);
			}
		}
	}

	/**
	 * Ajoute une matrice complète de tuiles à la scène.
	 * 
	 * @param matrix matrice 3D de codes de tuiles
	 */
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
							ImageRenderable3D imageRendarable3D = new ImageRenderable3D(
									image.getBufferedImage(), x, y, z, image.getOffsetX(), image.getOffsetY(), rank,
									image.getRenderType(), 1.0f);

							sceneObjects.add(imageRendarable3D);
							// pointToSceneObjectsMap
							// .computeIfAbsent(new DiscretePoint(x, y, z), k -> new HashSet<>())
							// .add(code);
							pointToSceneObjectsMap
									.computeIfAbsent(new DiscretePoint(x, y, z), k -> new HashSet<>())
									.add(imageRendarable3D);

						}

					}

				}
			}
		}
	}

	/**
	 * Ajoute un personnage à la scène.
	 * 
	 * @param character personnage à ajouter
	 */
	@Override
	public void addToSceneMap(Character<?> character) {
		characters.add(character);
		sceneCharacters.add(character);
	}

	// getter pour position actuelle du joueur en grille
	/**
	 * Retourne la coordonnée X du joueur.
	 */
	@Override
	public float getPlayerX() {
		return player.getX();
	}

	/**
	 * Retourne la coordonnée Y du joueur.
	 */
	@Override
	public float getPlayerY() {
		return player.getY();
	}

	/**
	 * Retourne la coordonnée Z du joueur.
	 */
	@Override
	public float getPlayerZ() {
		return player.getZ();
	}

	/**
	 * Met à jour la vitesse du joueur.
	 */
	@Override
	public void updatePlayerSpeed(float vx, float vy, float vz) {
		player.setSpeed(vx, vy, vz);
	}

	/**
	 * Stoppe le joueur.
	 */
	@Override
	public void stopPlayer() {
		player.stop();
	}

	/**
	 * Assigne le joueur.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void assignPlayer(Character<? extends CharacterMode> player) {
		this.player = (Character<CharacterMode>) player;
	}

	/**
	 * Change le mode du joueur.
	 */
	@Override
	public void updatePlayerMode(String state) {
		player.changeMode(state);
	}

	/**
	 * Change la direction du joueur.
	 */
	@Override
	public void updatePlayerDirection(String direction) {
		player.changeDirection(direction);
	}

	/**
	 * Vide la scène.
	 */
	@Override
	public void clearMap() {
		sceneObjects.clear();
		pointToSceneObjectsMap.clear();
	}

	/**
	 * Retourne les codes des objets présents à une position donnée.
	 * 
	 * @param x coordonnée X
	 * @param y coordonnée Y
	 * @param z coordonnée Z
	 * @return ensemble de codes
	 */
	@Override
	public Set<IRenderable3D> getSceneObjectsFromPoint(int x, int y, int z) {
		return pointToSceneObjectsMap.get(new DiscretePoint(x, y, z));
	}

	@Override
	public void removeMap(int x, int y, int z, IRenderable3D obj) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
