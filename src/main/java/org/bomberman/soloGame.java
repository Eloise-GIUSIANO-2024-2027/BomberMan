package org.bomberman;

public class soloGame {


    public static final int WIDTH = 11;   // Largeur de la grille
    public static final int HEIGHT = 13;  // Hauteur de la grille
    private int[][] grid;  // Grille du jeu (10x10)

    public soloGame() {
        grid = new int[WIDTH][HEIGHT];
        initGridSolo();
    }

    // Initialiser la grille avec des valeurs (0 = vide, 1 = mur)
    private void initGridSolo() {
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

}
