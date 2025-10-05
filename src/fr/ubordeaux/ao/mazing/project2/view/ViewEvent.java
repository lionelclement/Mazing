package fr.ubordeaux.ao.mazing.project2.view;

public class ViewEvent {
    public enum Type {
        GOTHIT, ON_STEPS, ON_TRAPDOOR
    }

    public final Type type;
    public final String attribute;
    public final Object value;

    public ViewEvent(Type type, String attribute, Object value) {
        this.type = type;
        this.attribute = attribute;
        this.value = value;
    }
}
