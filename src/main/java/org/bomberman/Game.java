package org.bomberman;


import org.bomberman.entite.Bonus;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private static final int WIDTH = 13;   // Largeur de la grille
    private static final int HEIGHT = 11;  // Hauteur de la grille
    private int[][] grid;
    private List<Bonus> activeBonuses; // Liste pour les bonus actifs
    private List<PacMan_Personnage> joueurs;  // Grille du jeu (10x10)

    public Game() {
        grid = new int[HEIGHT][WIDTH];
        activeBonuses = new ArrayList<>();
        initGrid();
    }

    // Initialiser la grille avec des valeurs (0 = vide, 1 = mur)
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

    private boolean isSpawnZone(int ligne, int colonne) {
        return (ligne <= 1 && colonne <= 1) || // coin haut-gauche
                (ligne <= 1 && colonne >= WIDTH - 2) || // coin haut-droit
                (ligne >= HEIGHT - 2 && colonne <= 1) || // coin bas-gauche
                (ligne >= HEIGHT - 2 && colonne >= WIDTH - 2); // coin bas-droit
    }

    // Récupérer la grille du jeu
    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    // Démarrer le jeu (logique de base ici, à étendre)
    public void startGame() {
        System.out.println("Le jeu a commencé !");
        // Logique du jeu à ajouter ici (mouvement, explosions, etc.)
    }
    public List<Bonus> getActiveBonuses() {
        return activeBonuses;
    }

    public void addBonus(Bonus bonus) {
        activeBonuses.add(bonus);
    }

    // NOUVEAU/CORRIGÉ : La méthode removeBonus doit exister ici
    public void removeBonus(Bonus bonus) {
        activeBonuses.remove(bonus);
    }

}
