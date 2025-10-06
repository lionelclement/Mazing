package fr.ubordeaux.ao.mazing.api.view;

import fr.ubordeaux.ao.mazing.api.entity.Animation;
import fr.ubordeaux.ao.mazing.api.board.IBoardGame;
import fr.ubordeaux.ao.mazing.api.entity.Entity;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BoardGameComponent extends JPanel {
    private static final long serialVersionUID = 1L;
    private IBoardGame boardGame;
    /** Boucle de jeu pour mettre à jour les animations */
    private Timer gameLoop;

    public BoardGameComponent(IBoardGame boardGame) {
        super();
        this.boardGame = boardGame;
        // Timer de mise à jour des animations (40 fps)
        gameLoop = new Timer(1000 / 40, _ -> {
            updateAnimations();
        });
        gameLoop.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dessine le fond pré-généré
        if (boardGame.getBackgroundImage() != null) {
            g.drawImage(boardGame.getBackgroundImage(), 0, 0, null);
        }

        // Centre de l'écran pour le rendu
        g.translate(boardGame.getWidth() / 2, boardGame.getTileHeight());

        List<IRenderable> renderableScene = new ArrayList<>(boardGame.getSceneObjects().size() + boardGame.getSceneEntities().size());
        renderableScene.addAll(boardGame.getSceneObjects());
        renderableScene.addAll(boardGame.getSceneEntities());
        renderScene(g, renderableScene);

    }

    private void renderScene(Graphics g, List<IRenderable> sceneObjects) {
        // Trier les objets par z, puis x+y, puis par type
        List<IRenderable> sorted = sceneObjects.stream()
                .sorted(Comparator
                        .comparingInt(o -> ((int) ((IRenderable) o).getZ()))
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
        for (Entity c : boardGame.getEntities()) {
            Animation animation = c.getCurrentAnimation();
            animation.tick(c);
        }
        repaint();
    }
}
