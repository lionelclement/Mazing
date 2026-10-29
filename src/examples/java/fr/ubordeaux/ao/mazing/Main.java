package fr.ubordeaux.ao.mazing;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.entity.AnimatedEntity;
import fr.ubordeaux.ao.mazing.view.entity.FloorTile;
import fr.ubordeaux.ao.mazing.view.entity.OccludingEntity;
import fr.ubordeaux.ao.mazing.view.entity.ShadowMode;
import fr.ubordeaux.ao.mazing.view.renderer.IsometricRenderer;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedSprites;
import fr.ubordeaux.ao.mazing.view.sprite.IsometricSprites;
import fr.ubordeaux.ao.mazing.view.swing.ViewPanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Exemple minimal : un sol en pierre et un personnage animé.
 */
public final class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(Main::showScene);
	}

	private static void showScene() {
		IsometricRenderer renderer = new IsometricRenderer(150, 320);
		renderer.setDebug(true);
		ViewPanel scene = new ViewPanel(800, 600, renderer);

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				scene.addRenderable(new FloorTile(
						IsometricSprites.STONE, Orientation.NORTH, i, j, 0));
			}
		}

		// Cube de debug bleu : exactement une case 3D, au centre du sol 5 x 5.
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 5; y++) {
				for (int z = 0; z < 1; z++) {
					scene.addDebugCube(x, y, z);
				}
			}
		}
		// Place le personnage au centre de la scène.
		scene.addRenderable(new AnimatedEntity(
				AnimatedSprites.CRUSADER_IDLE, Orientation.SOUTH,
				2, 2, 0, 2, ShadowMode.WITH_SHADOW));

		JFrame window = new JFrame("Mazing");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(scene);
		window.setSize(800, 600);
		window.setLocationRelativeTo(null);
		window.setVisible(true);

		new Timer(1000 / 24, event -> {
			scene.animationTick();
			scene.repaint();
		}).start();
	}
}
