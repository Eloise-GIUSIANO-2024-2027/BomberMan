package org.bomberman.entite;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import org.bomberman.Game;
import org.bomberman.GameGrid;
import org.bomberman.Joueur_Personnage;

import java.util.Objects;

/**
 * Classe représentant un bonus dans le jeu Bomberman.
 * Un bonus peut être de type VITESSE ou RAYON et peut être collecté par les joueurs
 * pour obtenir des améliorations temporaires ou permanentes.
 *
 * @author Eloïse Giusiano
 */
public class Bonus {

    /** Position X du bonus sur la grille de jeu */
    private int bonusX;
    /** Position Y du bonus sur la grille de jeu */
    private int bonusY;
    /** Référence vers l'instance principale du jeu */
    private Game game;
    /** Référence vers la grille de jeu */
    private GameGrid gameGrid;
    /** Vue graphique du bonus affiché à l'écran */
    private ImageView imageView;
    /** Représentation textuelle du type de bonus */
    private String typeBonusString;
    /** Type énuméré du bonus */
    private TypeBonus type;

    /**
     * Retourne la position X du bonus sur la grille.
     *
     * @return la coordonnée X du bonus
     */
    public int getBonusX() {
        return bonusX;
    }

    /**
     * Retourne la position Y du bonus sur la grille.
     *
     * @return la coordonnée Y du bonus
     */
    public int getBonusY() {
        return bonusY;
    }

    /**
     * Retourne la vue graphique du bonus.
     *
     * @return l'ImageView représentant le bonus à l'écran
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Retourne le type énuméré du bonus.
     *
     * @return le type de bonus (VITESSE ou RAYON)
     */
    public TypeBonus getType() {
        return type;
    }

    /**
     * Retourne la représentation textuelle du type de bonus.
     *
     * @return une chaîne représentant le type de bonus ("VITESSE" ou "RAYON")
     */
    public String getTypeBonusString() {
        return typeBonusString;
    }

    /**
     * Énumération définissant les types de bonus disponibles dans le jeu.
     */
    public enum TypeBonus {
        /** Bonus qui augmente temporairement la vitesse du joueur */
        VITESSE,
        /** Bonus qui augmente le rayon d'explosion des bombes */
        RAYON
    }

    /**
     * Constructeur de la classe Bonus.
     * Crée un nouveau bonus à la position spécifiée avec le type donné.
     * Si aucun type n'est spécifié (null), un type aléatoire est généré.
     *
     * @param game référence vers l'instance principale du jeu
     * @param bonusX position X du bonus sur la grille (en coordonnées de grille)
     * @param bonusY position Y du bonus sur la grille (en coordonnées de grille)
     * @param gameGrid référence vers la grille de jeu
     * @param type type de bonus à créer, ou null pour génération aléatoire
     *
     * @throws RuntimeException si le chargement de l'image du bonus échoue
     */
    public Bonus(Game game, int bonusX, int bonusY, GameGrid gameGrid, TypeBonus type) {
        this.bonusX = bonusX;
        this.bonusY = bonusY;
        this.game = game;
        this.gameGrid = gameGrid;

        if (type != null) {
            this.type = type;
        } else {
            this.type = Math.random() < 0.5 ? TypeBonus.VITESSE : TypeBonus.RAYON;
        }

        String imagePath;
        if (this.type == TypeBonus.VITESSE) {
            imagePath = "/terrain/BONUS.png";
            this.typeBonusString = "VITESSE";
        } else {
            imagePath = "/terrain/BONUS2.png";
            this.typeBonusString = "RAYON";
        }

        try {
            Image bonusImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath)), 48, 48, false, false);
            imageView = new ImageView(bonusImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image du bonus: " + e.getMessage());
            imageView = new ImageView();
        }

        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        imageView.setX(bonusX * 48);
        imageView.setY(bonusY * 48);

        game.addBonus(this);

        System.out.println("Bonus " + typeBonusString + " créé à la position (" + bonusX + ", " + bonusY + ")");
    }


    /**
     * Applique l'effet du bonus au joueur spécifié.
     * Cette méthode gère l'application des différents types de bonus :
     * - VITESSE : augmente temporairement la vitesse du joueur pour 15 secondes
     * - RAYON : active le bonus de rayon d'explosion des bombes
     *
     * Après application, le bonus est automatiquement supprimé du jeu.
     *
     * @param joueur le joueur PacMan_Personnage qui collecte le bonus
     *
     * @throws RuntimeException si une erreur survient lors de l'activation du bonus rayon
     *
     * @see #appliquerBonusVitesse(Joueur_Personnage)
     * @see #supprimerBonus()
     */
    public void appliquerBonus(Joueur_Personnage joueur) {
        System.out.println("=== DÉBUT APPLICATION BONUS ===");
        System.out.println("Type de bonus : " + typeBonusString);
        System.out.println("Joueur : " + joueur.getPlayerNumber());

        if (typeBonusString.equals("VITESSE")) {
            System.out.println("Vitesse AVANT : " + joueur.vitesse);
            appliquerBonusVitesse(joueur);
            System.out.println("Vitesse APRÈS : " + joueur.vitesse);
        } else if (typeBonusString.equals("RAYON")) {
            System.out.println("Activation bonus rayon...");
            try {
                joueur.activerBonusRayon();
                System.out.println("Bonus rayon activé avec succès !");
            } catch (Exception e) {
                System.err.println("Erreur lors de l'activation du bonus rayon : " + e.getMessage());
                e.printStackTrace();
            }
        }

        supprimerBonus();
        System.out.println("=== FIN APPLICATION BONUS ===");
    }

    /**
     * Applique l'effet du bonus de vitesse au joueur.
     * Divise par 2 la vitesse du joueur (ce qui augmente sa rapidité de déplacement)
     * pendant une durée de 15 secondes, puis restaure la vitesse initiale.
     *
     * @param joueur le joueur auquel appliquer le bonus de vitesse
     *
     * @implNote Utilise une PauseTransition pour gérer la durée temporaire du bonus.
     *           La restauration de la vitesse s'effectue sur le thread JavaFX.
     */
    private void appliquerBonusVitesse(Joueur_Personnage joueur) {
        System.out.println("Bonus de vitesse activé pour le joueur ! Vitesse augmentée pour 15 secondes.");
        double vitesseInitiale = joueur.vitesse;
        joueur.vitesse = vitesseInitiale / 2.0;

        PauseTransition pause = new PauseTransition(Duration.seconds(15));
        pause.setOnFinished(event -> {
            Platform.runLater(() -> {
                joueur.vitesse = vitesseInitiale;
                System.out.println("Bonus de vitesse terminé pour le joueur. Vitesse rétablie.");
            });
        });
        pause.play();
    }

    /**
     * Supprime le bonus du jeu et de l'affichage.
     * Cette méthode retire l'ImageView du bonus de son conteneur parent
     * et supprime le bonus de la liste des bonus actifs dans le jeu.
     *
     * @implNote L'opération de suppression de l'interface graphique s'effectue
     *           sur le thread JavaFX via Platform.runLater() pour assurer
     *           la synchronisation des threads.
     */
    private void supprimerBonus() {
        Platform.runLater(() -> {
            if (imageView.getParent() != null) {
                ((Pane) imageView.getParent()).getChildren().remove(imageView);
            }
        });

        game.removeBonus(this);
    }
}
