package fr.ubordeaux.ao.mazing.api;

public interface IBoardGame {

    void fillTilesBackground(int code);

    void fillCheckerboardBackground(int size);

    float getPlayerX();

    float getPlayerZ();

    float getPlayerY();

    void stopPlayer();

    void assignPlayer(Character<? extends CharacterMode> character);

    void setPlayerPosition(float x, float y, float f);

    void setPlayerSpeed(float vx, float vy, float vz);

    void changePlayerMode(String mode);

    void changePlayerDirection(String direction);

    void addToSceneMap(Character<?> character);

    void addToSceneMap(int code, int x, int y, int z);

    void addToSceneMap(int[][][] content);

}
