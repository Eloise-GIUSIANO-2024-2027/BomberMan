package org.bomberman.entite;

public abstract class Entite {
    private int x;
    private int y;
    private String linkSprite;
    protected boolean estVivant = true;


    public Entite(int x, int y, String linkSprite) {
        this.x = x;
        this.y = y;
        this.linkSprite = linkSprite;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int[] getPos() {
        return new int[]{this.x, this.y};
    }

    public void disparait() {
        this.estVivant = false;
    }
    public boolean estVivant() {
        return this.estVivant;
    }




    // Dans votre boucle de jeu :
//    public void mettreAJour() {
//        entites.removeIf(entite -> !entite.estVivant());
//    }
}
