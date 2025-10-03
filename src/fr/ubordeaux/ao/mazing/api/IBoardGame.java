package fr.ubordeaux.ao.mazing.api;

public interface IBoardGame {

    void fillTilesBackground(int code, float alpha);

    void fillCheckerboardBackground(int size);

    float getPlayerX();

    float getPlayerZ();

    float getPlayerY();

    void stopPlayer();

    void assignPlayer(Character<? extends CharacterMode> character);

    void updatePlayerPosition(float x, float y, float f);

    void updatePlayerSpeed(float vx, float vy, float vz);

    void updatePlayerMode(String mode);

    void updatePlayerDirection(String direction);

    void addToSceneMap(Character<?> character);

    void addToSceneMap(int code, int x, int y, int z, float alpha);

    void addToSceneMap(int[][][] content);

    void clearMap();

}
