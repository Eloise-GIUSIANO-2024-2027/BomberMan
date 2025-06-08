package org.bomberman;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.util.*;

public class Bot_Personnage extends Group {
    private String direction = "bas";
    private Rectangle rectangle = new Rectangle(48, 48);
    private int gridX;
    private int gridY;
    private static final int CELL_SIZE = 50;
    private Game game;

    public Bot_Personnage(Game game, int startX, int startY) {
        this.game = game;
        this.gridX = startX;
        this.gridY = startY;

        rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-back.gif")), 32, 32, false, false)));
        super.getChildren().add(rectangle);
        updatePixelPosition();
    }

//    public void avancerSimplement() {
//        int[][] directions = {
//                {1, 0},   // droite
//                {-1, 0},  // gauche
//                {0, 1},   // bas
//                {0, -1}   // haut
//        };
//
//        // Mélanger les directions aléatoirement
//        List<int[]> directionsAleatoire = new ArrayList<>(Arrays.asList(directions));
//        Collections.shuffle(directionsAleatoire);
//
//        for (int[] dir : directionsAleatoire) {
//            int newX = gridX + dir[0];
//            int newY = gridY + dir[1];
//            if (isValidGridPosition(newX, newY)) {
//                gridX = newX;
//                gridY = newY;
//                updatePixelPosition();
//                System.out.println("Bot a bougé vers : " + gridX + "," + gridY);
//                return;
//            }
//        }
//    }

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

    private boolean isValidGridPosition(int x, int y) {
        int[][] grid = game.getGrid();
        return x >= 0 && y >= 0 && y < grid.length && x < grid[y].length && grid[y][x] == 0;
    }

    private void updatePixelPosition() {
        setTranslateX(gridX * CELL_SIZE);
        setTranslateY(gridY * CELL_SIZE);
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }
}
