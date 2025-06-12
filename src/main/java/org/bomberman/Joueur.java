package org.bomberman;

import java.io.IOException;

public class Joueur extends Joueur_Personnage {


    public Joueur(Game game, int startX, int startY, int playerNumber) throws IOException {
        super(game,startX,startY,playerNumber);
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