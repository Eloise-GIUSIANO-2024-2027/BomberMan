package org.bomberman;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    // Méthode appelée lorsque le bouton "NORMAL GAME" est cliqué
    @FXML
    private void startMultiGame(ActionEvent event) {
        System.out.println("Démarrer le jeu Normal !");
        // Logique pour passer à la scène du jeu principal
        try {
            // Charge le FXML du jeu (game.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game.fxml"));
            Parent gameRoot = loader.load();
            Scene gameScene = new Scene(gameRoot, 800, 630);

            // Récupère le stage actuel
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(gameScene);
            stage.setTitle("Super Bomberman - Le Jeu");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du jeu : " + e.getMessage());
        }
    }

    // Méthode appelée lorsque le bouton "BATTLE MODE" est cliqué
    @FXML
    private void startSoloMode(ActionEvent event) {
        System.out.println("Démarrer le mode Solo !");
        // Implémente la logique spécifique au mode bataille ici
        // Cela pourrait charger une autre scène FXML ou configurer le jeu différemment.
    }

    // Méthode appelée lorsque le bouton "PASSWORD" est cliqué
    @FXML
    private void changementTheme(ActionEvent event) {
        System.out.println("Changement du theme !");
        // Implémente les futur theme
    }

    @FXML
    public void regleCommande(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("regle.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Bomberman - Règles du Jeu");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Erreur lors du chargement de la page des règles : " + e.getMessage());
            }

    }


    @FXML
    public void quittertout() {
        Platform.exit(); // Fait sortir l'application JavaFX
        System.exit(0); // Optionnel: Assure la terminaison complète de la JVM (utile si des threads tournent en arrière-plan)

    }


    // Méthode d'initialisation du contrôleur (optionnel pour ce menu simple)
    @FXML
    public void initialize() {
        // Tu peux y ajouter du code d'initialisation pour le menu si nécessaire
    }

}
