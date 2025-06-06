package org.bomberman;

import org.bomberman.entite.Bombe;

public class Game {

    private static final int WIDTH = 11;   // Largeur de la grille
    private static final int HEIGHT = 13;  // Hauteur de la grille
    private int[][] grid;  // Grille du jeu (10x10)

    public Game() {
        grid = new int[WIDTH][HEIGHT];
        initGrid();
    }

    // Initialiser la grille avec des valeurs (0 = vide, 1 = mur)
    private void initGrid() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (i % 2 == 1 && j % 2 == 1 && j!=WIDTH-1 && i!=HEIGHT-1) {
                    grid[i][j] = 1;  // Positionner des murs
                } else {
                    grid[i][j] = 0;  // Espaces vides
                }
            }
        }
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


}
