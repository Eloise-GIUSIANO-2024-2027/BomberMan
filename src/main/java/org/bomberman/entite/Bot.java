package org.bomberman.entite;

import org.bomberman.Game;

public class Bot extends Joueur {
    private Game game;

    public Bot(int x, int y, Game game) {
        super(x, y, "character.idle-front-bot", game); //changer pour le sprite spécifique pour le bot
    }

    public void prendreDecision() {
        int[][] grid = game.getGrid();
        int[] currentPos = getPos(); // Position actuelle du bot

        // Exemple de déplacement aléatoire pour commencer :
        int randomMove = (int) (Math.random() * 4); // 0:haut, 1:bas, 2:gauche, 3:droite

        // Vérifier si le déplacement est possible avant de le faire
        boolean moved = false;
        switch (randomMove) {
            case 0: // Haut
                if (currentPos[1] > 0 && grid[currentPos[0]][currentPos[1] - 1] == 0) { // Vérifie le bord et que la case n'est pas un mur
                    haut();
                    moved = true;
                }
                break;
            case 1: // Bas
                if (currentPos[1] < game.HEIGHT - 1 && grid[currentPos[0]][currentPos[1] + 1] == 0) {
                    bas();
                    moved = true;
                }
                break;
            case 2: // Gauche
                if (currentPos[0] > 0 && grid[currentPos[0] - 1][currentPos[1]] == 0) {
                    gauche();
                    moved = true;
                }
                break;
            case 3: // Droite
                if (currentPos[0] < game.WIDTH - 1 && grid[currentPos[0] + 1][currentPos[1]] == 0) {
                    droite();
                    moved = true;
                }
                break;
        }

        // Exemple très simple de pose de bombe (pas intelligent du tout)
        if (Math.random() < 0.01) { // 1% de chance de poser une bombe à chaque décision
            poseBombe();
        }
    }

    // ajouter d'autres méthodes ici pour des décisions plus intelligentes
}