/**
 * Package principal de l'application Bomberman.
 */
package org.bomberman;


import org.bomberman.entite.Bonus;

import java.util.ArrayList;
import java.util.List;
/**
 * Gère la grille de jeu et les bonus actifs.
 */
public class Game {

    /**Largeur de la grille*/
    private static final int WIDTH = 13;
    /**Hauteur de la grille*/
    private static final int HEIGHT = 11;
    /** Grille du jeu*/
    private int[][] grid;
    /** Liste pour les bonus actifs*/
    private List<Bonus> activeBonuses;

    /**
     * Constructeur de la classe Game.
     * Initialise la grille de jeu et la liste des bonus actifs.
     */
    public Game() {
        grid = new int[HEIGHT][WIDTH];
        activeBonuses = new ArrayList<>();
        initGrid();
    }

    /**
     * Initialise la grille du jeu avec des valeurs.
     * 0 = vide, 1 = mur, 2 = mur destructible.
     * Les zones de "spawn" des joueurs sont laissées vides.
     */
    private void initGrid() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (isSpawnZone(i, j)) {
                    grid[i][j] = 0;
                    continue;
                }

                if (i % 2 == 1 && j % 2 == 1 && j != WIDTH - 1 && i != HEIGHT - 1) {
                    grid[i][j] = 1;
                } else if (Math.random() < 0.5) {
                    grid[i][j] = 2;
                } else {
                    grid[i][j] = 0;  // Espaces vides
                }
            }
        }

    }

    /**
     * Vérifie si une position donnée correspond à une zone de "spawn" pour les joueurs.
     * Les zones de spawn sont les coins de la grille.
     *
     * @param ligne La ligne de la case à vérifier.
     * @param colonne La colonne de la case à vérifier.
     * @return true si la case est une zone de spawn, false sinon.
     */
    private boolean isSpawnZone(int ligne, int colonne) {
        return (ligne <= 1 && colonne <= 1) || // coin haut-gauche
                (ligne <= 1 && colonne >= WIDTH - 2) || // coin haut-droit
                (ligne >= HEIGHT - 2 && colonne <= 1) || // coin bas-gauche
                (ligne >= HEIGHT - 2 && colonne >= WIDTH - 2); // coin bas-droit
    }

    /**
     * Récupère la grille actuelle du jeu.
     *
     * @return La grille du jeu sous forme de tableau 2D d'entiers.
     */
    public int[][] getGrid() {
        return grid;
    }

    /**
     * Définit la grille du jeu.
     *
     * @param grid La nouvelle grille du jeu à définir.
     */
    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    /**
     * Récupère la liste des bonus actifs sur la grille de jeu.
     *
     * @return La liste des objets Bonus actifs.
     */
    public List<Bonus> getActiveBonuses() {
        return activeBonuses;
    }

    /**
     * Ajoute un bonus à la liste des bonus actifs.
     *
     * @param bonus Le bonus à ajouter.
     */
    public void addBonus(Bonus bonus) {
        activeBonuses.add(bonus);
    }

    /**
     * Supprime un bonus de la liste des bonus actifs.
     *
     * @param bonus Le bonus à supprimer.
     */
    public void removeBonus(Bonus bonus) {
        activeBonuses.remove(bonus);
    }

}
