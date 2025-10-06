package fr.ubordeaux.ao.mazing.api.model.board;

import fr.ubordeaux.ao.mazing.api.model.entity.Entity;

/**
 * Interface représentant une vue du plateau de jeu.
 * Elle fournit des méthodes pour gérer les éléments du plateau
 * ainsi que la position et le mouvement du joueur.
 */
public interface IBoardGame {

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
    void assignPlayer(Entity character);

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
     * Ajoute le personnage sur le plateau
     *
     * @param entity le personnage à ajouter
     */
    void addEntity(Entity entity);
}
