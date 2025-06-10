package org.bomberman.entite;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.animation.PauseTransition;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import org.bomberman.Game;
import org.bomberman.GameGrid;
import org.bomberman.PacMan_Personnage;
import org.bomberman.Bot_Personnage;

import java.util.List;
import java.util.Objects;

public class Bonus {
    private int bonusX;
    private int bonusY;
    private Game game;
    private GameGrid gameGrid;
    private ImageView imageView;
    private String typeBonusString;
    private TypeBonus type;

    public int getBonusX() {
        return bonusX;
    }

    public int getBonusY() {
        return bonusY;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TypeBonus getType() {
        return type;
    }

    public String getTypeBonusString() {
        return typeBonusString;
    }

    public enum TypeBonus {
        VITESSE,
        RAYON
    }

    public Bonus(Game game, int bonusX, int bonusY, GameGrid gameGrid, TypeBonus type) {
        this.bonusX = bonusX;
        this.bonusY = bonusY;
        this.game = game;
        this.gameGrid = gameGrid;

        this.type = Math.random() < 0.5 ? TypeBonus.VITESSE : TypeBonus.RAYON;

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
    }

    // Méthode spécifique pour PacMan_Personnage
    public void appliquerBonus(PacMan_Personnage joueur) {
        if (typeBonusString.equals("VITESSE")) {
            appliquerBonusVitesse(joueur);
        } else if (typeBonusString.equals("RAYON")) {
            joueur.activerBonusRayon();
        }

        supprimerBonus();
    }

    // Méthode spécifique pour Bot_Personnage
    public void appliquerBonus(Bot_Personnage bot) {
        if (typeBonusString.equals("VITESSE")) {
            appliquerBonusVitesse(bot);
        } else if (typeBonusString.equals("RAYON")) {
            bot.activerBonusRayon();
        }

        supprimerBonus();
    }

    private void appliquerBonusVitesse(PacMan_Personnage joueur) {
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

    private void appliquerBonusVitesse(Bot_Personnage bot) {
        System.out.println("Bonus de vitesse activé pour le bot ! Vitesse augmentée pour 15 secondes.");
        double vitesseInitiale = bot.vitesse;
        bot.vitesse = vitesseInitiale / 2.0;

        PauseTransition pause = new PauseTransition(Duration.seconds(15));
        pause.setOnFinished(event -> {
            Platform.runLater(() -> {
                bot.vitesse = vitesseInitiale;
                System.out.println("Bonus de vitesse terminé pour le bot. Vitesse rétablie.");
            });
        });
        pause.play();
    }

    private void supprimerBonus() {
        // Supprimer l'affichage du bonus
        Platform.runLater(() -> {
            if (imageView.getParent() != null) {
                ((Pane) imageView.getParent()).getChildren().remove(imageView);
            }
        });

        // Retirer le bonus de la liste des bonus actifs
        game.removeBonus(this);
    }
}
