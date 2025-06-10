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

import java.util.List;
import java.util.Objects;

// Assurez-vous que Joueur est correctement importé si cette classe n'est pas dans le même package
// import org.bomberman.entite.Joueur; // Cette ligne est nécessaire si Bonus n'est pas dans le même package que Joueur

public class Bonus {
    private int bonusX;
    private int bonusY;
    private Game game;
    private GameGrid gameGrid;
    private ImageView imageView;
    private String typeBonusString;
    private TypeBonus type;
    private List<PacMan_Personnage> joueurs;

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
            imagePath = "/terrain/BONUS2.png"; // ← Vous devez créer cette image
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

        // Configure l'ImageView
        imageView.setFitWidth(48);
        imageView.setFitHeight(48);
        imageView.setX(bonusX * 48); // Positionne l'ImageView
        imageView.setY(bonusY * 48);

    }

        // ← AJOUTER cette nouvelle méthode
        public void appliquerBonusRayon(PacMan_Personnage joueur) {
            System.out.println("Bonus rayon collecté !");

            Platform.runLater(() -> {
                if (imageView.getParent() != null) {
                    ((Pane) imageView.getParent()).getChildren().remove(imageView);
                }
            });
            game.removeBonus(this);

            joueur.activerBonusRayon();
        }

        public void appliquerBonusVitesse(PacMan_Personnage joueur, GridPane map, int bonusX, int bonusY) {
            System.out.println("Bonus de vitesse activé ! Vitesse du joueur augmentée pour 15 secondes.");

            Platform.runLater(() -> {
                if (imageView.getParent() != null) {
                    ((Pane) imageView.getParent()).getChildren().remove(imageView);
                }
            });
            game.removeBonus(this);

            double vitesseInitiale = joueur.vitesse;
            joueur.vitesse = vitesseInitiale / 2.0;

            PauseTransition pause = new PauseTransition(Duration.seconds(15));
            pause.setOnFinished(event -> {
                Platform.runLater(() -> {
                    joueur.vitesse = vitesseInitiale;
                    System.out.println("Bonus de vitesse terminé. Vitesse rétablie pour le joueur.");
                });
            });
            pause.play();
        }

    public void appliquerBonus(PacMan_Personnage joueur) {
        if (typeBonusString.equals("VITESSE")) {
            appliquerBonusVitesse(joueur, gameGrid, bonusX, bonusY);
        } else if (typeBonusString.equals("RAYON")) {
            appliquerBonusRayon(joueur);
        }
    }



}