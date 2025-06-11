package org.bomberman;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import org.bomberman.entite.Bombe;
import org.bomberman.entite.Bonus;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private int botNumber;// Identifiant unique pour chaque bot
    private List<Bombe> listeBombesBot = new ArrayList<>();
    private String theme = "default";
    private Random random = new Random();
    private String strategie = "AGGRESSIVE"; // AGGRESSIVE, DEFENSIVE, MIXED
    private int rayonDetection = 3; // Distance à laquelle le bot détecte le joueur
    private boolean enModePoursuiteJoueur = false;
    private long dernierePlacementBombe = 0;
    private long derniereCollecteBonus = 0;
    private static final long COOLDOWN_BOMBE_BOT = 1000; // 2 secondes
    private static final long COOLDOWN_BONUS = 1000; // 1 seconde

    public Bot_Personnage(Game game, int startX, int startY, int botId,int botNumber) throws IOException {
        this.game = game;
        this.gridX = startX;
        this.gridY = startY;
        this.botId = botId;
        this.botNumber = botNumber;
        Path path = Paths.get("src/main/resources/data.txt");
        System.out.println(Files.readString(path));
        this.theme = Files.readString(path);
        super(game, startX, startY, playerNumber);
        this.vitesse = vitesseX;

        rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-back-"+theme+"-"+botNumber+".gif")), 32, 32, false, false)));
        super.getChildren().add(rectangle);
        updatePixelPosition();
    }


    public void agir(PacMan_Personnage joueurPrincipal, List<PacMan_Personnage> tousJoueurs,
                     GameGrid gameGrid, List<Bot_Personnage> autresBots) {


        if (!this.estVivant() || joueurPrincipal == null || !joueurPrincipal.estVivant()) {
            return; // Ne rien faire si mort ou pas de joueur à attaquer


        System.out.println("Bot " + getPlayerNumber() + " - Position: (" + getGridX() + "," + getGridY() + ") - Action en cours...");

        //  PRIORITÉ 1: Collecter des bonus si disponibles à proximité
        if (chercherEtCollecterBonus()) {
            System.out.println("Bot " + getPlayerNumber() + " - Collecte de bonus");
            return;

        //  PRIORITÉ 2: Attaquer le joueur s'il est proche
        if (tentativeAttaqueJoueur(joueurPrincipal, gameGrid)) {
            System.out.println("Bot " + getPlayerNumber() + " - Attaque du joueur !");
            return;
        }

        // PRIORITÉ 3: Se rapprocher du joueur
        if (seRapprocherDuJoueur(joueurPrincipal)) {
            System.out.println("Bot " + getPlayerNumber() + " - Poursuite du joueur");
            return;

        //  PRIORITÉ 4: Mouvement aléatoire si rien d'autre à faire
        mouvementAleatoire();
        System.out.println("Bot " + getPlayerNumber() + " - Mouvement aléatoire");
    }


    private boolean tentativeAttaqueJoueur(PacMan_Personnage joueur, GameGrid gameGrid) {
        int distanceX = Math.abs(this.getGridX() - joueur.getGridX());
        int distanceY = Math.abs(this.getGridY() - joueur.getGridY());
        int distanceTotale = distanceX + distanceY; // Distance de Manhattan

        // Conditions pour attaquer :
        boolean joueurAPortee = distanceTotale <= 2; // Joueur à 2 cases ou moins
        boolean peutPoserBombe = peutPlacerBombeMaintenant();
        boolean caseLibre = game.getGrid()[this.getGridY()][this.getGridX()] == 0;

        if (joueurAPortee && peutPoserBombe && caseLibre) {
            System.out.println(" Bot " + getPlayerNumber() + " ATTAQUE le joueur ! Distance: " + distanceTotale);

            // Calculer le rayon optimal
            int rayon = this.aBonusRayon() ? 2 : 1;
            if (this.aBonusRayon()) {
                this.consommerBonusRayon();
                System.out.println("Bot " + getPlayerNumber() + " utilise son bonus rayon !");
            }

            // POSER LA BOMBE
            try {
                List<Bombe> listeBombes = new ArrayList<>(); // Vous devriez récupérer la vraie liste
                new Bombe(this.getGridX(), this.getGridY(), rayon, game, gameGrid,
                        List.of(), List.of(this), this, listeBombes);

                this.dernierePlacementBombe = System.currentTimeMillis();


                fuirDeLaBombe();

                return true;
            } catch (Exception e) {
                System.err.println("Erreur lors du placement de bombe par le bot: " + e.getMessage());
            }
        }

        return false;
    }


    private void fuirDeLaBombe() {
        // Essayer de se déplacer dans une direction sûre
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // Haut, Bas, Gauche, Droite

        for (int[] dir : directions) {
            int nouvelleX = this.getGridX() + dir[0];
            int nouvelleY = this.getGridY() + dir[1];

            if (positionValide(nouvelleX, nouvelleY)) {
                if (dir[0] == -1) this.deplacerAGauche();
                else if (dir[0] == 1) this.deplacerADroite(15); // Supposant une grille 15x15
                else if (dir[1] == -1) this.deplacerEnHaut();
                else if (dir[1] == 1) this.deplacerEnBas(15);

                System.out.println("Bot " + getPlayerNumber() + " fuit vers (" + nouvelleX + "," + nouvelleY + ")");
                break;
            }
        }
    }


    private boolean chercherEtCollecterBonus() {
        if (System.currentTimeMillis() - derniereCollecteBonus < COOLDOWN_BONUS) {
            return false; // Cooldown actif
        }

        List<Bonus> bonus = game.getActiveBonuses();

        for (Bonus b : bonus) {
            int distanceX = Math.abs(this.getGridX() - b.getBonusX());
            int distanceY = Math.abs(this.getGridY() - b.getBonusY());

            // Si un bonus est sur la même case
            if (distanceX == 0 && distanceY == 0) {
                b.appliquerBonus(this);
                derniereCollecteBonus = System.currentTimeMillis();
                System.out.println("✨ Bot " + getPlayerNumber() + " a collecté un bonus !");
                return true;
            }

            // Si un bonus est à 1 case, se diriger vers lui
            if (distanceX + distanceY == 1) {
                if (distanceX == 1) {
                    if (b.getBonusX() > this.getGridX()) {
                        this.deplacerADroite(15);
                    } else {
                        this.deplacerAGauche();
                    }
                } else {
                    if (b.getBonusY() > this.getGridY()) {
                        this.deplacerEnBas(15);
                    } else {
                        this.deplacerEnHaut();
                    }
                }
                System.out.println("Bot " + getPlayerNumber() + " se dirige vers un bonus");
                return true;
            }
        }

        return false;
    }

    private boolean seRapprocherDuJoueur(PacMan_Personnage joueur) {
        int deltaX = joueur.getGridX() - this.getGridX();
        int deltaY = joueur.getGridY() - this.getGridY();

        // Choisir la direction qui réduit le plus la distance
        boolean bougerX = Math.abs(deltaX) >= Math.abs(deltaY);

        if (bougerX && deltaX != 0) {
            // Se déplacer horizontalement
            if (deltaX > 0 && positionValide(this.getGridX() + 1, this.getGridY())) {
                this.deplacerADroite(15);
                return true;
            } else if (deltaX < 0 && positionValide(this.getGridX() - 1, this.getGridY())) {
                this.deplacerAGauche();
                return true;
            }
        } else if (deltaY != 0) {
            // Se déplacer verticalement
            if (deltaY > 0 && positionValide(this.getGridX(), this.getGridY() + 1)) {
                this.deplacerEnBas(15);
                return true;
            } else if (deltaY < 0 && positionValide(this.getGridX(), this.getGridY() - 1)) {
                this.deplacerEnHaut();
                return true;
            }
        }

        return false;
    }


    private void mouvementAleatoire() {
        int direction = random.nextInt(4);

        switch (direction) {
            case 0 -> {
                if (positionValide(getGridX(), getGridY() - 1)) {
                    deplacerEnHaut();
                }
            }
            case 1 -> {
                if (positionValide(getGridX(), getGridY() + 1)) {
                    deplacerEnBas(15);
                }
            }
            case 2 -> {
                if (positionValide(getGridX() - 1, getGridY())) {
                    deplacerAGauche();
                }
            }
            case 3 -> {
                if (positionValide(getGridX() + 1, getGridY())) {
                    deplacerADroite(15);
                }
            }
        }
    }


    private boolean peutPlacerBombeMaintenant() {
        long maintenant = System.currentTimeMillis();
        return (maintenant - dernierePlacementBombe) >= COOLDOWN_BOMBE_BOT && this.canPlaceBomb();
    }


    private boolean positionValide(int x, int y) {
        int[][] grid = game.getGrid();

        if (y < 0 || x < 0 || y >= grid.length || x >= grid[0].length) {
            return false;
        }

        return grid[y][x] == 0; // 0 = case libre
    }

    // GETTERS/SETTERS pour la configuration
    public void setStrategie(String strategie) {
        this.strategie = strategie;
    }

    public void setRayonDetection(int rayon) {
        this.rayonDetection = rayon;
    }
}