package org.bomberman;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Pacman extends PacMan_Personnage {


    public Pacman(Game game, int startX, int startY) {
        super(game,startX,startY);
        setLayoutX(5);  // Position X de départ
        setLayoutY(5);
    }

    @Override
    public void deplacerAGauche() {
        super.deplacerAGauche();

    }

    @Override
    public void deplacerADroite(double largeurJeu) {
        super.deplacerADroite(largeurJeu);
    }

    @Override
    public void deplacerEnBas(double hauteurJeu) {
        super.deplacerEnBas(hauteurJeu);
    }

    @Override
    public void deplacerEnHaut() {
        super.deplacerEnHaut();
    }

}