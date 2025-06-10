package org.bomberman;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import org.bomberman.entite.Bombe;
import org.bomberman.entite.Bonus;

import java.util.*;

public class Bot_Personnage extends Group {
    private String direction = "bas";
    private Rectangle rectangle = new Rectangle(48, 48);
    private int gridX;
    private int gridY;
    private static final int CELL_SIZE = 50;
    private Game game;
    private boolean estVivant = true;
    private int botId;
    private boolean canPlaceBomb = true;
    private int botNumber;
    private List<Bombe> listeBombesBot = new ArrayList<>();
    private String theme = "default";
    private boolean aBonusRayon = false;
    public double vitesse;
    private double vitesseInitiale;

    public Bot_Personnage(Game game, int startX, int startY, int botId, int botNumber, double vitesse, double vitesseInitiale) {
        this.game = game;
        this.gridX = startX;
        this.gridY = startY;
        this.botId = botId;
        this.botNumber = botNumber;
        this.vitesse = 3.0;
        this.vitesseInitiale = this.vitesse;

        rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-back-" + theme + "-" + botNumber + ".gif")), 32, 32, false, false)));
        super.getChildren().add(rectangle);
        updatePixelPosition();
    }

    public double getVitesse() {
        return vitesse;
    }

    public void setVitesse(double vitesse) {
        this.vitesse = vitesse;
    }

    public double getVitesseInitiale() {
        return vitesseInitiale;
    }

    public boolean canPlaceBomb() {
        return canPlaceBomb;
    }

    public void setCanPlaceBomb(boolean canPlaceBomb) {
        this.canPlaceBomb = canPlaceBomb;
    }

    public void deplacerAGauche() {
        if (!estVivant) return;
        int nouvellePositionX = gridX - 1;

        if (isValidGridPosition(nouvellePositionX, gridY)) {
            gridX = nouvellePositionX;
            updatePixelPosition();
            checkBonusCollision();
        }

        if (!direction.equals("gauche")) {
            direction = "gauche";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-left-" + theme + "-" + botNumber + ".gif")), 32, 32, false, false)));
        }
    }

    public void deplacerADroite(double largeurJeu) {
        if (!estVivant) return;
        int nouvellePositionX = gridX + 1;

        if (isValidGridPosition(nouvellePositionX, gridY)) {
            gridX = nouvellePositionX;
            updatePixelPosition();
            checkBonusCollision();
        }

        if (!direction.equals("droite")) {
            direction = "droite";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-right-" + theme + "-" + botNumber + ".gif")), 32, 32, false, false)));
        }
    }

    public void deplacerEnBas(double hauteurJeu) {
        if (!estVivant) return;
        int nouvellePositionY = gridY + 1;

        if (isValidGridPosition(gridX, nouvellePositionY)) {
            gridY = nouvellePositionY;
            updatePixelPosition();
            checkBonusCollision();
        }
        if (!direction.equals("bas")) {
            direction = "bas";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-front-" + theme + "-" + botNumber + ".gif")), 32, 32, false, false)));
        }
    }

    public void deplacerEnHaut() {
        if (!estVivant) return;
        int nouvellePositionY = gridY - 1;

        if (isValidGridPosition(gridX, nouvellePositionY)) {
            gridY = nouvellePositionY;
            updatePixelPosition();
            checkBonusCollision();
        }

        if (!direction.equals("haut")) {
            direction = "haut";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-back-" + theme + "-" + botNumber + ".gif")), 32, 32, false, false)));
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

    public Group getImageView() {
        return this;
    }

    public void agir(PacMan_Personnage joueur, List<PacMan_Personnage> tousLesJoueurs, GameGrid gameGrid, List<Bot_Personnage> bot) {
        if (!estVivant) return;

        int botX = getGridX();
        int botY = getGridY();

        // 1. FUITE SI BOMBE PROCHE (priorité absolue)
        if (estDansZoneDanger(botX, botY)) {
            for (int[] dir : new int[][]{{0, -1}, {0, 1}, {-1, 0}, {1, 0}}) {
                int newX = botX + dir[0];
                int newY = botY + dir[1];
                if (!estDansZoneDanger(newX, newY) && isValid(newX, newY)) {
                    seDeplacerVers(newX, newY);
                    return;
                }
            }

            // Plan B : fuir vers une case libre même si elle reste dans la zone danger
            for (int[] dir : new int[][]{{0, -1}, {0, 1}, {-1, 0}, {1, 0}}) {
                int newX = botX + dir[0];
                int newY = botY + dir[1];
                if (isValid(newX, newY)) {
                    seDeplacerVers(newX, newY);
                    return;
                }
            }
            return;
        }

        // 2. RECHERCHE DE BONUS PROCHE (nouvelle priorité)
        Bonus bonusLePlusProche = trouverBonusLePlusProche();
        if (bonusLePlusProche != null) {
            double distanceBonus = calculerDistance(botX, botY, bonusLePlusProche.getBonusX(), bonusLePlusProche.getBonusY());

            // Si le bonus est très proche (distance <= 3), on y va en priorité
            if (distanceBonus <= 3) {
                System.out.println("Bot " + botId + " se dirige vers un bonus à (" + bonusLePlusProche.getBonusX() + "," + bonusLePlusProche.getBonusY() + ")");
                if (seDeplacerVersObjectif(bonusLePlusProche.getBonusX(), bonusLePlusProche.getBonusY())) {
                    return; // On a réussi à se déplacer vers le bonus
                }
            }
        }

        // 3. RECHERCHE DE LA CIBLE LA PLUS PROCHE (joueur ou autres bots)
        CibleInfo cibleLaPlusProche = trouverCibleLaPlusProche(joueur, bot);

        if (cibleLaPlusProche != null) {
            // 4. SI CIBLE À PORTÉE DE BOMBE → POSER UNE BOMBE
            if (estAPorteeDeBombe(botX, botY, cibleLaPlusProche.x, cibleLaPlusProche.y)) {
                if (canPlaceBomb() && game.getGrid()[botY][botX] == 0) {
                    System.out.println("Bot " + botId + " pose une bombe pour attaquer la cible à (" + cibleLaPlusProche.x + "," + cibleLaPlusProche.y + ")");
                    new Bombe(botX, botY, 2, game, gameGrid, tousLesJoueurs, bot, null, listeBombesBot);
                    setCanPlaceBomb(false);
                    game.getGrid()[botY][botX] = 3;
                    return;
                }
            }

            // 5. SINON → SE RAPPROCHER DE LA CIBLE
            if (seDeplacerVersObjectif(cibleLaPlusProche.x, cibleLaPlusProche.y)) {
                return;
            }
        }

        // 6. Si on a un bonus pas trop loin et qu'on n'a pas de cible prioritaire, aller vers le bonus
        if (bonusLePlusProche != null) {
            System.out.println("Bot " + botId + " se dirige vers un bonus éloigné à (" + bonusLePlusProche.getBonusX() + "," + bonusLePlusProche.getBonusY() + ")");
            if (seDeplacerVersObjectif(bonusLePlusProche.getBonusX(), bonusLePlusProche.getBonusY())) {
                return;
            }
        }

        // 7. BLOQUÉ → POSER UNE BOMBE POUR DÉTRUIRE DES OBSTACLES
        if (canPlaceBomb() && game.getGrid()[botY][botX] == 0) {
            System.out.println("Bot " + botId + " pose une bombe pour détruire des obstacles");
            new Bombe(botX, botY, 2, game, gameGrid, tousLesJoueurs, bot, null, listeBombesBot);
            setCanPlaceBomb(false);
            game.getGrid()[botY][botX] = 3;
        }
    }

    // Nouvelle méthode pour trouver le bonus le plus proche
    private Bonus trouverBonusLePlusProche() {
        List<Bonus> activeBonuses = game.getActiveBonuses();
        if (activeBonuses.isEmpty()) {
            return null;
        }

        Bonus bonusLePlusProche = null;
        double distanceMin = Double.MAX_VALUE;

        for (Bonus bonus : activeBonuses) {
            double distance = calculerDistance(getGridX(), getGridY(), bonus.getBonusX(), bonus.getBonusY());
            if (distance < distanceMin) {
                distanceMin = distance;
                bonusLePlusProche = bonus;
            }
        }

        return bonusLePlusProche;
    }

    // Méthode améliorée pour se déplacer vers un objectif
    private boolean seDeplacerVersObjectif(int objectifX, int objectifY) {
        int botX = getGridX();
        int botY = getGridY();

        int dx = objectifX - botX;
        int dy = objectifY - botY;

        // Prioriser le mouvement selon la plus grande distance
        int[][] directions = Math.abs(dx) > Math.abs(dy) ?
                new int[][]{{Integer.signum(dx), 0}, {0, Integer.signum(dy)}} :
                new int[][]{{0, Integer.signum(dy)}, {Integer.signum(dx), 0}};

        for (int[] dir : directions) {
            int newX = botX + dir[0];
            int newY = botY + dir[1];
            if (isValid(newX, newY)) {
                seDeplacerVers(newX, newY);
                return true;
            }
        }

        return false; // Impossible de se déplacer vers l'objectif
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

    private CibleInfo trouverCibleLaPlusProche(PacMan_Personnage joueur, List<Bot_Personnage> autresBots) {
        CibleInfo cibleLaPlusProche = null;
        double distanceMin = Double.MAX_VALUE;

        if (joueur != null && joueur.estVivant()) {
            double distance = calculerDistance(getGridX(), getGridY(), joueur.getGridX(), joueur.getGridY());
            if (distance < distanceMin) {
                distanceMin = distance;
                cibleLaPlusProche = new CibleInfo(joueur.getGridX(), joueur.getGridY(), distance);
            }
        }

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

    private double calculerDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private boolean estAPorteeDeBombe(int bombeX, int bombeY, int cibleX, int cibleY) {
        int rayon = 2;

        if (bombeX == cibleX && bombeY == cibleY) {
            return true;
        }

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            for (int i = 1; i <= rayon; i++) {
                int nx = bombeX + dir[0] * i;
                int ny = bombeY + dir[1] * i;
                if (nx < 0 || ny < 0 || ny >= game.getGrid().length || nx >= game.getGrid()[0].length) {
                    break;
                }
                int cell = game.getGrid()[ny][nx];
                if (nx == cibleX && ny == cibleY) {
                    return true;
                }
                if (cell == 1) {
                    break;
                }
                if (cell == 2) {
                    if (nx == cibleX && ny == cibleY) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }

    private boolean isValid(int x, int y) {
        int[][] grid = game.getGrid();
        return x >= 0 && y >= 0 && y < grid.length && x < grid[0].length && grid[y][x] == 0;
    }

    private boolean estDansZoneDanger(int x, int y) {
        int[][] grid = game.getGrid();
        int rayon = 2;

        for (int by = 0; by < grid.length; by++) {
            for (int bx = 0; bx < grid[0].length; bx++) {
                if (grid[by][bx] == 3) {
                    if (x == bx && y == by) return true;

                    int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                    for (int[] dir : directions) {
                        for (int i = 1; i <= rayon; i++) {
                            int nx = bx + dir[0] * i;
                            int ny = by + dir[1] * i;

                            if (nx < 0 || ny < 0 || ny >= grid.length || nx >= grid[0].length) break;

                            int cell = grid[ny][nx];
                            if (cell == 1) break;
                            if (nx == x && ny == y) return true;
                            if (cell == 2) break;
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

        checkBonusCollision();
    }

    public void disparait() {
        this.estVivant = false;
        this.setVisible(false);
    }

    public boolean estVivant() {
        return this.estVivant;
    }

    public void activerBonusRayon() {
        this.aBonusRayon = true;
        System.out.println("Bot " + botId + " a reçu le bonus Rayon !");
    }

    public boolean aBonusRayon() {
        return aBonusRayon;
    }

    public void consommerBonusRayon() {
        this.aBonusRayon = false;
        System.out.println("Bot " + botId + " : Bonus Rayon consommé.");
    }

    private void checkBonusCollision() {
        List<Bonus> activeBonuses = game.getActiveBonuses();
        for (int i = activeBonuses.size() - 1; i >= 0; i--) {
            Bonus bonus = activeBonuses.get(i);
            if (bonus.getBonusX() == this.getGridX() && bonus.getBonusY() == this.getGridY()) {
                System.out.println("Bot " + botId + " a ramassé un bonus " + bonus.getTypeBonusString() + " !");
                bonus.appliquerBonus(this);
                break;
            }
        }
    }
}