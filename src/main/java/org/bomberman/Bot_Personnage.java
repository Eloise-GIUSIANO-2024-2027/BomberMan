package org.bomberman;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.util.Objects;

public class Bot_Personnage extends Group {
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

    public void avancerSimplement() {
        int[][] directions = {
                {1, 0},   // droite
                {-1, 0},  // gauche
                {0, 1},   // bas
                {0, -1}   // haut
        };

        for (int[] dir : directions) {
            int newX = gridX + dir[0];
            int newY = gridY + dir[1];
            if (isValidGridPosition(newX, newY)) {
                gridX = newX;
                gridY = newY;
                updatePixelPosition();
                System.out.println("Bot a bougé vers : " + gridX + "," + gridY);
                return;
            }
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
