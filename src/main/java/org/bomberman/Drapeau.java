package org.bomberman;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.bomberman.entite.Joueur;

public class Drapeau extends Group {
    private int gridX;
    private int gridY;
    private static final int CELL_SIZE = 48;
    private PacMan_Personnage proprietaire;

    public Drapeau(int x, int y, PacMan_Personnage joueur) {
        this.gridX = x ;
        this.gridY = y;
        this.proprietaire = joueur;

        Rectangle rect = new Rectangle(CELL_SIZE, CELL_SIZE);
        rect.setFill(Color.GOLD); // ou image de drapeau
        getChildren().add(rect);

        setTranslateX(gridX  * CELL_SIZE);
        setTranslateY(gridY * CELL_SIZE);
    }

    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
//    public PacMan_Personnage getProprietaire() { return proprietaire; }

}

