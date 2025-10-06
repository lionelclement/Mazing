package fr.ubordeaux.ao.mazing.project2.model;

public interface IMazingModel {
    void changeMode(String characterId, Mode mode);

    void setPlayerSpeed(double dx, double dy, double dz);

    void mazeGenerate();

    void changePlayerMode(Mode walk);

    void setPlayerPositionZ(double f);

    double getPlayerPositionZ();

    boolean isPlayerOnSteps();

    boolean isPlayerOnTrapdoor();

    int getMazeDepth();

}
