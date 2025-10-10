package fr.ubordeaux.ao.mazing.api;

import java.awt.Color;
import java.util.Set;

public interface IWindowGame {

    public void setTitle(String s);
    public void setBackground(Color c);
    public void setResizable(boolean b);
    public void setVisible(boolean b);
    public void scroll(int x, int y);
    
    /** Remplit le plateau avec un fond de tiles */
	void fillTilesBackground(int code, int x, int y, int width, int height, float alpha);

    /** Remplit le plateau avec un fond de tiles */
	void fillTilesBackground(int code, int x, int y, int width, int height);

    /** Remplit le plateau avec un fond en damier */
    void fillCheckerboardBackground(int x, int y, int width, int height);

    /** Ajoute un personnage à la scène */
    void add(ICharacter<? extends CharacterMode> character);

    /** Ajoute un tile ou objet à la scène */
    void add(int code, int x, int y, int z, float alpha);

    /** Ajoute un tile ou objet à la scène */
    void add(int code, int x, int y, int z);

    /** Ajoute une matrice complète de tiles */
    void add(int[][][] matrix);

    void drawTile(Color color, int x, int y, int z);

    /** Vide la scène */
    void clear();

    /**
     * Récupère l'ensemble des objets présents à un point donné de la scène.
     *
     * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @param z Coordonnée Z.
     * @return Ensemble des codes d'objets présents à la position spécifiée.
     */
    Set<Integer> getCodesFromPoint(int x, int y, int z);

    void setSize(int width, int height);

    void setTileSize(int tile_width, int tile_height);

    void setFPS(int fps);


}
