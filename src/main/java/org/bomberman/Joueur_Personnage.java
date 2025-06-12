/**
 * Package principal de l'application Bomberman.
 */
package org.bomberman;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.application.Platform;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
/**
 * Classe de base pour un personnage joueur dans le jeu Bomberman.
 * Gère les propriétés communes aux joueurs, y compris la position, le mouvement,
 * la capacité à poser des bombes et les interactions avec les bonus et les drapeaux.
 */
public class Joueur_Personnage extends Group  {
    /**Direction actuelle du personnage ("haut", "bas", "gauche", "droite")*/
    private String direction = "bas";
    /**Taille d'une case (48x48 comme dans GameGrid)*/
    protected Rectangle rectangle = new Rectangle(48, 48);
    /**Position X dans la grille (colonne)*/
    private int gridX = 0;
    /**Position Y dans la grille (ligne)*/
    private int gridY = 0;
    /**Taille d'une case de la grille en pixels*/
    private static final int CELL_SIZE = 50;
    /**Référence au modèle de jeu*/
    protected Game game;
    /**Indique si le personnage est vivant*/
    private boolean estVivant = true;

    /**Numéro du joueur (1, 2, etc.)*/
    protected int playerNumber =1;
    /**Thème actuel pour les textures du personnage*/
    private String theme = "default";
    /**Vitesse de déplacement du personnage*/
    public double vitesse = 0.1;

    /**Timestamp du dernier placement de bombe*/
    private long dernierePlacementBombe = 0;
    /**Temps de rechargement entre deux placements de bombes (en ms)*/
    private static final long COOLDOWN_BOMBE = 350;

    /**Vrai si le joueur a le bonus de rayon d'explosion étendu*/
    private boolean aBonusRayon = false;
    /**Indique si le joueur peut placer une bombe (hors cooldown)*/
    private boolean canPlaceBomb = true;

    /**nom du joueur*/
    public String nom;
    /**score du joueur*/
    public int score;

    // Pour le mode Capture The Flag
    /**Le drapeau appartenant à ce joueur*/
    private Drapeau monDrapeau;
    /**Vrai si le drapeau de ce joueur a été capturé*/
    private boolean aEteCapture = false;
    /**Compteur des drapeaux ennemis capturés par ce joueur*/
    private int drapeauxCaptures = 0;


    /**
     * Retourne le score actuel du joueur.
     *
     * @return Le score du joueur.
     */
    public int getScore() {return this.score;}

    /**
     * Définit la vitesse de déplacement du joueur.
     *
     * @param vitesse La nouvelle vitesse du joueur.
     */
    public void setVitesse(double vitesse) {
        this.vitesse = vitesse;
    }

    /**
     * Retourne la vitesse de déplacement actuelle du joueur.
     *
     * @return La vitesse du joueur.
     */
    public double getVitesse() {
        return vitesse;
    }

    /**
     * Vérifie si le joueur peut placer une bombe.
     *
     * @return true si le joueur peut placer une bombe, false sinon.
     */
    public boolean canPlaceBomb() {
        return canPlaceBomb;
    }

    /**
     * Définit la capacité du joueur à placer une bombe.
     *
     * @param canPlaceBomb true pour autoriser le placement de bombe, false pour le désactiver.
     */
    public void setCanPlaceBomb(boolean canPlaceBomb) {
        this.canPlaceBomb = canPlaceBomb;
    }

    /**
     * Active le bonus de rayon pour les bombes du joueur.
     */
    public void activerBonusRayon() {
        this.aBonusRayon = true;
        System.out.println("Joueur a reçu le bonus Rayon !");
    }

    /**
     * Vérifie si le joueur a le bonus de rayon de bombe actif.
     *
     * @return true si le bonus de rayon est actif, false sinon.
     */
    public boolean aBonusRayon() {
        return aBonusRayon;
    }

    /**
     * Consomme le bonus de rayon, le rendant inactif.
     */
    public void consommerBonusRayon() {
        this.aBonusRayon = false;
        System.out.println("Bonus Rayon consommé.");
    }

    /**
     * Constructeur de la classe Joueur_Personnage.
     * Initialise un nouveau personnage joueur avec sa position de départ, son numéro de joueur et son thème.
     *
     * @param game L'instance du modèle de jeu.
     * @param startX La position X (colonne) de départ sur la grille.
     * @param startY La position Y (ligne) de départ sur la grille.
     * @param playerNumber Le numéro identifiant ce joueur.
     * @throws IOException Si une erreur d'entrée/sortie survient lors de la lecture du fichier de thème.
     */
    public Joueur_Personnage(Game game, int startX, int startY, int playerNumber) throws IOException {
        this.game = game;
        this.gridX = startX;
        this.gridY = startY;
        this.playerNumber = playerNumber;
        Path path = Paths.get("src/main/resources/data.txt");
        this.theme = Files.readString(path);

        rectangle.setFill(new ImagePattern(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/character/idle-front-"+theme+"-"+this.playerNumber+".gif")), 32, 32, false, false)));
        super.getChildren().add(this.rectangle);

        // Positionner le personnage selon sa position dans la grille
        updatePixelPosition();
    }

    /**
     * Met à jour la position visuelle du personnage en pixels sur la scène JavaFX
     * en fonction de sa position sur la grille.
     */
    public void updatePixelPosition() {
        double newX = gridX * CELL_SIZE;
        double newY = gridY * CELL_SIZE;

        // Utiliser TranslateX/Y au lieu de LayoutX/Y
        setTranslateX(newX);
        setTranslateY(newY);

    }

    /**
     * Vérifie si une position donnée sur la grille est valide pour le déplacement (c'est-à-dire vide).
     *
     * @param x La colonne (X) de la position à vérifier.
     * @param y La ligne (Y) de la position à vérifier.
     * @return true si la position est valide et vide, false sinon.
     */
    private boolean isValidGridPosition(int x, int y) {
        int[][] grid = game.getGrid();

        if (y < 0 || x < 0 || y >= grid.length || x >= grid[0].length) return false;

        return grid[y][x] == 0;
    }

    /**
     * Déplace le personnage d'une case vers la gauche s'il est vivant et si la destination est valide.
     * Met à jour l'image du personnage pour refléter la nouvelle direction.
     */
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

    /**
     * Déplace le personnage d'une case vers la droite s'il est vivant et si la destination est valide.
     * Met à jour l'image du personnage pour refléter la nouvelle direction.
     *
     * @param largeurJeu La largeur totale du jeu (non utilisée directement dans cette implémentation).
     */
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

    /**
     * Déplace le personnage d'une case vers le bas s'il est vivant et si la destination est valide.
     * Met à jour l'image du personnage pour refléter la nouvelle direction.
     *
     * @param hauteurJeu La hauteur totale du jeu (non utilisée directement dans cette implémentation).
     */
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

    /**
     * Déplace le personnage d'une case vers le haut s'il est vivant et si la destination est valide.
     * Met à jour l'image du personnage pour refléter la nouvelle direction.
     */
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

    /**
     * Vérifie si le joueur peut placer une bombe en tenant compte du cooldown et de son état de vie.
     *
     * @return true si le joueur peut placer une bombe, false sinon.
     */
    public boolean peutPlacerBombe() {
        long maintenant = System.currentTimeMillis();
        boolean cooldownOK = (maintenant - dernierePlacementBombe) >= COOLDOWN_BOMBE;
        return canPlaceBomb && cooldownOK && estVivant;
    }

    /**
     * Marque qu'une bombe a été placée par le joueur, activant le cooldown.
     */
    public void marquerBombePlacee() {
        this.dernierePlacementBombe = System.currentTimeMillis();
        this.canPlaceBomb = false;

        System.out.println("Joueur " + playerNumber + " - Cooldown de bombe activé pour 1 seconde");
    }


    /**
     * Active le cooldown pour le placement de bombes. Une fois le cooldown écoulé,
     * la capacité de placer des bombes est réactivée.
     */
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

    /**
     * Marque le personnage comme non vivant et le rend invisible.
     */
    public void disparait() {
        this.estVivant = false;
        this.setVisible(false);
    }

    /**
     * Vérifie si le personnage est vivant.
     *
     * @return true si le personnage est vivant, false sinon.
     */
    public boolean estVivant() {
        return this.estVivant;
    }

    /**
     * Retourne le numéro du joueur.
     *
     * @return Le numéro du joueur.
     */
    public int getPlayerNumber() {return this.playerNumber;}

    /**
     * Retourne la position X (colonne) du personnage sur la grille.
     *
     * @return La position X sur la grille.
     */
    public int getGridX() {
        return gridX;
    }

    /**
     * Retourne la position Y (ligne) du personnage sur la grille.
     *
     * @return La position Y sur la grille.
     */
    public int getGridY() {
        return gridY;
    }

    public interface Joueur {
        double vitesse = 0.1; // vitesse par défaut
        void setVitesse(double vitesse);
        double getVitesse();
    }


    // --- Pour le mode Capture The Flag ---
    /**
     * Définit le drapeau appartenant à ce joueur pour le mode Capture The Flag.
     *
     * @param d Le drapeau du joueur.
     */
    public void setMonDrapeau(Drapeau d) {
        this.monDrapeau = d;
    }

    /**
     * Retourne le drapeau appartenant à ce joueur.
     *
     * @return L'objet Drapeau du joueur.
     */
    public Drapeau getMonDrapeau() {
        return monDrapeau;
    }

    /**
     * Vérifie si le drapeau de ce joueur a été capturé par un ennemi.
     *
     * @return true si le drapeau du joueur a été capturé, false sinon.
     */
    public boolean aEteCapture() {
        return aEteCapture;
    }

    /**
     * Définit si le drapeau de ce joueur a été capturé.
     *
     * @param aEteCapture true si le drapeau a été capturé, false sinon.
     */
    public void setAEteCapture(boolean aEteCapture) {
        this.aEteCapture = aEteCapture;
    }

    /**
     * Retourne le nombre de drapeaux ennemis capturés par ce joueur.
     *
     * @return Le nombre de drapeaux capturés.
     */
    public int getDrapeauxCaptures() {
        return drapeauxCaptures;
    }

    /**
     * Incrémente le compteur de drapeaux ennemis capturés par ce joueur.
     */
    public void incrementerDrapeauxCaptures() {
        this.drapeauxCaptures++;
    }

    /**
     * Vérifie si ce joueur est sur un drapeau ennemi et le capture si c'est le cas.
     * Un joueur ne peut pas capturer son propre drapeau ni un drapeau déjà capturé.
     *
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