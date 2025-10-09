package fr.ubordeaux.ao.mazing.api;

public class BoardGameController {
    private final BoardGameModel model;
    private final BoardGameView view;

    public BoardGameController(BoardGameModel model, BoardGameView view) {
        this.model = model;
        this.view = view;
    }

    public void addCharacter(ICharacter<? extends CharacterMode> character) {
        model.add(character);
        view.repaint();
    }

    public void addTile(int code, int x, int y, int z, float alpha) {
        model.add(code, x, y, z, alpha);
        view.repaint();
    }

    public void clear() {
        model.clear();
        view.repaint();
    }
}
