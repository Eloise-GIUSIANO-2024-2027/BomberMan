// src/main/java/org/bomberman/GameGrid.java
package org.bomberman;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class GameGrid extends GridPane {

    private Game game;
    int[][] grid;
    private Pane entityLayer; // Couche pour les entités (personnages, bombes)
    private String theme = "default";

    public GameGrid(Game game) throws IOException {
        this.game = game;
        grid = game.getGrid();

        // Configurer le GridPane
        this.setAlignment(Pos.CENTER);
        this.setHgap(2);
        this.setVgap(2);
        Path path = Paths.get("src/main/resources/data.txt");
        System.out.println(Files.readString(path));
        this.theme = Files.readString(path);

        // Créer une couche pour les entités
        entityLayer = new Pane();

        refresh();
    }

    public void refresh() {
        // Sauvegarder TOUTES les entités (personnages, bombes, etc.)
        java.util.List<javafx.scene.Node> entites = new java.util.ArrayList<>();
        for (javafx.scene.Node node : this.getChildren()) {
            if (!(node instanceof Rectangle)) {
                entites.add(node);
            }
        }

        // Vider complètement la grille
        this.getChildren().clear();

        // D'ABORD recréer le terrain
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Rectangle rectangle = new Rectangle(48, 48);

                // Appliquer les textures selon le type de case
                if (grid[i][j] == 1) {
                    rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/terrain/rock"+theme+".png")), 32, 32, false, false)));
                } else if (grid[i][j] == 2) {
                    rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/terrain/wall"+theme+".png")), 32, 32, false, false)));
                } else if (grid[i][j] == 3) {
                    rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/terrain/grassdefault.png")), 32, 32, false, false)));
                } else if (grid[i][j] == 4) {
                    rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/fxs/explosion.png")), 32, 32, false, false)));
                } else {
                    rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/terrain/grass"+theme+".png")), 32, 32, false, false)));
                }

                this.add(rectangle, j, i);
            }
        }

        // ENSUITE remettre toutes les entités par-dessus
        this.getChildren().addAll(entites);

        // Forcer les personnages au premier plan
        for (javafx.scene.Node entite : entites) {
            entite.toFront();
        }
    }

    // Méthode pour obtenir la couche des entités
    public Pane getEntityLayer() {
        return entityLayer;
    }
}