package fr.ubordeaux.ao.mazing.api;

import java.awt.Color;

/**
 * Interface générique représentant une fenêtre de jeu (2D ou isométrique).
 * 
 * 
 * Cette interface fournit un ensemble de méthodes pour :
 * 
 *   -configurer la fenêtre du jeu (titre, taille, fond...)</li>
 *   -afficher et manipuler des tuiles (tiles) ou objets</li>
 *   -ajouter des personnages animés</li>
 *   -gérer le défilement (scrolling) et le taux de rafraîchissement (FPS)</li>
 * 
 * 
 * Elle sert de contrat entre la logique du jeu (modèle/contrôleur)
 * et le moteur d’affichage graphique.
 * 
 */
public interface IWindowGame {

    // =========================
    // 🎨 CONFIGURATION GÉNÉRALE
    // =========================

    /**
     * Définit si la fenêtre doit être visible à l’écran.
     *
     * @param b true pour afficher la fenêtre, false pour la masquer
     */
    void setVisible(boolean b);

    /**
     * Définit la taille des tuiles utilisées pour le rendu.
     *
     * @param tile_width  largeur d’une tuile (en pixels)
     * @param tile_height hauteur d’une tuile (en pixels)
     */
    void setTileSize(int tile_width, int tile_height);

    /**
     * Définit le nombre d’images par seconde (FPS) pour les animations.
     *
     * @param fps fréquence d’affichage souhaitée (ex. 60 pour 60 FPS)
     */
    void setFPS(int fps);


    // =========================
    // 🧭 DÉPLACEMENT / SCROLL
    // =========================

    /**
     * Fait défiler la scène selon un décalage donné.
     *
     * @param x déplacement horizontal du point d’origine (positif = droite)
     * @param y déplacement vertical du point d’origine (positif = bas)
     */
    void scroll(int x, int y);


    // =========================
    // 🧱 GESTION DU PLATEAU
    // =========================

    /**
     * Remplit la scène avec un fond uniforme de tuiles.
     *
     * @param code   identifiant de la tuile à utiliser
     * @param x      position de départ en X (grille)
     * @param y      position de départ en Y (grille)
     * @param width  largeur en nombre de tuiles
     * @param height hauteur en nombre de tuiles
     * @param alpha  transparence (0.0 = transparent, 1.0 = opaque)
     */
    void fillTilesBackground(int code, int x, int y, int width, int height, float alpha);

    /**
     * Remplit la scène avec un fond uniforme de tuiles (opaque).
     *
     * @param code   identifiant de la tuile à utiliser
     * @param x      position de départ en X (grille)
     * @param y      position de départ en Y (grille)
     * @param width  largeur en nombre de tuiles
     * @param height hauteur en nombre de tuiles
     */
    void fillTilesBackground(int code, int x, int y, int width, int height);

    /**
     * Remplit la scène avec un fond en damier.
     *
     * @param x      position de départ en X
     * @param y      position de départ en Y
     * @param width  largeur en nombre de tuiles
     * @param height hauteur en nombre de tuiles
     */
    void fillCheckerboardBackground(int x, int y, int width, int height);

    /**
     * Vide complètement la scène (tuiles et objets).
     */
    void clear();


    // =========================
    // 👾 AJOUT D’OBJETS / TUILES
    // =========================

    /**
     * Ajoute un personnage (ex. {@link Crusader}, {@link Spider}, etc.) à la scène.
     *
     * @param character l’objet personnage à ajouter
     */
    void add(ICharacter<? extends ICharacterMode> character);

    /**
     * Ajoute un élément (tuile, décor, objet) à une position donnée avec transparence.
     *
     * @param code  identifiant de la tuile ou de l’objet
     * @param x     coordonnée X sur la grille
     * @param y     coordonnée Y sur la grille
     * @param z     hauteur (niveau)
     * @param alpha transparence (0.0 = invisible, 1.0 = opaque)
     */
    void add(int code, int x, int y, int z, float alpha);

    /**
     * Ajoute un élément (tuile, décor, objet) opaque à la scène.
     *
     * @param code identifiant de la tuile ou de l’objet
     * @param x    coordonnée X sur la grille
     * @param y    coordonnée Y sur la grille
     * @param z    hauteur (niveau)
     */
    void add(int code, int x, int y, int z);

    /**
     * Ajoute un ensemble complet de tuiles (matrice 3D).
     *
     * @param matrix matrice [x][y][z] représentant le plateau
     */
    void add(int[][][] matrix);


    /**
     * Ajoute un ensemble complet de tuiles (matrice 3D).
     *
     * @param matrix matrice [x][y][z] représentant le plateau
     * @param alpha transparence (0.0 = invisible, 1.0 = opaque)
     */
    void add(int[][][] matrix, float alpha);


    // =========================
    // ✏️ OUTILS DE DESSIN
    // =========================

    /**
     * Dessine une tuile unie d’une certaine couleur aux coordonnées indiquées.
     *
     * @param color couleur à dessiner
     * @param x     coordonnée X sur la grille
     * @param y     coordonnée Y sur la grille
     * @param z     hauteur (niveau)
     */
    void addTileBackground(Color color, int x, int y, int z);

    /**
     * Dessine un cube de la taille d'une tuile uni d’une certaine couleur aux coordonnées indiquées.
     *
     * @param color couleur à dessiner
     * @param x     coordonnée X sur la grille
     * @param y     coordonnée Y sur la grille
     * @param z     hauteur (niveau)
     */
    void addCubeBackground(Color color, int x, int y, int z);


}
