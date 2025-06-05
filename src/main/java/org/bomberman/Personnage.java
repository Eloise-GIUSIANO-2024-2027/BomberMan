package org.bomberman;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

class Personnage extends Group {
    final static double LARGEUR_MOITIE_PERSONNAGE = 10;
    final static double LARGEUR_PERSONNAGE = LARGEUR_MOITIE_PERSONNAGE * 2;
    private final Circle corps;
    private String direction;


    public Personnage(String direction, Color couleurContour, Color couleurRemplissage) {
        this.direction = direction;
        corps = new Circle(10, 10, LARGEUR_MOITIE_PERSONNAGE, couleurContour);
        corps.setFill(couleurRemplissage);
        super.getChildren().add(corps);
    }

    public void deplacerAGauche() {
        //    ****
        //   *    *
        //  *---   *
        //   *    *
        //    ****

        //déplacement <----
        if (getLayoutX() >= LARGEUR_PERSONNAGE) {
            double posXAvantDeplacement = getLayoutX();
            setLayoutX(getLayoutX() - LARGEUR_PERSONNAGE);
            //gestion de la collision avec les obstacles
            if (estEnCollisionAvecLesObstaclesDuJeu()) {
                setLayoutX(posXAvantDeplacement);
            }

        }
        if (!direction.equals("gauche")) {
            direction = "gauche";
        }
    }

    public void deplacerADroite(double largeurJeu) {
        //    ****
        //   *    *
        //  *   ---*
        //   *    *
        //    ****
        // déplacement ---->
        if (getLayoutX() < largeurJeu - LARGEUR_PERSONNAGE) {
            double posXAvantDeplacement = getLayoutX();

            setLayoutX(getLayoutX() + LARGEUR_PERSONNAGE);
            //gestion de la collision avec les obstacles
            if (estEnCollisionAvecLesObstaclesDuJeu()) {
                setLayoutX(posXAvantDeplacement);
            }
        }
        if (!direction.equals("droite")) {
            direction = "droite";
        }
    }

    public void deplacerEnBas(double hauteurJeu) {
        //    *****
        //   *     *
        //  *   |   *
        //   *  |  *
        //    *****

        if (getLayoutY() < hauteurJeu - LARGEUR_PERSONNAGE) {
            double posYAvantDeplacement = getLayoutY();

            setLayoutY(getLayoutY() + LARGEUR_PERSONNAGE);
            //gestion de la collision avec les obstacles
            if (estEnCollisionAvecLesObstaclesDuJeu()) {
                setLayoutY(posYAvantDeplacement);
            }
        }
        //sens de la bouche
        if (!direction.equals("bas")) {
            direction = "bas";
        }
    }

    public void deplacerEnHaut() {
        //    *****
        //   *  |  *
        //  *   |   *
        //   *     *
        //    *****
        if (getLayoutY() >= LARGEUR_PERSONNAGE) {
            double posYAvantDeplacement = getLayoutY();
            setLayoutY(getLayoutY() - LARGEUR_PERSONNAGE);
            //gestion de la collision avec les obstacles
            if (estEnCollisionAvecLesObstaclesDuJeu()) {
                setLayoutY(posYAvantDeplacement);
            }
        }
        //sens de la bouche
        if (!direction.equals("haut")) {
            direction = "haut";
        }
    }

    public boolean estEnCollision(Personnage autrePersonnage) {
        return getBoundsInParent().contains(autrePersonnage.getBoundsInParent())
                || autrePersonnage.getBoundsInParent().contains(getBoundsInParent());
    }

    public boolean estEnCollisionAvecLesObstaclesDuJeu() {
        for (Obstacle unObstacle : JeuMain.getLesObstacles()) {
            if (getBoundsInParent().contains(unObstacle.getBoundsInParent())
                    || unObstacle.getBoundsInParent().contains(getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }


}