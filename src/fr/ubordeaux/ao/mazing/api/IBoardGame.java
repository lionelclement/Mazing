package fr.ubordeaux.ao.mazing.api;

import java.util.Set;

/**
 * Interface représentant une vue du plateau de jeu.
 * Elle fournit des méthodes pour gérer les éléments du plateau,
 * la position et le mouvement du joueur, ainsi que l'affichage de la scène.
 */
public interface IBoardGame {

    /**
     * Remplit l'arrière-plan du plateau avec un tile spécifique.
     *
     * @param code  Le code du tile à utiliser pour l'arrière-plan.
     * @param alpha La transparence à appliquer (0.0 = transparent, 1.0 = opaque).
     */
    void fillTilesBackground(int code, float alpha);

    /**
     * Remplit l'arrière-plan du plateau avec un motif en damier.
     *
     * @param size La taille des carreaux du damier.
     */
    void fillCheckerboardBackground(int size);

    /**
     * Retourne la position X actuelle du joueur.
     *
     * @return La coordonnée X du joueur.
     */
    float getPlayerX();

    /**
     * Retourne la position Z actuelle du joueur (hauteur).
     *
     * @return La coordonnée Z du joueur.
     */
    float getPlayerZ();

    /**
     * Retourne la position Y actuelle du joueur.
     *
     * @return La coordonnée Y du joueur.
     */
    float getPlayerY();

    /**
     * Stoppe le mouvement du joueur.
     */
    void stopPlayer();

    /**
     * Assigne un personnage au joueur.
     *
     * @param character Le personnage à associer au joueur.
     */
    void assignPlayer(Character<? extends CharacterMode> character);

    /**
     * Met à jour la position du joueur sur le plateau.
     *
     * @param x La nouvelle coordonnée X.
     * @param y La nouvelle coordonnée Y.
     * @param z La nouvelle coordonnée Z.
     */
    void updatePlayerPosition(float x, float y, float z);

    /**
     * Met à jour la vitesse du joueur.
     *
     * @param vx La vitesse selon X.
     * @param vy La vitesse selon Y.
     * @param vz La vitesse selon Z.
     */
    void updatePlayerSpeed(float vx, float vy, float vz);

    /**
     * Met à jour le mode actuel du joueur.
     *
     * @param mode Nom du mode à appliquer.
     */
    void updatePlayerMode(String mode);

    /**
     * Met à jour la direction du joueur.
     *
     * @param direction La direction à appliquer (ex: "NORTH", "SOUTH").
     */
    void updatePlayerDirection(String direction);

    /**
     * Ajoute un personnage à la scène.
     *
     * @param character Le personnage à ajouter.
     */
    void addToSceneMap(Character<?> character);

    /**
     * Ajoute un tile à la scène à une position donnée.
     *
     * @param code  Code du tile.
     * @param x     Coordonnée X sur la scène.
     * @param y     Coordonnée Y sur la scène.
     * @param z     Coordonnée Z sur la scène.
     * @param alpha Transparence du tile (0.0 = transparent, 1.0 = opaque).
     */
    void addToSceneMap(int code, int x, int y, int z, float alpha);

    /**
     * Ajoute une grille de tiles à la scène.
     *
     * @param content Tableau 3D représentant les tiles et leurs positions.
     */
    void addToSceneMap(int[][][] content);

    /**
     * Efface tous les éléments de la scène.
     */
    void clearMap();

    /**
     * Récupère l'ensemble des objets présents à un point donné de la scène.
     *
     * @param x Coordonnée X.
     * @param y Coordonnée Y.
     * @param z Coordonnée Z.
     * @return Ensemble des codes d'objets présents à la position spécifiée.
     */
    Set<Integer> getSceneObjectsFromPoint(int x, int y, int z);
}
