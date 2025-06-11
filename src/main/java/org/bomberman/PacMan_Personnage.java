package org.bomberman;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Platform;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import java.util.Objects;
import java.util.Objects;
import java.util.List;

public class PacMan_Personnage extends Group  {
    private String direction = "bas";
    protected Rectangle rectangle = new Rectangle(48, 48);
    private int gridX = 0; // Position X dans la grille (colonne)
    private int gridY = 0; // Position Y dans la grille (ligne)
    private static final int CELL_SIZE = 50; // Taille d'une case (48x48 comme dans GameGrid)
    protected Game game;
    private boolean estVivant = true;
    protected int playerNumber =1;// Initialise le joueur comme vivant par défaut
    private String theme = "wix";
    public double vitesse = 0.1;
    public boolean bonusRayonActif = false;

    private long dernierePlacementBombe = 0;
    private static final long COOLDOWN_BOMBE = 1000;

    private boolean aBonusRayon = false; // Pour le bonus de rayon
    private boolean canPlaceBomb = true;

    // ← AJOUTER ces méthodes
    public void setVitesse(double vitesse) {
        this.vitesse = vitesse;
    }

    public double getVitesse() {
        return vitesse;
    }

    // Pour le mode Capture The Flag
    private Drapeau monDrapeau; // Le drapeau appartenant à ce joueur
    private boolean aEteCapture = false; // Vrai si le drapeau de ce joueur a été capturé
    private int drapeauxCaptures = 0; // Compteur des drapeaux ennemis capturés par ce joueur

    public boolean canPlaceBomb() {
        return canPlaceBomb;
    }

    public void setCanPlaceBomb(boolean canPlaceBomb) {
        this.canPlaceBomb = canPlaceBomb;
    }

    public void activerBonusRayon() {
        this.aBonusRayon = true;
        System.out.println("Joueur a reçu le bonus Rayon !");
    }

    public boolean aBonusRayon() {
        return aBonusRayon;
    }

    public void consommerBonusRayon() {
        this.aBonusRayon = false;
        System.out.println("Bonus Rayon consommé.");
    }

    public PacMan_Personnage(Game game, int startX, int startY,int playerNumber) {
        this.game = game;
        this.gridX = startX;
        this.gridY = startY;
        this.playerNumber = playerNumber;

        rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-front-"+theme+"-"+this.playerNumber+".gif")), 32, 32, false, false)));
        super.getChildren().add(this.rectangle);

        // Positionner le personnage selon sa position dans la grille
        updatePixelPosition();
    }

    private void updatePixelPosition() {
        double newX = gridX * CELL_SIZE;
        double newY = gridY * CELL_SIZE;

        // Utiliser TranslateX/Y au lieu de LayoutX/Y
        setTranslateX(newX);
        setTranslateY(newY);

    }

    private boolean isValidGridPosition(int x, int y) {
        int[][] grid = game.getGrid();

        if (y < 0 || x < 0 || y >= grid.length || x >= grid[0].length) return false;

        return grid[y][x] == 0;
    }

    public void deplacerAGauche() {
        if (!estVivant) return;
        int nouvellePositionX = gridX - 1;

        if (isValidGridPosition(nouvellePositionX, gridY)) {
            gridX = nouvellePositionX;
            updatePixelPosition();
        }

        if (!direction.equals("gauche")) {
            direction = "gauche";
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-left-"+theme+"-"+playerNumber+".gif")), 32, 32, false, false)));
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
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-right-"+theme+"-"+playerNumber+".gif")), 32, 32, false, false)));
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
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-front-"+theme+"-"+playerNumber+".gif")), 32, 32, false, false)));
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
            rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-back-"+theme+"-"+playerNumber+".gif")), 32, 32, false, false)));
        }
    }

    public boolean peutPlacerBombe() {
        long maintenant = System.currentTimeMillis();
        boolean cooldownOK = (maintenant - dernierePlacementBombe) >= COOLDOWN_BOMBE;
        return canPlaceBomb && cooldownOK && estVivant;
    }

    public void marquerBombePlacee() {
        this.dernierePlacementBombe = System.currentTimeMillis();
        this.canPlaceBomb = false;

        System.out.println("Joueur " + playerNumber + " - Cooldown de bombe activé pour 1 seconde");
    }


    public void activerCooldownBombe() {
        this.canPlaceBomb = false;
        this.dernierePlacementBombe = System.currentTimeMillis();

        // Timer pour réactiver automatiquement après le cooldown
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    canPlaceBomb = true;
                    System.out.println("Joueur " + playerNumber + " peut de nouveau poser une bombe (cooldown terminé)");
                });
            }
        }, COOLDOWN_BOMBE);
    }

    public long getTempsRestantCooldown() {
        long maintenant = System.currentTimeMillis();
        long tempsEcoule = maintenant - dernierePlacementBombe;
        return Math.max(0, COOLDOWN_BOMBE - tempsEcoule);
    }

    public void disparait() {
        this.estVivant = false;
        this.setVisible(false);
    }

    public boolean estVivant() {
        return this.estVivant;
    }

    public int getPlayerNumber() {return this.playerNumber;}

    // Getters pour la position de grille (utiles pour debug)
    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public interface Joueur {
        double vitesse = 0.1; // vitesse par défaut
        void setVitesse(double vitesse);
        double getVitesse();
    }

    //Pour le catch the flagh
    // --- Pour le mode Capture The Flag ---

    public void setMonDrapeau(Drapeau d) {
        this.monDrapeau = d;
    }

    public Drapeau getMonDrapeau() {
        return monDrapeau;
    }

    public boolean aEteCapture() {
        return aEteCapture;
    }

    public void setAEteCapture(boolean aEteCapture) {
        this.aEteCapture = aEteCapture;
    }

    public int getDrapeauxCaptures() {
        return drapeauxCaptures;
    }

    public void incrementerDrapeauxCaptures() {
        this.drapeauxCaptures++;
    }

    /**
     * Vérifie si ce joueur est sur un drapeau ennemi et le capture si c'est le cas.
     * @param tousLesDrapeaux La liste de tous les drapeaux présents sur la carte.
     * @return true si un drapeau ennemi a été capturé, false sinon.
     */
    public boolean tenterCaptureDrapeau(List<Drapeau> tousLesDrapeaux) {
        if (!this.estVivant) { // Seuls les joueurs vivants peuvent capturer des drapeaux
            return false;
        }

        for (Drapeau drapeau : tousLesDrapeaux) {
            // Un joueur ne peut pas capturer son propre drapeau
            // Un joueur ne peut capturer un drapeau que s'il n'a pas déjà été capturé
            if (drapeau.getGridX() == this.gridX &&
                    drapeau.getGridY() == this.gridY &&
                    drapeau.getProprietaire() != this && // Vérifie que ce n'est pas son propre drapeau
                    !drapeau.isCaptured()) { // Vérifie qu'il n'est pas déjà capturé

                drapeau.setCaptured(true); // Marque le drapeau comme capturé
                drapeau.disparaitre(); // Rend le drapeau invisible
                this.incrementerDrapeauxCaptures(); // Incrémente le compteur de drapeaux capturés
                return true;
            }
        }
        return false;
    }
}