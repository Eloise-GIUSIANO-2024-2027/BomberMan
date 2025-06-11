package org.bomberman;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class PacMan_Fantome extends PacMan_Personnage {

    private final Rectangle basCorps;

    private final Circle oeilGauche;
    private final Circle retineGauche;

    private final Circle oeilDroit;
    private final Circle retineDroite;


    public PacMan_Fantome(Game game, int startX, int startY,int playerNumber) throws IOException {
        super(game, startX, startY,playerNumber);
        basCorps = new Rectangle(0, 10, 20, 10);
        basCorps.setFill(Color.BLUE);

        oeilGauche = new Circle(6, 6, 2, Color.WHITE);
        //regarde vers la droite
        retineGauche = new Circle(oeilGauche.getCenterX() + 1, 6, 1, Color.BLACK);

        oeilDroit = new Circle(14, 6, 2, Color.WHITE);
        //regarde vers la droite
        retineDroite = new Circle(oeilDroit.getCenterX() + 1, 6, 1, Color.BLACK);

        super.getChildren().add(basCorps);
        super.getChildren().add(oeilGauche);
        super.getChildren().add(retineGauche);
        super.getChildren().add(oeilDroit);
        super.getChildren().add(retineDroite);
    }

    @Override
    public void deplacerAGauche() {
        super.deplacerAGauche();
        retineGauche.setCenterX(oeilGauche.getCenterX() - 1);
        retineDroite.setCenterX(oeilDroit.getCenterX() - 1);
        retineGauche.setCenterY(oeilGauche.getCenterY());
        retineDroite.setCenterY(oeilDroit.getCenterY());
    }

    @Override
    public void deplacerADroite(double largeurJeu) {
        super.deplacerADroite(largeurJeu);
        retineGauche.setCenterX(oeilGauche.getCenterX() + 1);
        retineDroite.setCenterX(oeilDroit.getCenterX() + 1);
        retineGauche.setCenterY(oeilGauche.getCenterY());
        retineDroite.setCenterY(oeilDroit.getCenterY());
    }

    @Override
    public void deplacerEnBas(double hauteurJeu) {
        super.deplacerEnBas(hauteurJeu);
        retineGauche.setCenterY(oeilGauche.getCenterY() + 1);
        retineDroite.setCenterY(oeilDroit.getCenterY() + 1);
        retineGauche.setCenterX(oeilGauche.getCenterX());
        retineDroite.setCenterX(oeilDroit.getCenterX());
    }

    @Override
    public void deplacerEnHaut() {
        super.deplacerEnHaut();
        retineGauche.setCenterY(oeilGauche.getCenterY() - 1);
        retineDroite.setCenterY(oeilDroit.getCenterY() - 1);
        retineGauche.setCenterX(oeilGauche.getCenterX());
        retineDroite.setCenterX(oeilDroit.getCenterX());
    }


}
