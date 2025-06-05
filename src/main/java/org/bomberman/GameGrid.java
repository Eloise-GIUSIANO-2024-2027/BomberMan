package org.bomberman;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameGrid extends StackPane {

    private Game game;  // L'instance de la logique du jeu

    public GameGrid(Game game) {
        this.game = game;

        // Récupérer la grille du jeu
        int[][] grid = game.getGrid();

        // Créer un GridPane pour contenir les rectangles représentant les cases
        GridPane gridPane = new GridPane();

        // Créer les cases (rectangles) pour chaque cellule de la grille
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Rectangle rectangle = new Rectangle(40, 40);  // Taille des cases
                if (grid[i][j] == 1) {
                    rectangle.setFill(Color.GRAY);  // Mur
                } else {
                    rectangle.setFill(Color.WHITE);  // Espace vide
                }

                // Ajouter le rectangle à la grille à la position (i, j)
                gridPane.add(rectangle, j, i);
            }
        }

        // Ajouter une bordure autour de la grille
        Rectangle border = new Rectangle(40 * grid[0].length, 40 * grid.length);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.BLACK);  // Bordure noire
        border.setStrokeWidth(3);  // Largeur de la bordure

        // Créer un StackPane pour centrer la grille et ajouter la bordure
        getChildren().addAll(border, gridPane);

        // Centrer le contenu du StackPane (grille + bordure)
        setAlignment(Pos.CENTER);
    }
}
