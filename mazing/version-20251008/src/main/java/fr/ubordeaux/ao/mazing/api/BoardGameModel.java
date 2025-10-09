package fr.ubordeaux.ao.mazing.api;

import java.util.HashSet;
import java.util.Set;

public class BoardGameModel {
    private final int width;
    private final int height;

    // Exemple de données : matrice 3D représentant les tiles
    private int[][][] tiles;

    public BoardGameModel(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new int[width][height][10]; // 10 = profondeur max, exemple
    }

    public void fillTilesBackground(int code, float alpha) {
        // Logique pour remplir le plateau
    }

    public void fillCheckerboardBackground(int size) {
        // Logique pour remplir damier
    }

    public void add(ICharacter<? extends CharacterMode> character) {
        // Ajouter personnage au modèle
    }

    public void add(int code, int x, int y, int z, float alpha) {
        tiles[x][y][z] = code;
    }

    public void add(int[][][] matrix) {
        this.tiles = matrix;
    }

    public void clear() {
        this.tiles = new int[width][height][10];
    }

    public Set<Integer> getCodesFromPoint(int x, int y, int z) {
        Set<Integer> codes = new HashSet<>();
        codes.add(tiles[x][y][z]);
        return codes;
    }

    public int[][][] getTiles() {
        return tiles;
    }
}
