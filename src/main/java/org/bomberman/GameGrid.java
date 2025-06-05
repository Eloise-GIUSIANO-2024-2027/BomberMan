// src/main/java/org/bomberman/GameGrid.java
package org.bomberman;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

// GameGrid hérite maintenant de GridPane directement
public class GameGrid extends GridPane {

    private Game game; // L'instance de la logique du jeu

    public GameGrid(Game game) {
        this.game = game;

        // Récupérer la grille du jeu
        int[][] grid = game.getGrid();

        // Configurer le GridPane
        this.setAlignment(Pos.CENTER); // Centrer les éléments à l'intérieur du GridPane
        this.setHgap(1); // Très petit espace horizontal entre les cellules
        this.setVgap(1); // Très petit espace vertical entre les cellules
        WritableImage target = new WritableImage(32, 32);

        // Créer les cases (rectangles) pour chaque cellule de la grille
        for (int i = 0; i < grid.length; i++) { // Itère sur les lignes (y)
            for (int j = 0; j < grid[i].length; j++) { // Itère sur les colonnes (x)
                Rectangle rectangle = new Rectangle(40, 40); // Taille des cases

                // Appliquer les couleurs en fonction du type de case
                if (grid[i][j] == 1) {
                    rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/terrain/rock1.png")),32,32,false,false))); // Gris foncé pour les murs
                    // Ou utiliser une classe CSS si tu veux : rectangle.getStyleClass().add("grid-cell-wall");
                } else {
                    rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/terrain/grass1.png")),32,32,false,false))); // Gris foncé pour les murs
                    // Ou utiliser une classe CSS si tu veux : rectangle.getStyleClass().add("grid-cell-empty");
                }

                // Ajouter le rectangle à la grille à la position (j, i)
                // Attention, GridPane.add(node, columnIndex, rowIndex)
                this.add(rectangle, j, i);
            }
        }
    }
}