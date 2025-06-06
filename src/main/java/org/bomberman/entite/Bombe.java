package org.bomberman.entite;

import java.util.Timer;
import java.util.TimerTask;

public class Bombe extends Entite {
    public boolean explose = false;
    private int taille;

    public Bombe(int x, int y, int taille, int[][] map) {
        super(x, y, "imgBombe");
        this.taille = taille;

        //Placement de la bombe
        map[y][x] = 2;


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                exploser(); // explosion de la bombe
            }
        }, 2000); // 2000 millisecondes = 2 secondes
    }

    public void exploser() {
        explose = true;

    }
}
