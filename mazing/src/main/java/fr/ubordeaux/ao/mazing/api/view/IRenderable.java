package fr.ubordeaux.ao.mazing.api.view;

import fr.ubordeaux.ao.mazing.api.RenderType;

import java.awt.Graphics;

/**
 * Interface représentant un objet ou personnage pouvant être rendu en 3D
 * dans l'espace du jeu, avec position et priorité de rendu.
 */
public interface IRenderable {

    /**
     * Retourne la position X du rendu.
     *
     * @return coordonnée X
     */
    float getX();

    /**
     * Retourne la position Y du rendu.
     *
     * @return coordonnée Y
     */
    float getY();

    /**
     * Retourne la position Z du rendu.
     *
     * @return coordonnée Z
     */
    float getZ();

    /**
     * Retourne le type de rendu (sol, objet, personnage, etc.).
     *
     * @return type de rendu
     */
    RenderType getRenderType();

    /**
     * Rend l'objet sur le contexte graphique fourni.
     *
     * @param g contexte graphique dans lequel dessiner l'objet
     */
    void render(Graphics g);

    /**
     * Retourne le rang du rendu pour déterminer l'ordre d'affichage
     * parmi plusieurs objets à la même profondeur.
     *
     * @return rang de priorité de rendu
     */
    int getRank();

    /**
     * Retourne la profondeur isométrique utilisée pour le tri des objets.
     *
     * @return profondeur isométrique
     */
    float getIsoDepth();
}
