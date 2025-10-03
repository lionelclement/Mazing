package fr.ubordeaux.ao.mazing.project2.view;

public class ViewEvent {
    public enum Type {
        PLAYER_MOVE, OTHER_ACTION
    }

    public final Type type;
    public final Object data;

    public ViewEvent(Type type, Object data) {
        this.type = type;
        this.data = data;
    }
}
