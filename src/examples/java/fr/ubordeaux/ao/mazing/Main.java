
package fr.ubordeaux.ao.mazing;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.entity.AnimatedEntity;
import fr.ubordeaux.ao.mazing.view.entity.FloorTile;
import fr.ubordeaux.ao.mazing.view.entity.ShadowMode;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedSprites;
import fr.ubordeaux.ao.mazing.view.sprite.IsometricSprites;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Exemple minimal : un sol en pierre et un personnage animé.
 */
public final class Main {

    private static final int SCENE_SIZE = 10;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showScene);
    }

    private static void showScene() {

        // Création d'une scène automatiquement centrée.
        Scene scene = new Scene(
                800, 600,
                SCENE_SIZE, SCENE_SIZE);

        // Ajout du sol.
        for (int x = 0; x < SCENE_SIZE; x++) {
            for (int y = 0; y < SCENE_SIZE; y++) {
                scene.addRenderable(new FloorTile(
                        IsometricSprites.STONE,
                        Orientation.NORTH,
                        x, y, 0));
            }
        }

        // Cubes de débogage.
        for (int x = 0; x < SCENE_SIZE; x++) {
            for (int y = 0; y < SCENE_SIZE; y++) {
                scene.addDebugCube(x, y, 0);
            }
        }

        // Personnage animé.
        scene.addRenderable(new AnimatedEntity(
                AnimatedSprites.CRUSADER_IDLE,
                Orientation.SOUTH,
                2, 2, 0, 2,
                ShadowMode.WITH_SHADOW));

        scene.startAnimation(24);
		
		// Affichage sans manipulation de JFrame.
        scene.show();

        // Animation.
        new Timer(1000 / 24, event -> {
            scene.animationTick();
            scene.repaint();
        }).start();
    }
}
