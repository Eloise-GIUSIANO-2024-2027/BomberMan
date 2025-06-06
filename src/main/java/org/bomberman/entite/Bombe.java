package org.bomberman.entite;

import org.bomberman.Game;

import java.util.Timer;
import java.util.TimerTask;

public class Bombe extends Entite {
    public boolean explose = false;
    private int taille;
    int x = getPos()[0];
    int y = getPos()[1];
    Game game;
    int[][] map;

    public Bombe(int x, int y, int taille, Game game) {
        super(x, y, "imgBombe");
        this.taille = taille;

        map = game.getGrid();
        map[x][y] = 2; // représente la bombe
        game.setGrid(map); // Met à jour la map



        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                exploser(); // explosion de la bombe
            }
        }, 2000); // 2000 millisecondes = 2 secondes
    }

    private void exploser() {
        if (explose) return; // Éviter les explosions multiples

        explose = true;
        System.out.println("La bombe a explosé à la position (" + x + ", " + y + ")");

        // Explosion au centre
        map[x][y] = 3; // 2 pour explosion

        // Explosion en croix selon la taille
        // Chaque direction doit être traitée séparément

        // Explosion vers la droite
        for (int i = 1; i <= taille; i++) {
            if (x + i >= map.length) break; // Sortie de la map
            if (map[x + i][y] == '#') break; // Mur indestructible - arrêter
            if (map[x + i][y] == 'M') { // Mur destructible
                map[x + i][y] = 'X';
                break; // Arrêter après avoir détruit le mur
            }
            map[x + i][y] = 'X';
        }

        // Explosion vers la gauche
        for (int i = 1; i <= taille; i++) {
            if (x - i < 0) break; // Sortie de la map
            if (map[x - i][y] == '#') break; // Mur indestructible - arrêter
            if (map[x - i][y] == 'M') { // Mur destructible
                map[x - i][y] = 'X';
                break; // Arrêter après avoir détruit le mur
            }
            map[x - i][y] = 'X';
        }

        // Explosion vers le bas
        for (int i = 1; i <= taille; i++) {
            if (y + i >= map[0].length) break; // Sortie de la map
            if (map[x][y + i] == '#') break; // Mur indestructible - arrêter
            if (map[x][y + i] == 'M') { // Mur destructible
                map[x][y + i] = 'X';
                break; // Arrêter après avoir détruit le mur
            }
            map[x][y + i] = 'X';
        }

        // Explosion vers le haut
        for (int i = 1; i <= taille; i++) {
            if (y - i < 0) break; // Sortie de la map
            if (map[x][y - i] == '#') break; // Mur indestructible - arrêter
            if (map[x][y - i] == 'M') { // Mur destructible
                map[x][y - i] = 'X';
                break; // Arrêter après avoir détruit le mur
            }
            map[x][y - i] = 'X';
        }

        game.setGrid(map);
    }
}
