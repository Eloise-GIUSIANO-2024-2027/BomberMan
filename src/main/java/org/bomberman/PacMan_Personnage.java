package org.bomberman;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.w3c.dom.css.Rect;

import java.util.Objects;

class PacMan_Personnage extends Group {
    private String direction = "bas";
    private Rectangle rectangle = new Rectangle(48, 48);
    private int gridX = 0; // Position X dans la grille (colonne)
    private int gridY = 0; // Position Y dans la grille (ligne)
    private static final int CELL_SIZE = 48; // Taille d'une case (48x48 comme dans GameGrid)
    private Game game;


    public PacMan_Personnage(Game game, int startX, int startY) {
        this.game = game;
        this.gridX = startX;
        this.gridY = startY;

        rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-front.gif")), 32, 64, false, false)));
        super.getChildren().add(this.rectangle);

        // Positionner le personnage selon sa position dans la grille
        updatePixelPosition();
    }

    private void updatePixelPosition() {
        double newX = gridX * CELL_SIZE;
        double newY = gridY * CELL_SIZE;

        System.out.println("Mise à jour position: grille(" + gridX + "," + gridY + ") -> pixels(" + newX + "," + newY + ")");

        // Utiliser TranslateX/Y au lieu de LayoutX/Y
        setTranslateX(newX);
        setTranslateY(newY);

        System.out.println("Position translate après mise à jour: " + getTranslateX() + ", " + getTranslateY());
    }

    private boolean isValidGridPosition(int x, int y) {
        int[][] grid = game.getGrid();

        // Vérifier les limites de la grille
        if (x < 0 || y < 0 ) {
            return false;
        }

        // Vérifier si la case n'est pas un mur (1 = mur, 0 = vide)
        return grid[y][x] == 0;
    }

    public void deplacerAGauche() {
        System.out.println("Tentative de déplacement vers la gauche");

        int nouvellePositionX = gridX - 1;

        if (isValidGridPosition(nouvellePositionX, gridY)) {
            gridX = nouvellePositionX;
            updatePixelPosition();
            System.out.println("Déplacement réussi vers la gauche");
        } else {
            System.out.println("Déplacement bloqué - obstacle ou limite");
        }

        if (!direction.equals("gauche")) {
            direction = "gauche";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-left.gif")), 32, 32, false, false)));
        }
    }

    public void deplacerADroite(double largeurJeu) {
        System.out.println("Tentative de déplacement vers la droite");

        int nouvellePositionX = gridX + 1;

        if (isValidGridPosition(nouvellePositionX, gridY)) {
            gridX = nouvellePositionX;
            updatePixelPosition();
            System.out.println("Déplacement réussi vers la droite");
        } else {
            System.out.println("Déplacement bloqué - obstacle ou limite");
        }

        if (!direction.equals("droite")) {
            direction = "droite";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-right.gif")), 32, 32, false, false)));
        }
    }

    public void deplacerEnBas(double hauteurJeu) {
        System.out.println("Tentative de déplacement vers le bas");

        int nouvellePositionY = gridY + 1;

        if (isValidGridPosition(gridX, nouvellePositionY)) {
            gridY = nouvellePositionY;
            updatePixelPosition();
            System.out.println("Déplacement réussi vers le bas");
        } else {
            System.out.println("Déplacement bloqué - obstacle ou limite");
        }

        if (!direction.equals("bas")) {
            direction = "bas";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-front.gif")), 32, 32, false, false)));
        }
    }

    public void deplacerEnHaut() {
        System.out.println("Tentative de déplacement vers le haut");

        int nouvellePositionY = gridY - 1;

        if (isValidGridPosition(gridX, nouvellePositionY)) {
            gridY = nouvellePositionY;
            updatePixelPosition();
            System.out.println("Déplacement réussi vers le haut");
        } else {
            System.out.println("Déplacement bloqué - obstacle ou limite");
        }

        if (!direction.equals("haut")) {
            direction = "haut";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-back.gif")), 32, 32, false, false)));
        }
    }

    // Getters pour la position de grille (utiles pour debug)
    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
}