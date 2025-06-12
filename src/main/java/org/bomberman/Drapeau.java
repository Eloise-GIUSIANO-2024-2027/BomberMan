package org.bomberman;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Drapeau extends Group {
    private int gridX;
    private int gridY;
    private static final int CELL_SIZE = 50;
    private Joueur_Personnage proprietaire;
    private boolean isCaptured = false;

    public Drapeau(int x, int y, Joueur_Personnage joueur, Color couleurDrapeau) { //
        this.gridX = x ;
        this.gridY = y;
        this.proprietaire = joueur;

        Rectangle rect = new Rectangle(CELL_SIZE - 10 , CELL_SIZE- 10);
        rect.setFill(couleurDrapeau); // Utilisation de la couleur passée en paramètre
        getChildren().add(rect);

        setTranslateX(gridX  * CELL_SIZE+5);
        setTranslateY(gridY * CELL_SIZE+2);
    }

    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }

    public Joueur_Personnage getProprietaire() {
        return proprietaire;
    }

    public boolean isCaptured() {
        return isCaptured;
    }

    public void setCaptured(boolean captured) {
        isCaptured = captured;
    }

    /**
     * Rend le drapeau invisible après sa capture.
     */
    public void disparaitre() {
        this.setVisible(false);
    }
}

