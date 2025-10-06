package fr.ubordeaux.ao.project2.model;

public class CharacterModel {

    private final String name;
    private Point position = new Point();
    private Point speed = new Point();
    private String currentMode;
    private Boolean onSteps;
    private Boolean onTrapdoor;

    public CharacterModel(String name) {
        this.name = name;
    }

    public Point getPosition() {
        return position;
    }

    public Point getSpeed() {
        return speed;
    }

    public String getCurrentMode() {
        return currentMode;
    }

    public Boolean getOnSteps() {
        return onSteps;
    }

    public Boolean getOnTrapdoor() {
        return onTrapdoor;
    }

    public void setCurrentMode(String currentMode) {
        this.currentMode = currentMode;
    }

    public void setOnSteps(Boolean onSteps) {
        this.onSteps = onSteps;
    }

    public void setOnTrapdoor(Boolean onTrapdoor) {
        this.onTrapdoor = onTrapdoor;
    }

    @Override
    public String toString() {
        return name;
    }

}
