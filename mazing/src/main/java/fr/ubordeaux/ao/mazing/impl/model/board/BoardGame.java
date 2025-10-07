package fr.ubordeaux.ao.mazing.impl.model.board;

import fr.ubordeaux.ao.mazing.api.model.board.IBoardGame;
import fr.ubordeaux.ao.mazing.api.model.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente le plateau de jeu isométrique et gère le rendu des tuiles, murs et
 * personnages.
 * 
 * Cette classe hérite de {@link JPanel} et implémente {@link IBoardGame}. Elle
 * gère :
 * 
 * - Le rendu des objets 3D et des personnages.
 * - Le fond du plateau et les textures des tuiles.
 * - Les animations des personnages.
 * - La gestion des positions et états du joueur.
 * 
 * 
 */
public class BoardGame implements IBoardGame {

	public static final int WIDTH = 1366;
	public static final int HEIGHT = 738;

	/** Largeur du plateau */
	private final int width;

	/** Hauteur du plateau */
	private final int height;


	/** Liste des personnages du plateau */
	private List<Entity> entities;


	/** Référence vers le joueur */
	private Entity player;

	/**
	 * Constructeur principal.
	 * 
	 * @param width  largeur du plateau en pixels
	 * @param height hauteur du plateau en pixels
	 */
	public BoardGame(int width, int height) {
		this.width = width;
		this.height = height;
		this.entities = new ArrayList<>();
	}

	/**
	 * Constructeur par défaut avec taille 1024x800.
	 */
	public BoardGame() {
		this(WIDTH, HEIGHT);
	}

	/**
	 * Met à jour la position du joueur.
	 * 
	 * @param x coordonnée X
	 * @param y coordonnée Y
	 * @param z coordonnée Z
	 */
	@Override
	public void updatePlayerPosition(float x, float y, float z) {
		player.setPosition(x, y, z);
	}

	/**
	 * Retourne le joueur actuel.
	 * 
	 * @return joueur
	 */
	public Entity getPlayer() {
		return player;
	}

	/**
	 * Retourne la coordonnée X du joueur.
	 */
	@Override
	public float getPlayerX() {
		return player.getX();
	}

	/**
	 * Retourne la coordonnée Y du joueur.
	 */
	@Override
	public float getPlayerY() {
		return player.getY();
	}

	/**
	 * Retourne la coordonnée Z du joueur.
	 */
	@Override
	public float getPlayerZ() {
		return player.getZ();
	}

	/**
	 * Met à jour la vitesse du joueur.
	 */
	@Override
	public void updatePlayerSpeed(float vx, float vy, float vz) {
		player.setSpeed(vx, vy, vz);
	}

	/**
	 * Stoppe le joueur.
	 */
	@Override
	public void stopPlayer() {
		player.stop();
	}

	/**
	 * Assigne le joueur.
	 */
	@Override
	public void assignPlayer(Entity player) {
		this.player = player;
	}

	/**
	 * Change le mode du joueur.
	 */
	@Override
	public void updatePlayerMode(String state) {
		player.changeMode(state);
	}

	/**
	 * Change la direction du joueur.
	 */
	@Override
	public void updatePlayerDirection(String direction) {
		player.changeDirection(direction);
	}

	public List<Entity> getEntities() {
		return this.entities;
	}

	public int getWidth() {
		return width;
	}

	@Override
	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}
}
