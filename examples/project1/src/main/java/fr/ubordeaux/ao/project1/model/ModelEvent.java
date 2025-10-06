package fr.ubordeaux.ao.project1.model;

public class ModelEvent {
    public enum Type {
        PLAYER_SPEED, PLAYER_MODE
    }

    public final Type type;
    public final Object data;

    public ModelEvent(Type type, Object data) {
        this.type = type;
        this.data = data;
    }
}
