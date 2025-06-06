package org.bomberman.entite;

import org.bomberman.Game;

public class Joueur extends Entite {
    public double vitesse = 0.2;
    private int nbBombes = 0;
    private int nbBombesMax = 1;
    private Game game;
    private int tailleBombe = 1;

    public Joueur(int x, int y, String linkSprite, Game game){
        super(x, y, "character.idle-front");
        this.game = game;
    }

    public int[] haut(){
        int[] pos = getPos();
        setPos(pos[0], pos[1] - 1);
        return getPos();
    }
    public int[] bas(){
        int[] pos = getPos();
        setPos(pos[0], pos[1] + 1);
        return getPos();
    }
    public int[] gauche(){
        int[] pos = getPos();
        setPos(pos[0] - 1, pos[1]);
        return getPos();
    }
    public int[] droite(){
        int[] pos = getPos();
        setPos(pos[0] + 1, pos[1]);
        return getPos();
    }

    public int[] poseBombe(){
        new Bombe(getPos()[0], getPos()[1], tailleBombe, game);
        return getPos();
    }
}
