// src/main/java/org/bomberman/gameController.java
package org.bomberman;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

public class gameController {

    @FXML
    private StackPane gameAreaStackPane; // Référence au StackPane dans FXML

    @FXML
    private Button startButton; // Référence au bouton démarrer

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

    public void initialize() {
        System.out.println("gameController initialisé.");
        if (startButton != null) {
            startButton.setVisible(true);
            startButton.setManaged(true);
        }
    }
}