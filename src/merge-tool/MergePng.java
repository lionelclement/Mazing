package merge;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergePng extends Application {

    private static final int TARGET_WIDTH = 256; // largeur d'une cellule
    private static final int TARGET_HEIGHT = 512; // hauteur d'une cellule
    private static final int IMAGES_PER_ROW = 10; // nb d'images par ligne
    private static final int LINES_PER_ATLAS = 10; // nb de lignes par atlas

    @Override
    public void start(Stage stage) {
        File dir = new File("png");
        if (!dir.exists() || !dir.isDirectory()) {
            System.err.println("Dossier 'png' introuvable !");
            System.exit(1);
        }

        // Lister et trier les PNG
        List<File> files = new ArrayList<>();
        for (File f : dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"))) {
            files.add(f);
        }
        files.sort((a, b) -> a.getName().compareTo(b.getName()));

        if (files.isEmpty()) {
            System.err.println("Aucun fichier PNG trouvé !");
            Platform.exit();
            return;
        }

        int maxImagesPerAtlas = IMAGES_PER_ROW * LINES_PER_ATLAS;
        int atlasCount = (int) Math.ceil(files.size() / (double) maxImagesPerAtlas);

        System.out.println("Total fichiers : " + files.size());
        System.out.println("Nombre d'atlas générés : " + atlasCount);

        for (int atlasIndex = 0; atlasIndex < atlasCount; atlasIndex++) {
            int start = atlasIndex * maxImagesPerAtlas;
            int end = Math.min(start + maxImagesPerAtlas, files.size());

            int atlasRows = LINES_PER_ATLAS;
            int canvasWidth = IMAGES_PER_ROW * TARGET_WIDTH;
            int canvasHeight = atlasRows * TARGET_HEIGHT;

            Canvas canvas = new Canvas(canvasWidth, canvasHeight);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvasWidth, canvasHeight);

            int x = 0, y = 0;
            for (int i = start; i < end; i++) {
                File f = files.get(i);
                Image img = new Image(f.toURI().toString());

                double imgWidth = img.getWidth();
                double imgHeight = img.getHeight(); // <-- corrected
                double scale = Math.min(TARGET_WIDTH / imgWidth, TARGET_HEIGHT / imgHeight);

                double newWidth = imgWidth * scale;
                double newHeight = imgHeight * scale;

                double dstX = x;
                double dstY = y;

                gc.drawImage(img, dstX, dstY, newWidth, newHeight);

                x += TARGET_WIDTH;
                if (x >= IMAGES_PER_ROW * TARGET_WIDTH) {
                    x = 0;
                    y += TARGET_HEIGHT;
                }
            }

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            WritableImage snapshot = new WritableImage(canvasWidth, canvasHeight);
            canvas.snapshot(params, snapshot);

            try {
                File outFile = new File("all_" + atlasIndex + ".png");
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", outFile);
                System.out.println("Image générée : " + outFile.getName() +
                        " (" + canvasWidth + "x" + canvasHeight + ")");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}