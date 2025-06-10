package org.bomberman;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import org.bomberman.entite.Bombe;

import java.util.*;

public class Bot_Personnage extends Group {
    private String direction = "bas";
    private Rectangle rectangle = new Rectangle(48, 48);
    private int gridX;
    private int gridY;
    private static final int CELL_SIZE = 50;
    private Game game;
    private boolean estVivant = true;
    private int botId; // Identifiant unique pour chaque bot
    private boolean canPlaceBomb = true;

    public Bot_Personnage(Game game, int startX, int startY, int botId) {
        this.game = game;
        this.gridX = startX;
        this.gridY = startY;
        this.botId = botId;

        rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-back.gif")), 32, 32, false, false)));
        super.getChildren().add(rectangle);
        updatePixelPosition();
    }

    public boolean canPlaceBomb() {
        return canPlaceBomb;
    }

    public void setCanPlaceBomb(boolean canPlaceBomb) {
        this.canPlaceBomb = canPlaceBomb;
    }

    // Méthodes de déplacement inchangées...
    public void deplacerAGauche() {
        if (!estVivant) return;
        int nouvellePositionX = gridX - 1;

        if (isValidGridPosition(nouvellePositionX, gridY)) {
            gridX = nouvellePositionX;
            updatePixelPosition();
        }

        if (!direction.equals("gauche")) {
            direction = "gauche";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-left.gif")), 32, 32, false, false)));
        }
    }

    public void deplacerADroite(double largeurJeu) {
        if (!estVivant) return;
        int nouvellePositionX = gridX + 1;

        if (isValidGridPosition(nouvellePositionX, gridY)) {
            gridX = nouvellePositionX;
            updatePixelPosition();
        }

        if (!direction.equals("droite")) {
            direction = "droite";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-right.gif")), 32, 32, false, false)));
        }
    }

    public void deplacerEnBas(double hauteurJeu) {
        if (!estVivant) return;
        int nouvellePositionY = gridY + 1;

        if (isValidGridPosition(gridX, nouvellePositionY)) {
            gridY = nouvellePositionY;
            updatePixelPosition();
        }
        if (!direction.equals("bas")) {
            direction = "bas";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-front.gif")), 32, 32, false, false)));
        }
    }

    public void deplacerEnHaut() {
        if (!estVivant) return;
        int nouvellePositionY = gridY - 1;

        if (isValidGridPosition(gridX, nouvellePositionY)) {
            gridY = nouvellePositionY;
            updatePixelPosition();
        }

        if (!direction.equals("haut")) {
            direction = "haut";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-back.gif")), 32, 32, false, false)));
        }
    }

    private boolean isValidGridPosition(int x, int y) {
        int[][] grid = game.getGrid();
        return x >= 0 && y >= 0 && y < grid.length && x < grid[y].length && grid[y][x] == 0;
    }

    private void updatePixelPosition() {
        setTranslateX(gridX * CELL_SIZE);
        setTranslateY(gridY * CELL_SIZE);
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public int getBotId() {
        return botId;
    }

    public Group getImageView() { // Renvoie le groupe comme une "ImageView" pour l'ajout visuel
        return this; // Puisque Bot_Personnage étend Group, il peut être ajouté directement.
        // C'est l'équivalent de getImageView() pour les joueurs.
    }


    // Méthode principale modifiée pour cibler tous les ennemis
    public void agir(PacMan_Personnage joueur, List<PacMan_Personnage> tousLesJoueurs, GameGrid gameGrid, List<Bot_Personnage> bot) {
        if (!estVivant) return;

        int botX = getGridX();
        int botY = getGridY();

        // 1. FUITE SI BOMBE PROCHE (priorité absolue)
        if (estDansZoneDanger(botX, botY)) {
            for (int[] dir : new int[][]{{0,-1},{0,1},{-1,0},{1,0}}) {
                int newX = botX + dir[0];
                int newY = botY + dir[1];
                if (!estDansZoneDanger(newX, newY) && isValid(newX, newY)) {
                    seDeplacerVers(newX, newY);
                    return;
                }
            }

            // Plan B : fuir vers une case libre même si elle reste dans la zone danger
            for (int[] dir : new int[][]{{0,-1},{0,1},{-1,0},{1,0}}) {
                int newX = botX + dir[0];
                int newY = botY + dir[1];
                if (isValid(newX, newY)) {
                    seDeplacerVers(newX, newY);
                    return;
                }
            }
            return;
        }
        if (canPlaceBomb() && game.getGrid()[botY][botX] == 0) {
            System.out.println("Bot " + botId + " pose une bombe pour détruire des obstacles");
            // L'appel doit correspondre au constructeur à 9 arguments :
            new Bombe(botX, botY, 2, game, gameGrid, tousLesJoueurs, bot, null); // <-- Correct pour le bot
            setCanPlaceBomb(false);
            game.getGrid()[botY][botX] = 3;
        }

        // 2. RECHERCHE DE LA CIBLE LA PLUS PROCHE (joueur ou autres bots)
        CibleInfo cibleLaPlusProche = trouverCibleLaPlusProche(joueur, bot);

        if (cibleLaPlusProche != null) {
            // 3. SI CIBLE À PORTÉE DE BOMBE → POSER UNE BOMBE
            if (estAPorteeDeBombe(botX, botY, cibleLaPlusProche.x, cibleLaPlusProche.y)) {
                if (game.getGrid()[botY][botX] == 0) {
                    System.out.println("Bot " + botId + " pose une bombe pour attaquer la cible à (" + cibleLaPlusProche.x + "," + cibleLaPlusProche.y + ")");
                    new Bombe(botX, botY, 2, game, gameGrid, tousLesJoueurs, bot, null);
                    setCanPlaceBomb(false);
                    game.getGrid()[botY][botX] = 3;
                    return;
                }
            }

            // 4. SINON → SE RAPPROCHER DE LA CIBLE
            int dx = cibleLaPlusProche.x - botX;
            int dy = cibleLaPlusProche.y - botY;

            int[][] directions = Math.abs(dx) > Math.abs(dy) ?
                    new int[][]{{Integer.signum(dx), 0}, {0, Integer.signum(dy)}} :
                    new int[][]{{0, Integer.signum(dy)}, {Integer.signum(dx), 0}};

            for (int[] dir : directions) {
                int newX = botX + dir[0];
                int newY = botY + dir[1];
                if (isValid(newX, newY)) {
                    seDeplacerVers(newX, newY);
                    return;
                }
            }

            if (canPlaceBomb() && estAPorteeDeBombe(botX, botY, cibleLaPlusProche.x, cibleLaPlusProche.y)) {
                if (game.getGrid()[botY][botX] == 0) {
                    System.out.println("Bot " + botId + " pose une bombe pour attaquer la cible à (" + cibleLaPlusProche.x + "," + cibleLaPlusProche.y + ")");
                    // L'appel doit correspondre au constructeur à 9 arguments :
                    new Bombe(botX, botY, 2, game, gameGrid, tousLesJoueurs, bot, null); // <-- Correct pour le bot
                    setCanPlaceBomb(false);
                    game.getGrid()[botY][botX] = 3;
                    return;
                }
            }
        }

        // 5. BLOQUÉ → POSER UNE BOMBE POUR DÉTRUIRE DES OBSTACLES
        if (game.getGrid()[botY][botX] == 0) {
            System.out.println("Bot " + botId + " pose une bombe pour détruire des obstacles");
            new Bombe(botX, botY, 2, game, gameGrid, tousLesJoueurs, bot, null);
            setCanPlaceBomb(false);
            game.getGrid()[botY][botX] = 3;
        }
    }

    // Classe interne pour stocker les informations de cible
    private static class CibleInfo {
        int x, y;
        double distance;

        CibleInfo(int x, int y, double distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
    }

    // Trouve la cible la plus proche (joueur ou autres bots)
    private CibleInfo trouverCibleLaPlusProche(PacMan_Personnage joueur, List<Bot_Personnage> autresBots) {
        CibleInfo cibleLaPlusProche = null;
        double distanceMin = Double.MAX_VALUE;

        // Vérifier le joueur principal
        if (joueur != null) {
            double distance = calculerDistance(getGridX(), getGridY(), joueur.getGridX(), joueur.getGridY());
            if (distance < distanceMin) {
                distanceMin = distance;
                cibleLaPlusProche = new CibleInfo(joueur.getGridX(), joueur.getGridY(), distance);
            }
        }

        // Vérifier les autres bots
        for (Bot_Personnage autreBot : autresBots) {
            if (autreBot != this && autreBot.estVivant()) {
                double distance = calculerDistance(getGridX(), getGridY(), autreBot.getGridX(), autreBot.getGridY());
                if (distance < distanceMin) {
                    distanceMin = distance;
                    cibleLaPlusProche = new CibleInfo(autreBot.getGridX(), autreBot.getGridY(), distance);
                }
            }
        }

        return cibleLaPlusProche;
    }

    // Calcule la distance de Manhattan entre deux points
    private double calculerDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    // Vérifie si une cible est à portée de bombe
    private boolean estAPorteeDeBombe(int bombeX, int bombeY, int cibleX, int cibleY) {
        int rayon = 2;

        // Même position
        if (bombeX == cibleX && bombeY == cibleY) {
            return true;
        }

        // Vérifier les 4 directions
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            for (int i = 1; i <= rayon; i++) {
                int nx = bombeX + dir[0] * i;
                int ny = bombeY + dir[1] * i;
                // Hors limites
                if (nx < 0 || ny < 0 || ny >= game.getGrid().length || nx >= game.getGrid()[0].length) {
                    break;
                }
                int cell = game.getGrid()[ny][nx];
                // Cible trouvée
                if (nx == cibleX && ny == cibleY) {
                    return true;
                }
                // Obstacle → arrêter dans cette direction
                if (cell == 1) { // Mur incassable
                    break;
                }
                if (cell == 2) { // Bloc destructible → cible peut être atteinte mais propagation s'arrête
                    if (nx == cibleX && ny == cibleY) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    // Méthodes utilitaires inchangées...
    private boolean isValid(int x, int y) {
        int[][] grid = game.getGrid();
        return x >= 0 && y >= 0 && y < grid.length && x < grid[0].length && grid[y][x] == 0;
    }

    private boolean estDansZoneDanger(int x, int y) {
        int[][] grid = game.getGrid();
        int rayon = 2;

        for (int by = 0; by < grid.length; by++) {
            for (int bx = 0; bx < grid[0].length; bx++) {
                if (grid[by][bx] == 3) { // Bombe trouvée
                    if (x == bx && y == by) return true; // Centre

                    int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                    for (int[] dir : directions) {
                        for (int i = 1; i <= rayon; i++) {
                            int nx = bx + dir[0] * i;
                            int ny = by + dir[1] * i;

                            if (nx < 0 || ny < 0 || ny >= grid.length || nx >= grid[0].length) break;

                            int cell = grid[ny][nx];
                            if (cell == 1) break; // Mur incassable → stop
                            if (nx == x && ny == y) return true;
                            if (cell == 2) break; // Bloc destructible → zone atteinte, mais stop propagation
                        }
                    }
                }
            }
        }
        return false;
    }

    private void seDeplacerVers(int x, int y) {
        int dx = x - getGridX();
        int dy = y - getGridY();

        if (dx == 1) deplacerADroite(game.getGrid()[0].length * CELL_SIZE);
        else if (dx == -1) deplacerAGauche();
        else if (dy == 1) deplacerEnBas(game.getGrid().length * CELL_SIZE);
        else if (dy == -1) deplacerEnHaut();
    }

    public void disparait() {
        this.estVivant = false;
        this.setVisible(false);
    }

    public boolean estVivant() {
        return this.estVivant;
    }
}