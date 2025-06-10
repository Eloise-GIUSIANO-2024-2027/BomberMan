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
            System.out.println("Bot " + botId + " fuit une bombe !");
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

        // 2. RECHERCHE DE LA CIBLE LA PLUS PROCHE
        CibleInfo cibleLaPlusProche = trouverCibleLaPlusProche(joueur, tousLesJoueurs, bot);

        if (cibleLaPlusProche != null) {
            double distanceCible = cibleLaPlusProche.distance;

            // 3. SI CIBLE TRÈS PROCHE → ATTAQUER
            if (distanceCible <= 2 && canPlaceBomb() && game.getGrid()[botY][botX] == 0) {
                System.out.println("Bot " + botId + " pose une bombe car cible très proche à distance " + distanceCible);
                new Bombe(botX, botY, 2, game, gameGrid, tousLesJoueurs, bot, null, listeBombesBot);
                setCanPlaceBomb(false);
                game.getGrid()[botY][botX] = 3;
                return;
            }

            // 4. SI CIBLE À PORTÉE DE BOMBE → ATTAQUER
            if (estAPorteeDeBombe(botX, botY, cibleLaPlusProche.x, cibleLaPlusProche.y) && canPlaceBomb() && game.getGrid()[botY][botX] == 0) {
                System.out.println("Bot " + botId + " pose une bombe pour attaquer la cible à (" + cibleLaPlusProche.x + "," + cibleLaPlusProche.y + ")");
                new Bombe(botX, botY, 2, game, gameGrid, tousLesJoueurs, bot, null, listeBombesBot);
                setCanPlaceBomb(false);
                game.getGrid()[botY][botX] = 3;
                return;
            }

            // 5. PATHFINDING INTELLIGENT : Chercher un chemin vers la cible
            List<int[]> chemin = trouverChemin(botX, botY, cibleLaPlusProche.x, cibleLaPlusProche.y);

            if (chemin != null && !chemin.isEmpty()) {
                // Il y a un chemin libre, suivre le chemin
                int[] prochainePas = chemin.get(0);
                if (isValid(prochainePas[0], prochainePas[1])) {
                    System.out.println("Bot " + botId + " suit un chemin vers la cible");
                    seDeplacerVers(prochainePas[0], prochainePas[1]);
                    return;
                }
            } else {
                // 6. PAS DE CHEMIN LIBRE → CASSER DES MURS STRATÉGIQUEMENT
                int[] murACasser = trouverMurACasser(botX, botY, cibleLaPlusProche.x, cibleLaPlusProche.y);
                if (murACasser != null && canPlaceBomb()) {
                    // Se diriger vers le mur à casser
                    if (Math.abs(murACasser[0] - botX) <= 1 && Math.abs(murACasser[1] - botY) <= 1) {
                        // On est adjacent au mur, poser une bombe
                        if (game.getGrid()[botY][botX] == 0) {
                            System.out.println("Bot " + botId + " casse un mur à (" + murACasser[0] + "," + murACasser[1] + ") pour atteindre la cible");
                            new Bombe(botX, botY, 2, game, gameGrid, tousLesJoueurs, bot, null, listeBombesBot);
                            setCanPlaceBomb(false);
                            game.getGrid()[botY][botX] = 3;
                            return;
                        }
                    } else {
                        // Se rapprocher du mur à casser
                        if (seDeplacerVersObjectif(murACasser[0], murACasser[1])) {
                            System.out.println("Bot " + botId + " se dirige vers un mur à casser");
                            return;
                        }
                    }
                }
            }

            // 7. FALLBACK : Se rapprocher de la cible même si bloqué
            if (seDeplacerVersObjectif(cibleLaPlusProche.x, cibleLaPlusProche.y)) {
                return;
            }
        }

        // 8. RECHERCHE DE BONUS (seulement si pas de cible prioritaire)
        Bonus bonusLePlusProche = trouverBonusLePlusProche();
        if (bonusLePlusProche != null) {
            double distanceBonus = calculerDistance(botX, botY, bonusLePlusProche.getBonusX(), bonusLePlusProche.getBonusY());

            if (distanceBonus <= 4) {
                System.out.println("Bot " + botId + " se dirige vers un bonus");
                if (seDeplacerVersObjectif(bonusLePlusProche.getBonusX(), bonusLePlusProche.getBonusY())) {
                    return;
                }
            }
        }

        // 9. DESTRUCTION ALÉATOIRE D'OBSTACLES
        if (canPlaceBomb() && game.getGrid()[botY][botX] == 0) {
            System.out.println("Bot " + botId + " pose une bombe pour détruire des obstacles");
            new Bombe(botX, botY, 2, game, gameGrid, tousLesJoueurs, bot, null, listeBombesBot);
            setCanPlaceBomb(false);
            game.getGrid()[botY][botX] = 3;
        }
    }

    // NOUVELLE MÉTHODE : Pathfinding simple avec BFS
    private List<int[]> trouverChemin(int startX, int startY, int targetX, int targetY) {
        int[][] grid = game.getGrid();
        boolean[][] visited = new boolean[grid.length][grid[0].length];
        Queue<PathNode> queue = new LinkedList<>();

        queue.offer(new PathNode(startX, startY, null));
        visited[startY][startX] = true;

        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        while (!queue.isEmpty()) {
            PathNode current = queue.poll();

            // Arrivé à destination
            if (current.x == targetX && current.y == targetY) {
                List<int[]> chemin = new ArrayList<>();
                PathNode node = current.parent; // On ignore la position de départ
                while (node != null && node.parent != null) {
                    chemin.add(0, new int[]{node.x, node.y});
                    node = node.parent;
                }
                return chemin;
            }

            // Explorer les voisins
            for (int[] dir : directions) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];

                if (newX >= 0 && newX < grid[0].length && newY >= 0 && newY < grid.length
                        && !visited[newY][newX] && grid[newY][newX] == 0) {
                    visited[newY][newX] = true;
                    queue.offer(new PathNode(newX, newY, current));
                }
            }
        }

        return null; // Pas de chemin trouvé
    }

    // Classe pour le pathfinding
    private static class PathNode {
        int x, y;
        PathNode parent;

        PathNode(int x, int y, PathNode parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }
    }

    // NOUVELLE MÉTHODE : Trouve le mur le plus stratégique à casser
    private int[] trouverMurACasser(int botX, int botY, int targetX, int targetY) {
        int[][] grid = game.getGrid();

        // Vérifier les murs dans la direction de la cible
        int dx = Integer.signum(targetX - botX);
        int dy = Integer.signum(targetY - botY);

        // Priorité 1: Murs directement dans la ligne de mire
        if (dx != 0) { // Mouvement horizontal
            for (int x = botX + dx; x != targetX && x >= 0 && x < grid[0].length; x += dx) {
                if (grid[botY][x] == 2) { // Mur destructible
                    return new int[]{x, botY};
                } else if (grid[botY][x] == 1) { // Mur indestructible
                    break;
                }
            }
        }

        if (dy != 0) { // Mouvement vertical
            for (int y = botY + dy; y != targetY && y >= 0 && y < grid.length; y += dy) {
                if (grid[y][botX] == 2) { // Mur destructible
                    return new int[]{botX, y};
                } else if (grid[y][botX] == 1) { // Mur indestructible
                    break;
                }
            }
        }

        // Priorité 2: Murs adjacents au bot
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] dir : directions) {
            int newX = botX + dir[0];
            int newY = botY + dir[1];

            if (newX >= 0 && newX < grid[0].length && newY >= 0 && newY < grid.length
                    && grid[newY][newX] == 2) {
                return new int[]{newX, newY};
            }
        }

        return null;
    }

    // Méthodes existantes...
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

    private boolean seDeplacerVersObjectif(int objectifX, int objectifY) {
        int botX = getGridX();
        int botY = getGridY();

        int dx = objectifX - botX;
        int dy = objectifY - botY;

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

        return false;
    }

    private static class CibleInfo {
        int x, y;
        double distance;

        CibleInfo(int x, int y, double distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
    }

    private CibleInfo trouverCibleLaPlusProche(PacMan_Personnage joueur, List<PacMan_Personnage> tousLesJoueurs, List<Bot_Personnage> autresBots) {
        CibleInfo cibleLaPlusProche = null;
        double distanceMin = Double.MAX_VALUE;

        for (PacMan_Personnage j : tousLesJoueurs) {
            if (j != null && j.estVivant()) {
                double distance = calculerDistance(getGridX(), getGridY(), j.getGridX(), j.getGridY());
                if (distance < distanceMin) {
                    distanceMin = distance;
                    cibleLaPlusProche = new CibleInfo(j.getGridX(), j.getGridY(), distance);
                }
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

        if (bombeX == cibleX) {
            return Math.abs(bombeY - cibleY) <= rayon;
        }
        if (bombeY == cibleY) {
            return Math.abs(bombeX - cibleX) <= rayon;
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