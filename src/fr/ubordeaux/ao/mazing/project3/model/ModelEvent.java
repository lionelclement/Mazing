package fr.ubordeaux.ao.mazing.project3.model;

public class ModelEvent {
    public enum Type {
        PLAYER_POSITIONZ, PLAYER_SPEED, CHANGE_MODE, WALLS, MAZE, SPIDER_MODE
    }

    public final Type type;
    public final String attribute;
    public final Object value;

    public ModelEvent(Type type, String attribute, Object value) {
        this.type = type;
        this.attribute = attribute;
        this.value = value;
    }

    public ModelEvent(Type type, Object value) {
        this.type = type;
        this.attribute = null;
        this.value = value;
    }

}
