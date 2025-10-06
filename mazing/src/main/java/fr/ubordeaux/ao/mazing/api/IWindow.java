package fr.ubordeaux.ao.mazing.api;

/**
 * Interface représentant une fenêtre graphique du jeu.
 * 
 * Fournit un accès au plateau de jeu (IBoardGame) affiché dans cette fenêtre.
 * Cette abstraction permet de découpler la logique de jeu de l'implémentation
 * concrète de la fenêtre graphique (ex : JFrame, JPanel contenant le board,
 * etc.).
 * 
 */
public interface IWindow {

    /**
     * Retourne le plateau de jeu associé à cette fenêtre.
     *
     * @return l'instance d'IBoardGame affichée dans cette fenêtre.
     */
    IBoardGame getBoardGame();

}
