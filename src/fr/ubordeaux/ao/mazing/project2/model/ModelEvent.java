package fr.ubordeaux.ao.mazing.project2.model;

public class ModelEvent {
    public enum Type {
        PLAYER_POS, PLAYER_SPEED, PLAYER_MODE, WALLS, MAZE
    }

    public final Type type;
    public final Object data;
    public Object addon = null;

    public ModelEvent(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    public ModelEvent(Type type, Object data, Object addon) {
        this.type = type;
        this.data = data;
        this.addon = addon;
    }
}
