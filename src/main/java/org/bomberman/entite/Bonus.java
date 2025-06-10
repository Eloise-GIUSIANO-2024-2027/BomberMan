package org.bomberman.entite;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView; // L'import est toujours nécessaire
import javafx.animation.PauseTransition;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.scene.layout.Pane; // Pour supprimer l'ImageView du parent
import org.bomberman.Game;
import org.bomberman.GameGrid;
import org.bomberman.PacMan_Personnage;

import java.util.Objects;

// Assurez-vous que Joueur est correctement importé si cette classe n'est pas dans le même package
// import org.bomberman.entite.Joueur; // Cette ligne est nécessaire si Bonus n'est pas dans le même package que Joueur

public class Bonus {
    private int bonusX;
    private int bonusY;
    private Game game;
    private GameGrid gameGrid;
    private ImageView imageView;

    public Bonus(Game game, int bonusX, int bonusY, GameGrid gameGrid) {
        this.bonusX = bonusX;
        this.bonusY = bonusY;
        this.game = game;
        this.gameGrid = gameGrid;

        try {
            Image bonusImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/terrain/BONUS.png")), 48, 48, false, false);
            imageView = new ImageView(bonusImage); // Crée l'ImageView
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image du bonus: " + e.getMessage());
            imageView = new ImageView(); // Fallback
        }

        // Configure l'ImageView
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        imageView.setX(bonusX * 48); // Positionne l'ImageView
        imageView.setY(bonusY * 48);

    }

    public void appliquerBonusVitesse(PacMan_Personnage joueur, GridPane map, int bonusX, int bonusY) {
        System.out.println("Bonus de vitesse activé ! Vitesse du joueur augmentée pour 15 secondes.");

        Platform.runLater(() -> {
            if (imageView.getParent() != null) {
                ((Pane) imageView.getParent()).getChildren().remove(imageView);
            }
        });
        game.removeBonus(this);

        double vitesseInitiale = joueur.vitesse; // ← Maintenant ça marche !
        joueur.vitesse = vitesseInitiale / 2.0; // Diviser par 2 = plus rapide

        PauseTransition pause = new PauseTransition(Duration.seconds(15));
        pause.setOnFinished(event -> {
            Platform.runLater(() -> {
                joueur.vitesse = vitesseInitiale;
                System.out.println("Bonus de vitesse terminé. Vitesse rétablie pour le joueur.");
            });
        });
        pause.play();

    }

    public int getBonusX() {
        return bonusX;
    }

    // ← AJOUTER cette méthode
    public ImageView getImageView() {
        return imageView;
    }

    public int getBonusY() {
        return bonusY;
    }
}