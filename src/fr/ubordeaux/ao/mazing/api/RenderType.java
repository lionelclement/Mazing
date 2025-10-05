package fr.ubordeaux.ao.mazing.api;

/**
 * Enumération représentant le type de rendu pour un objet dans le jeu.
 * 
 * Les types disponibles sont :
 * 
 * - {@link #FLOOR} : utilisé pour les éléments du sol.
 * - {@link #OBJECT} : utilisé pour les objets ou personnages à dessiner
 * par-dessus le sol.
 * 
 * 
 */
public enum RenderType {
    FLOOR, OBJECT
}
