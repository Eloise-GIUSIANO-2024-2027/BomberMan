package org.bomberman.entite;

/**
 * Classe abstraite représentant une entité dans le jeu Bomberman.
 * Une entité est un objet qui possède une position (x, y) sur la grille de jeu,
 * un sprite pour l'affichage graphique et un état de vie.
 *
 * @author Gustin Mailhe
 */
public abstract class Entite {
    /** Coordonnée x de l'entité sur la grille */
    private int x;
    /** Coordonnée y de l'entité sur la grille */
    private int y;
    /** Chemin vers le fichier sprite de l'entité */
    private String linkSprite;
    /** Indique si l'entité est encore vivante/active */
    protected boolean estVivant = true;


    /**
     * Constructeur de l'entité.
     *
     * @param x la coordonnée x initiale de l'entité
     * @param y la coordonnée y initiale de l'entité
     * @param linkSprite le chemin vers le fichier sprite de l'entité
     */
    public Entite(int x, int y, String linkSprite) {
        this.x = x;
        this.y = y;
        this.linkSprite = linkSprite;
    }

    /**
     * Modifie la position de l'entité.
     *
     * @param x la nouvelle coordonnée x
     * @param y la nouvelle coordonnée y
     */
    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Récupère la position actuelle de l'entité.
     *
     * @return un tableau d'entiers contenant les coordonnées [x, y]
     */
    public int[] getPos() {
        return new int[]{this.x, this.y};
    }

    /**
     * Fait disparaître l'entité en modifiant son état de vie.
     * Une fois cette méthode appelée, l'entité n'est plus considérée comme vivante.
     */
    public void disparait() {
        this.estVivant = false;
    }

    /**
     * Vérifie si l'entité est encore vivante/active.
     *
     * @return true si l'entité est vivante, false sinon
     */
    public boolean estVivant() {
        return this.estVivant;
    }
}
