package org.bomberman.entite;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.application.Platform;
import org.bomberman.Bot_Personnage;
import org.bomberman.Game;
import org.bomberman.GameGrid;
import org.bomberman.PacMan_Personnage; // Importez la classe PacMan_Personnage
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Bombe extends ImageView {
    private int x, y;
    private int rayon;
    private Game game;
    private GameGrid gameGrid;
    private List<PacMan_Personnage> joueurs;
    private List<Bot_Personnage> bot;

    public Bombe(int x, int y, int rayon, Game game, GameGrid gameGrid, List<PacMan_Personnage> joueurs, List<Bot_Personnage> bot) {
        this.x = x;
        this.y = y;
        this.rayon = rayon;
        this.game = game;
        this.gameGrid = gameGrid;
        this.joueurs = joueurs;
        this.bot = bot;

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
        List<int[]> affectedCells = new ArrayList<>();
        affectedCells.add(new int[]{y, x});

// Horizontal
        for (int dx = -rayon; dx <= rayon; dx++) {
            int nx = x + dx;
            if (nx >= 0 && nx < grid[0].length) {
                if (grid[y][nx] == 2) grid[y][nx] = 0;
                affectedCells.add(new int[]{y, nx}); // ➕ Ajouter la case à la liste
            }
        }

// Vertical
        for (int dy = -rayon; dy <= rayon; dy++) {
            int ny = y + dy;
            if (ny >= 0 && ny < grid.length) {
                if (grid[ny][x] == 2) grid[ny][x] = 0;
                affectedCells.add(new int[]{ny, x}); // ➕ Ajouter la case à la liste
            }
        }

        for (PacMan_Personnage joueur : joueurs) {
            // Vérifier si le joueur est toujours vivant
            if (joueur.estVivant()) {
                int joueurGridX = joueur.getGridX(); // Colonne du joueur
                int joueurGridY = joueur.getGridY(); // Ligne du joueur

                // Vérifier si la position du joueur est dans les cellules affectées par l'explosion
                for (int[] cell : affectedCells) {
                    int affectedRow = cell[0];
                    int affectedCol = cell[1];

                    if (joueurGridY == affectedRow && joueurGridX == affectedCol) {
                        // Le joueur est dans la zone d'explosion
                        joueur.disparait(); // Appeler la méthode pour marquer le joueur comme non-vivant
                        gameGrid.getEntityLayer().getChildren().remove(joueur); // Supprimer visuellement le joueur
                        System.out.println("Le joueur à la position (" + joueurGridX + ", " + joueurGridY + ") a été tué par la bombe !");
                        break; // Un joueur ne peut être tué qu'une fois par explosion, pas besoin de vérifier d'autres cellules
                    }
                }
            }
        }

        for (Bot_Personnage bot : bot) {
            // Vérifier si le joueur est toujours vivant
            if (bot.estVivant()) {
                int joueurGridX = bot.getGridX(); // Colonne du joueur
                int joueurGridY = bot.getGridY(); // Ligne du joueur

                // Vérifier si la position du joueur est dans les cellules affectées par l'explosion
                for (int[] cell : affectedCells) {
                    int affectedRow = cell[0];
                    int affectedCol = cell[1];

                    if (joueurGridY == affectedRow && joueurGridX == affectedCol) {
                        // Le joueur est dans la zone d'explosion
                        bot.disparait(); // Appeler la méthode pour marquer le joueur comme non-vivant
                        gameGrid.getEntityLayer().getChildren().remove(bot); // Supprimer visuellement le joueur
                        System.out.println("Le joueur à la position (" + joueurGridX + ", " + joueurGridY + ") a été tué par la bombe !");
                        break; // Un joueur ne peut être tué qu'une fois par explosion, pas besoin de vérifier d'autres cellules
                    }
                }
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