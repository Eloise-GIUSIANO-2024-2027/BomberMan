// src/main/java/org/bomberman/gameController.java
package org.bomberman;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent; // Important pour les événements de bouton
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node; // Pour accéder à la scène depuis l'événement
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class gameController {

    @FXML
    private VBox gameAreaStackPane; // Référence au StackPane dans FXML

    @FXML
    private Button startButton; // Référence au bouton démarrer

    @FXML
    private Button retourButton;

    private Game game; // L'instance de la logique du jeu
    private GameGrid gameGridDisplay; // L'instance de ta classe GameGrid

    @FXML
    public void startGame() {
        // Initialise la logique du jeu
        game = new Game();
        game.startGame(); // Appelle la méthode de démarrage de ta logique de jeu

        // Crée une instance de ta GameGrid personnalisée
        gameGridDisplay = new GameGrid(game);

        // Ajoute la GameGrid au StackPane central
        gameAreaStackPane.getChildren().clear(); // Vide le StackPane
        gameAreaStackPane.getChildren().add(gameGridDisplay); // Ajoute la GameGrid
    }


    @FXML
    public void retourMenu(ActionEvent event) {
        try {
            // Charger le FXML du menu
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("menu.fxml"));
            Parent menuRoot = loader.load();
            Scene menuScene = new Scene(menuRoot, 800, 630);
//
//            // Charger le CSS (assure-toi que ton fichier CSS est accessible et partagé)
//            menuScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());


            //Obtenir le Stage actuel et changer la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(menuScene);
            stage.setTitle("Super Bomberman"); // Remettre le titre du menu
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du menu : " + e.getMessage());
        }
    }


    @FXML
    public void regleCommande() {
        // a enoyer sur une autre page avec les regle et les commende
    }

    public void initialize() {
        System.out.println("gameController initialisé.");
        if (startButton != null) {
            startButton.setVisible(true);
            startButton.setManaged(true);
        }
    }
}