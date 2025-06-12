/**
 * Package principal de l'application Bomberman.
 */
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

/**
 * Représente la grille visuelle du jeu Bomberman en utilisant JavaFX GridPane.
 * Gère l'affichage du terrain et des entités sur la grille.
 */
public class GameGrid extends GridPane {

    /**L'instance du modèle de jeu*/
    private Game game;
    /**La grille de données du jeu*/
    int[][] grid;
    /** Couche pour les entités (personnages, bombes)*/
    private Pane entityLayer;
    /**Le thème actuel pour les textures*/
    private String theme = "default";

    /**
     * Constructeur de la classe GameGrid.
     * Initialise la grille visuelle basée sur le modèle de jeu et charge le thème actuel.
     *
     * @param game L'instance du jeu (modèle) à associer à cette grille visuelle.
     * @throws IOException Si une erreur d'entrée/sortie survient lors de la lecture du fichier de thème.
     */
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

    /**
     * Rafraîchit l'affichage de la grille de jeu.
     * Recrée le terrain en fonction de la grille de données et repositionne les entités existantes.
     */
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

    /**
     * Retourne la couche Pane destinée à contenir les entités du jeu (personnages, bombes, etc.).
     *
     * @return Le Pane qui sert de couche pour les entités.
     */
    public Pane getEntityLayer() {
        return entityLayer;
    }
}