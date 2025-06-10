package org.bomberman;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

public class PacMan_Personnage extends Group {
    private String direction = "bas";
    private Rectangle rectangle = new Rectangle(48, 48);
    private int gridX = 0; // Position X dans la grille (colonne)
    private int gridY = 0; // Position Y dans la grille (ligne)
    private static final int CELL_SIZE = 50; // Taille d'une case (48x48 comme dans GameGrid)
    private Game game;
    private boolean estVivant = true; // Initialise le joueur comme vivant par défaut
    public double vitesse = 0.1;

    // ← AJOUTER ces méthodes
    public void setVitesse(double vitesse) {
        this.vitesse = vitesse;
    }

    public double getVitesse() {
        return vitesse;
    }

    public PacMan_Personnage(Game game, int startX, int startY) {
        this.game = game;
        this.gridX = startX;
        this.gridY = startY;

        rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-front.gif")), 32, 32, false, false)));
        super.getChildren().add(this.rectangle);

        // Positionner le personnage selon sa position dans la grille
        updatePixelPosition();
    }

    private void updatePixelPosition() {
        double newX = gridX * CELL_SIZE;
        double newY = gridY * CELL_SIZE;

        // Utiliser TranslateX/Y au lieu de LayoutX/Y
        setTranslateX(newX);
        setTranslateY(newY);

    }

    private boolean isValidGridPosition(int x, int y) {
        int[][] grid = game.getGrid();

        if (y < 0 || x < 0 || y >= grid.length || x >= grid[0].length) return false;

        return grid[y][x] == 0;
    }

    public void deplacerAGauche() {

        int nouvellePositionX = gridX - 1;

        if (isValidGridPosition(nouvellePositionX, gridY)) {
            gridX = nouvellePositionX;
            updatePixelPosition();
        }

        if (!direction.equals("gauche")) {
            direction = "gauche";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-left.gif")), 32, 32, false, false)));
        }
    }

    public void deplacerADroite(double largeurJeu) {

        int nouvellePositionX = gridX + 1;

        if (isValidGridPosition(nouvellePositionX, gridY)) {
            gridX = nouvellePositionX;
            updatePixelPosition();
        }

        if (!direction.equals("droite")) {
            direction = "droite";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-right.gif")), 32, 32, false, false)));
        }
    }

    public void deplacerEnBas(double hauteurJeu) {

        int nouvellePositionY = gridY + 1;

        if (isValidGridPosition(gridX, nouvellePositionY)) {
            gridY = nouvellePositionY;
            updatePixelPosition();
        }

        if (!direction.equals("bas")) {
            direction = "bas";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-front.gif")), 32, 32, false, false)));
        }
    }

    public void deplacerEnHaut() {

        int nouvellePositionY = gridY - 1;

        if (isValidGridPosition(gridX, nouvellePositionY)) {
            gridY = nouvellePositionY;
            updatePixelPosition();
        }

        if (!direction.equals("haut")) {
            direction = "haut";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-back.gif")), 32, 32, false, false)));
        }
    }

    public void disparait() {
        this.estVivant = false;
        this.setVisible(false);
    }

    public boolean estVivant() {
        return this.estVivant;
    }

    // Getters pour la position de grille (utiles pour debug)
    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public interface Joueur {
        double vitesse = 0.1; // vitesse par défaut
        void setVitesse(double vitesse);
        double getVitesse();
    }
}