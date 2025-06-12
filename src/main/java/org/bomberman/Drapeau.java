/**
 * Package principal de l'application Bomberman.
 */
package org.bomberman;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Représente un drapeau dans le jeu Bomberman, utilisé dans le mode "Capture The Flag".
 * Chaque drapeau est associé à un joueur propriétaire et peut être capturé.
 */
public class Drapeau extends Group {
    private int gridX;
    private int gridY;
    private static final int CELL_SIZE = 50;
    private Joueur_Personnage proprietaire;
    private boolean isCaptured = false;

    /**
     * Constructeur de la classe Drapeau.
     * Crée une nouvelle instance de drapeau à une position donnée, avec un propriétaire et une couleur.
     *
     * @param x La position X (colonne) sur la grille.
     * @param y La position Y (ligne) sur la grille.
     * @param joueur Le joueur propriétaire de ce drapeau.
     * @param couleurDrapeau La couleur visuelle du drapeau.
     */
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

    /**
     * Retourne la position X (colonne) du drapeau sur la grille.
     *
     * @return La position X du drapeau.
     */
    public int getGridX() { return gridX; }

    /**
     * Retourne la position Y (ligne) du drapeau sur la grille.
     *
     * @return La position Y du drapeau.
     */
    public int getGridY() { return gridY; }

    /**
     * Retourne le joueur propriétaire de ce drapeau.
     *
     * @return L'objet Joueur_Personnage qui est le propriétaire.
     */
    public Joueur_Personnage getProprietaire() {
        return proprietaire;
    }

    /**
     * Vérifie si le drapeau a été capturé.
     *
     * @return true si le drapeau est capturé, false sinon.
     */
    public boolean isCaptured() {
        return isCaptured;
    }

    /**
     * Définit l'état de capture du drapeau.
     *
     * @param captured true pour marquer le drapeau comme capturé, false sinon.
     */
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

