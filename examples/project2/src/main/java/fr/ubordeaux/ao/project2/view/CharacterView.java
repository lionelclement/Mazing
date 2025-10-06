package fr.ubordeaux.ao.project2.view;

import fr.ubordeaux.ao.mazing.api.entity.Entity;
import fr.ubordeaux.ao.mazing.api.board.IBoardGame;

import java.util.HashSet;
import java.util.Set;

public class CharacterView {

    private final String name;
    private final Entity character;
    private final IBoardGame boardGame;
    private final ViewListener viewListener;
    private boolean gothit;

    private final Set<Integer> eastBarrier;
    private final Set<Integer> westBarrier;
    private final Set<Integer> northBarrier;
    private final Set<Integer> southBarrier;

    public CharacterView(String name, Entity character, IBoardGame boardGame, ViewListener listener,
                         Set<Integer> east, Set<Integer> west, Set<Integer> north, Set<Integer> south) {
        this.name = name;
        this.character = character;
        this.boardGame = boardGame;
        this.viewListener = listener;
        this.eastBarrier = east;
        this.westBarrier = west;
        this.northBarrier = north;
        this.southBarrier = south;

        init();
    }

    private void init() {
        // Tick trigger : collision et objets spéciaux autour
        character.setTickAnimationCallback(() -> {
            handleSpecialTiles();
            handleCollisions();
        });

        // Begin trigger : stop si GOTHIT
        character.setBeginAnimationCallback(() -> {
            if (character.getCurrentMode().toString().equals("GOTHIT")) {
                // character.changeMode("GOTHIT");
                character.setSpeed(0, 0, 0);
                gothit = true;
            }
        });

        // End trigger : remettre à IDLE
        character.setEndAnimationCallback(() -> {
            if (gothit) {
                gothit = false;
                character.setSpeed(0, 0, 0);
                character.changeMode("IDLE");
            }
        });

        // Mid & Loop triggers peuvent être définis si nécessaire
        character.setMidAnimationCallback(() -> {
        });

        character.setLoopAnimationCallback(() -> {
        });

        boardGame.addToSceneMap(character);
    }

    private void handleSpecialTiles() {
        Set<Integer> set = new HashSet<>();
        int cx = (int) character.getX();
        int cy = (int) character.getY();
        int cz = (int) character.getZ();

        // On regarde autour de (-1, 0, +1)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Set<Integer> objects = boardGame.getSceneObjectsFromPoint(cx + dx, cy + dy, cz);
                if (objects != null)
                    set.addAll(objects);
            }
        }

        viewListener.onViewEvent(new ViewEvent(ViewEvent.Type.ON_STEPS, name,
                set.contains(308)));
        viewListener.onViewEvent(new ViewEvent(ViewEvent.Type.ON_TRAPDOOR, name,
                set.contains(76)));
    }

    private void handleCollisions() {
        Set<Integer> set = (Set<Integer>) boardGame.getSceneObjectsFromPoint(
                (int) character.getX(), (int) character.getY(), (int) character.getZ());
        if (set == null || set.isEmpty())
            return;

        // Pour chaque direction, copie et intersection avec les barrières
        checkBarrier(set, southBarrier, 0, 1, 0.2f);
        checkBarrier(set, northBarrier, 0, -1, 0.1f);
        checkBarrier(set, eastBarrier, 1, 0, 0.2f);
        checkBarrier(set, westBarrier, -1, 0, 0.1f);
    }

    private void checkBarrier(Set<Integer> renderables, Set<Integer> barrier, int dx, int dy, float pushBack) {
        // Tester l'intersection entre les deux ensembles
        Set<Integer> copy = new HashSet<>(renderables);
        copy.retainAll(barrier); // garde uniquement les éléments communs

        if (!copy.isEmpty()) { // collision détectée
            if ((dx != 0 && character.getSpeedX() * dx > 0 && character.getX() % 1 < 0.1f) ||
                    (dy != 0 && character.getSpeedY() * dy > 0 && character.getY() % 1 < 0.1f)) {
                // on repousse légèrement le personnage
                character.setPosition(character.getX() - dx * pushBack, character.getY() - dy * pushBack,
                        character.getZ());
                character.setSpeed(0, 0, 0);
                viewListener.onViewEvent(new ViewEvent(ViewEvent.Type.GOTHIT, name, null));
            }
        }
    }

    void updateMode(String value) {
        character.changeMode(value);
    }

    @Override
    public String toString() {
        return name;
    }

}
