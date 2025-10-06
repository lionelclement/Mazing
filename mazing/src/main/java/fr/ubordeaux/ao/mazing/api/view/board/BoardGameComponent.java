package fr.ubordeaux.ao.mazing.api.view.board;

import fr.ubordeaux.ao.mazing.api.Loader;
import fr.ubordeaux.ao.mazing.api.RenderType;
import fr.ubordeaux.ao.mazing.api.TileMap;
import fr.ubordeaux.ao.mazing.api.view.IRenderable;
import fr.ubordeaux.ao.mazing.api.view.ImageRenderable3D;
import fr.ubordeaux.ao.mazing.api.view.entity.EntityRenderer;
import fr.ubordeaux.ao.mazing.api.view.entity.Animation;
import fr.ubordeaux.ao.mazing.api.model.board.IBoardGame;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BoardGameComponent extends JPanel {
    private static final long serialVersionUID = 1L;
    private IBoardGame boardGame;
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
    List<IRenderable> sceneObjects = new ArrayList<>();

    /** Liste des personnages présents dans la scène */
    List<EntityRenderer> sceneEntities = new ArrayList<>();

    /** Mapping des coordonnées vers les codes d’objets de scène */
    // Map<DiscretePoint, Set<Integer>> pointToSceneObjectsMap = new HashMap<>();
    Map<DiscretePoint, Set<Integer>> pointToSceneObjectsMap = new HashMap<>();
    /** Mapping du code d’image vers l’objet Image correspondant */
    private Map<Integer, fr.ubordeaux.ao.mazing.api.Image> codeToImageMap;
    /** Boucle de jeu pour mettre à jour les animations */
    private Timer gameLoop;

    public BoardGameComponent(IBoardGame boardGame) {
        this(boardGame, 1024, 800);
    }

    public BoardGameComponent(IBoardGame boardGame, int width, int height) {
        super();
        this.boardGame = boardGame;
        this.width = width;
        this.height = height;

        // Initialisation du TileMap pour le rendu
        TileMap.getInstance(TILE_WIDTH, TILE_HEIGHT, width, height, 8, 8);
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

    public IBoardGame getBoardGame() {
        return boardGame;
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

            codeToImageMap.put(i + offset, new fr.ubordeaux.ao.mazing.api.Image(scaled, offsetX, offsetY, renderType));

            col++;
            if (col == colsPerRow) {
                col = 0;
                row++;
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dessine le fond pré-généré
        if (this.background != null) {
            g.drawImage(this.background, 0, 0, null);
        }

        // Centre de l'écran pour le rendu
        g.translate(this.width / 2, TILE_HEIGHT);

        List<IRenderable> renderableScene = new ArrayList<>(sceneObjects.size() + sceneEntities.size());
        renderableScene.addAll(sceneObjects);
        renderableScene.addAll(sceneEntities);
        renderScene(g, renderableScene);

    }

    private void renderScene(Graphics g, List<IRenderable> sceneObjects) {
        // Trier les objets par z, puis x+y, puis par type
        List<IRenderable> sorted = sceneObjects.stream()
                .sorted(Comparator
                        .comparingInt(o -> ((int) ((IRenderable) o).getRenderZ()))
                        .thenComparingInt(o -> ((int) ((IRenderable) o).getIsoDepth()))
                        .thenComparingInt(o -> ((IRenderable) o).getRenderType().ordinal())
                        .thenComparingInt(o -> ((IRenderable) o).getRank())
                        .thenComparing(o -> ((IRenderable) o).getIsoDepth()))
                .toList();

        for (IRenderable obj : sorted) {
            obj.render(g);
        }
    }


    /**
     * Met à jour toutes les animations des personnages.
     */
    private void updateAnimations() {
        for (EntityRenderer e : this.sceneEntities) {
            Animation animation = e.getCurrentAnimation();
            animation.tick(e);
        }
        repaint();
    }
    /**
     * Remplit l'arrière-plan du plateau avec un tile spécifique.
     *
     * @param code  Le code du tile à utiliser pour l'arrière-plan.
     * @param alpha La transparence à appliquer (0.0 = transparent, 1.0 = opaque).
     */
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

        fr.ubordeaux.ao.mazing.api.Image tileImg = codeToImageMap.get(code);
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
     * Remplit l'arrière-plan du plateau avec un motif en damier.
     *
     * @param size La taille des carreaux du damier.
     */
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
     * Ajoute un personnage à la scène.
     *
     * @param entity Le personnage à ajouter.
     */
    public void addToSceneMap(EntityRenderer entity) {
        boardGame.addEntity(entity.getEntity());
        sceneEntities.add(entity);
    }

    /**
     * Ajoute un tile à la scène à une position donnée.
     *
     * @param code  Code du tile.
     * @param x     Coordonnée X sur la scène.
     * @param y     Coordonnée Y sur la scène.
     * @param z     Coordonnée Z sur la scène.
     * @param alpha Transparence du tile (0.0 = transparent, 1.0 = opaque).
     */
    public void addToSceneMap(int code, int x, int y, int z, float alpha) {
        if (code != -1) {
            fr.ubordeaux.ao.mazing.api.Image image = codeToImageMap.get(code);
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
                        .add(code);
            }
        }
    }

    /**
     * Ajoute une une matrice complète de tuiles à la scène.
     *
     * @param matrix Tableau 3D représentant les tiles et leurs positions.
     */
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
                        fr.ubordeaux.ao.mazing.api.Image image = codeToImageMap.get(code);
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
                                    .add(code);

                        }

                    }

                }
            }
        }
    }
    /**
     * Efface tous les éléments de la scène.
     */
    public void clearMap() {
        sceneObjects.clear();
        pointToSceneObjectsMap.clear();
    }

    /**
     * Récupère l'ensemble des objets présents à un point donné de la scène.
     *
     * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @param z Coordonnée Z.
     * @return Ensemble des codes d'objets présents à la position spécifiée.
     */
    public Set<Integer> getSceneObjectsFromPoint(int x, int y, int z) {
        return pointToSceneObjectsMap.get(new DiscretePoint(x, y, z));
    }

    public List<IRenderable> getSceneObjects() {
        return this.sceneObjects;
    }

    public List<EntityRenderer> getSceneEntities() {
        return this.sceneEntities;
    }

    public Image getBackgroundImage() {
        return background;
    }

    public int getWidth() {
        return width;
    }

    public int getTileHeight() {
        return TILE_HEIGHT;
    }
}
