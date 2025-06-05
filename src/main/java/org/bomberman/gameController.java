package org.bomberman;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class gameController {

    @FXML
    private GridPane gridPane;  // Référence à la GridPane dans FXML

    private Game game;  // L'instance de la logique du jeu

    // Méthode appelée pour démarrer le jeu
    public void startGame() {
        game = new Game();
        generateGrid();
    }

    // Méthode pour générer la grille sur l'interface graphique
    private void generateGrid() {
        int[][] grid = game.getGrid();  // Récupérer la grille de la logique du jeu

        // Vider le GridPane avant de le remplir
        gridPane.getChildren().clear();

        // Remplir le GridPane avec des cases (rectangles)
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Rectangle rectangle = new Rectangle(40, 40);
                if (grid[i][j] == 1) {
                    rectangle.setFill(Color.GRAY);  // Mur
                } else {
                    rectangle.setFill(Color.WHITE);  // Espace vide
                }
                // Ajouter le rectangle dans la grille
                gridPane.add(rectangle, j, i);
            }
        }
    }
}

