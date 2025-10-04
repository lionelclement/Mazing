package fr.ubordeaux.ao.mazing.project2.view;

public class ViewEvent {
    public enum Type {
        PLAYER_GOTHIT, PLAYER_ON_STEPS, PLAYER_ON_TRAPDOOR
    }

    public final Type type;
    public final Object data;

    public ViewEvent(Type type, Object data) {
        this.type = type;
        this.data = data;
    }
}
