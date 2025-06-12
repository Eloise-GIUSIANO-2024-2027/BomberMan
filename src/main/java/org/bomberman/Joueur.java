/**
 * Package principal de l'application Bomberman.
 */
package org.bomberman;

import java.io.IOException;

/**
 * Représente un joueur contrôlable dans le jeu Bomberman, étendant les fonctionnalités
 * de base définies dans {@link Joueur_Personnage}.
 */
public class Joueur extends Joueur_Personnage {

    /**
     * Constructeur de la classe Joueur.
     * Initialise un nouveau joueur avec sa position de départ et son numéro.
     * Appelle le constructeur de la classe parente {@link Joueur_Personnage}.
     *
     * @param game L'instance du modèle de jeu.
     * @param startX La position X (colonne) de départ du joueur sur la grille.
     * @param startY La position Y (ligne) de départ du joueur sur la grille.
     * @param playerNumber Le numéro identifiant ce joueur.
     * @throws IOException Si une erreur d'entrée/sortie survient lors de la lecture du fichier de thème dans le constructeur parent.
     */
    public Joueur(Game game, int startX, int startY, int playerNumber) throws IOException {
        super(game,startX,startY,playerNumber);
        setLayoutX(5);  // Position X de départ
        setLayoutY(5);
    }

    /**
     * Surcharge de la méthode {@code deplacerAGauche()} de la classe parente.
     * Déplace le joueur d'une case vers la gauche.
     */
    @Override
    public void deplacerAGauche() {
        super.deplacerAGauche();

    }

    /**
     * Surcharge de la méthode {@code deplacerADroite()} de la classe parente.
     * Déplace le joueur d'une case vers la droite.
     *
     * @param largeurJeu La largeur totale du jeu (passée au parent).
     */
    @Override
    public void deplacerADroite(double largeurJeu) {
        super.deplacerADroite(largeurJeu);
    }

    /**
     * Surcharge de la méthode {@code deplacerEnBas()} de la classe parente.
     * Déplace le joueur d'une case vers le bas.
     *
     * @param hauteurJeu La hauteur totale du jeu (passée au parent).
     */
    @Override
    public void deplacerEnBas(double hauteurJeu) {
        super.deplacerEnBas(hauteurJeu);
    }

    /**
     * Surcharge de la méthode {@code deplacerEnHaut()} de la classe parente.
     * Déplace le joueur d'une case vers le haut.
     */
    @Override
    public void deplacerEnHaut() {
        super.deplacerEnHaut();
    }

}