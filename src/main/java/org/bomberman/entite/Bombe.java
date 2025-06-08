package org.bomberman.entite;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.application.Platform;
import org.bomberman.Game;
import org.bomberman.GameGrid;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Bombe extends ImageView {
    private int x, y;
    private int rayon;
    private Game game;
    private GameGrid gameGrid;

    public Bombe(int x, int y, int rayon, Game game, GameGrid gameGrid) {
        this.x = x;
        this.y = y;
        this.rayon = rayon;
        this.game = game;
        this.gameGrid = gameGrid;

        // Charger l'image de la bombe
        try {
            Image bombeImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/fxs/imgBombe.png")), 48, 48, false, false);
            this.setImage(bombeImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de la bombe: " + e.getMessage());
        }

        int[][] grid = game.getGrid();
        if (x >= 0 && x < grid.length && y >= 0 && y < grid[0].length) {
            // Marquer la case comme occupée par une bombe
            grid[y][x] = 3; // 3 = bombe
            game.setGrid(grid);

            System.out.println("Bombe créée aux coordonnées: " + x + "," + y);

            // Ajouter à la grille
            Platform.runLater(() -> {
                gameGrid.getChildren().add(this);
                GridPane.setColumnIndex(this, x); // x est la colonne
                GridPane.setRowIndex(this, y);
                this.toFront(); // Assurer que la bombe est visible
            });

            startTimer();
        } else {
            System.err.println("Position invalide pour la bombe: " + x + "," + y);
        }
    }

    private void startTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> explode());
            }
        }, 2000); // 2 secondes
    }

    private void explode() {
        int[][] grid = game.getGrid();

        // Centre - retirer la bombe
        grid[y][x] = 0;

        // Destruction autour de la bombe
        // Explosion horizontale (le long de la ligne 'y', en changeant la colonne 'x')
        for (int dx = -rayon; dx <= rayon; dx++) {
            int nx = x + dx; // nx est la nouvelle colonne
            if (nx >= 0 && nx < grid[0].length) { // grid[0].length est la largeur
                if (grid[y][nx] == 2) grid[y][nx] = 0; // grid[ligne][colonne]
            }
        }
        // Explosion verticale (le long de la colonne 'x', en changeant la ligne 'y')
        for (int dy = -rayon; dy <= rayon; dy++) {
            int ny = y + dy; // ny est la nouvelle ligne
            if (ny >= 0 && ny < grid.length) { // grid.length est la hauteur
                if (grid[ny][x] == 2) grid[ny][x] = 0; // grid[ligne][colonne]
            }
        }

        game.setGrid(grid);
        gameGrid.refresh(); // Recréer seulement la grille de terrain

        // Supprimer visuellement la bombe
        Platform.runLater(() -> {
            if (this.getParent() instanceof Pane pane) {
                pane.getChildren().remove(this);
            }
        });
    }
}