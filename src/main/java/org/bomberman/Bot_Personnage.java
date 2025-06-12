package org.bomberman;

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

/**
 * La classe {@code Bot_Personnage} représente un personnage contrôlé par l'ordinateur
 * dans le jeu Bomberman. Elle étend la classe abstraite {@code Joueur_Personnage}
 * et implémente des logiques de comportement pour l'IA, incluant la recherche de bonus,
 * l'attaque du joueur, et le mouvement aléatoire.
 */
public class Bot_Personnage extends Joueur_Personnage {

    /**
     * Le rectangle visuel représentant le bot sur l'écran, avec une taille de 48x48 pixels.
     */
    private Rectangle rectangle = new Rectangle(48, 48);
    /**
     * La position X du bot sur la grille de jeu.
     */
    private int gridX;
    /**
     * La position Y du bot sur la grille de jeu.
     */
    private int gridY;
    /**
     * * Une référence à l'instance principale du jeu.
     */
    private Game game;
    /**
     * L'identifiant unique de ce bot.
     */
    private int botId;
    /**
     * Le numéro du bot, utilisé pour différencier les apparences ou les comportements.
     */
    private int botNumber;
    /**
     * Le thème graphique actuel utilisé pour le bot, chargé depuis un fichier.
     */
    private String theme = "default";
    /**
     * Un générateur de nombres aléatoires utilisé pour les mouvements aléatoires du bot.
     */
    private Random random = new Random();
    /**
     * La stratégie de comportement actuelle du bot (par exemple, "AGGRESSIVE", "DEFENSIVE", "MIXED").
     */
    private String strategie = "AGGRESSIVE";
    /**
     * Le rayon de détection du joueur par le bot.
     * Le bot peut détecter le joueur dans cette distance.
     */
    private int rayonDetection = 3;
    /**
     * Le timestamp du dernier placement de bombe par le bot.
     * Utilisé pour gérer le cooldown des bombes.
     */
    private long dernierePlacementBombe = 0;
    /**
     * Le timestamp de la dernière collecte de bonus par le bot.
     * Utilisé pour gérer le cooldown des bonus.
     */
    private long derniereCollecteBonus = 0;
    /**
     * La durée minimale en millisecondes entre deux placements de bombe par le bot.
     */
    private static final long COOLDOWN_BOMBE_BOT = 1000;
    /**
     * La durée minimale en millisecondes entre deux collectes de bonus par le bot.
     */
    private static final long COOLDOWN_BONUS = 1000; // 1 seconde

    /**
     * Construit une nouvelle instance de {@code Bot_Personnage}.
     * Initialise le bot avec sa position de départ, son identifiant,
     * et charge les ressources graphiques basées sur un thème.
     *
     * @param game L'instance du jeu à laquelle ce bot appartient.
     * @param startX La position X initiale du bot sur la grille.
     * @param startY La position Y initiale du bot sur la grille.
     * @param botId L'identifiant unique de ce bot.
     * @param botNumber Le numéro du bot, utilisé pour les ressources graphiques.
     * @throws IOException Si une erreur survient lors de la lecture du fichier de thème.
     */
    public Bot_Personnage(Game game, int startX, int startY, int botId,int botNumber) throws IOException {
        super(game, startX, startY, botNumber);
        this.game = game;
        this.gridX = startX;
        this.gridY = startY;
        this.botId = botId;
        this.botNumber = botNumber;
        Path path = Paths.get("src/main/resources/data.txt");
        System.out.println(Files.readString(path));
        this.theme = Files.readString(path);

        rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-back-"+theme+"-"+botNumber+".gif")), 32, 32, false, false)));
        super.getChildren().add(rectangle);
        updatePixelPosition();
    }

    /**
     * Définit le comportement du bot pour un tour de jeu.
     * Le bot priorise la collecte de bonus, l'attaque du joueur, la poursuite du joueur,
     * et enfin un mouvement aléatoire si aucune autre action n'est possible ou nécessaire.
     *
     * @param joueurPrincipal Le joueur principal que le bot peut attaquer ou poursuivre.
     * @param tousJoueurs Une liste de tous les joueurs présents dans le jeu (non utilisé dans cette implémentation).
     * @param gameGrid La grille du jeu, utilisée pour vérifier les déplacements.
     * @param autresBots Une liste des autres bots présents dans le jeu (non utilisé dans cette implémentation).
     */
    public void agir(Joueur_Personnage joueurPrincipal, List<Joueur_Personnage> tousJoueurs,
                     GameGrid gameGrid, List<Bot_Personnage> autresBots) {


        if (!this.estVivant() || joueurPrincipal == null || !joueurPrincipal.estVivant()) {
            return;
        }

        if (chercherEtCollecterBonus()) {
            System.out.println("Bot " + getPlayerNumber() + " - Collecte de bonus");
            return;
        }


        if (tentativeAttaqueJoueur(joueurPrincipal, gameGrid)) {
            System.out.println("Bot " + getPlayerNumber() + " - Attaque du joueur !");
            return;
        }


        if (seRapprocherDuJoueur(joueurPrincipal)) {
            System.out.println("Bot " + getPlayerNumber() + " - Poursuite du joueur");
            return;}

            mouvementAleatoire();
        System.out.println("Bot " + getPlayerNumber() + " - Mouvement aléatoire");


    }

    /**
     * Tente d'attaquer le joueur principal en plaçant une bombe si les conditions sont remplies.
     * Les conditions incluent la proximité du joueur, la disponibilité du cooldown de la bombe,
     * et la possibilité de placer une bombe sur la case actuelle.
     *
     * @param joueur Le joueur cible de l'attaque.
     * @param gameGrid La grille du jeu pour les vérifications de position.
     * @return {@code true} si une bombe a été placée avec succès, {@code false} sinon.
     */
    private boolean tentativeAttaqueJoueur(Joueur_Personnage joueur, GameGrid gameGrid) {
        int distanceX = Math.abs(this.getGridX() - joueur.getGridX());
        int distanceY = Math.abs(this.getGridY() - joueur.getGridY());
        int distanceTotale = distanceX + distanceY; // Distance de Manhattan

        // Conditions pour attaquer :
        boolean joueurAPortee = distanceTotale <= 2; // Joueur à 2 cases ou moins
        boolean peutPoserBombe = peutPlacerBombeMaintenant();
        boolean caseLibre = game.getGrid()[this.getGridY()][this.getGridX()] == 0;

        if (joueurAPortee && peutPoserBombe && caseLibre) {
            System.out.println(" Bot " + getPlayerNumber() + " ATTAQUE le joueur ! Distance: " + distanceTotale);

            int rayon = this.aBonusRayon() ? 2 : 1;
            if (this.aBonusRayon()) {
                this.consommerBonusRayon();
                System.out.println("Bot " + getPlayerNumber() + " utilise son bonus rayon !");
            }

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

    /**
     * Tente de faire fuir le bot d'une bombe récemment placée en se déplaçant vers une case sûre adjacente.
     * Le bot essaie de se déplacer dans une des quatre directions cardinales.
     */
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

    /**
     * Recherche et tente de collecter les bonus à proximité.
     * Si un bonus est sur la même case, il est collecté.
     * Si un bonus est à une case de distance, le bot se dirige vers lui.
     * Un cooldown est appliqué pour éviter des collectes répétées trop rapides.
     *
     * @return {@code true} si un bonus a été collecté ou si le bot se dirige vers un bonus, {@code false} sinon.
     */
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

    /**
     * Tente de rapprocher le bot du joueur principal.
     * Le bot se déplace soit horizontalement, soit verticalement, en priorisant
     * la direction qui réduit le plus la distance avec le joueur,
     * à condition que la position de destination soit valide.
     *
     * @param joueur Le joueur vers lequel le bot doit se rapprocher.
     * @return {@code true} si le bot a pu se déplacer pour se rapprocher du joueur, {@code false} sinon.
     */
    private boolean seRapprocherDuJoueur(Joueur_Personnage joueur) {
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

    /**
     * Effectue un mouvement aléatoire pour le bot.
     * Le bot choisit une direction aléatoire (haut, bas, gauche, droite)
     * et se déplace s'il s'agit d'une position valide.
     */
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

    /**
     * Vérifie si le bot peut placer une bombe à l'instant présent,
     * en tenant compte du cooldown de placement de bombe et de sa capacité à poser une bombe.
     *
     * @return {@code true} si le bot peut placer une bombe, {@code false} sinon.
     */
    private boolean peutPlacerBombeMaintenant() {
        long maintenant = System.currentTimeMillis();
        return (maintenant - dernierePlacementBombe) >= COOLDOWN_BOMBE_BOT && this.canPlaceBomb();
    }

    /**
     * Vérifie si une position donnée sur la grille est valide et libre.
     * Une position est valide si elle est dans les limites de la grille et si la case est libre (représentée par 0).
     *
     * @param x La coordonnée X de la position à vérifier.
     * @param y La coordonnée Y de la position à vérifier.
     * @return {@code true} si la position est valide et libre, {@code false} sinon.
     */
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