package fr.ubordeaux.ao.mazing.project2.model;

public class ModelEvent {
    public enum Type {
        PLAYER_POS, PLAYER_SPEED, PLAYER_MODE, WALLS
    }

    public final Type type;
    public final Object data;

    public ModelEvent(Type type, Object data) {
        this.type = type;
        this.data = data;
    }
}
