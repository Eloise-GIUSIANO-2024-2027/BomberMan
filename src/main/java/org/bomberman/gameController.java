// src/main/java/org/bomberman/gameController.java
package org.bomberman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class gameController {

    @FXML
    private VBox gameAreaStackPane; // Référence au StackPane dans FXML
    Game game = new Game();


    // Crée une instance de ta GameGrid personnalisée
    // L'instance de ta classe GameGrid
    GameGrid gameGridDisplay = new GameGrid(game);
    @FXML
    private Button startButton; // Référence au bouton démarrer

    @FXML
    public void startGame() throws IOException {
        // Initialise la logique du jeu
        // L'instance de la logique du jeu
        game.startGame(); // Appelle la méthode de démarrage de ta logique de jeu

        // Ajoute la GameGrid au StackPane central
        gameAreaStackPane.getChildren().clear(); // Vide le StackPane
        gameAreaStackPane.getChildren().add(gameGridDisplay);
        //Acteurs du jeu
        PacMan_Personnage pacman = new Pacman(game, 0, 0);
        PacMan_Personnage fantome = new PacMan_Fantome(game, 14, 14);


        // positionnement du fantôme

        gameGridDisplay.getChildren().addAll(pacman, fantome);
        deplacer(pacman,fantome);
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
    public void deplacer(PacMan_Personnage j1, PacMan_Personnage j2){
        // Appliquer l'événement clavier à la scène entière
        GameGrid k = gameGridDisplay;

        gameGridDisplay.getScene().setOnKeyPressed((KeyEvent event) -> {
            switch (event.getCode()) {
                case UP -> j1.deplacerEnHaut();
                case DOWN -> j1.deplacerEnBas(k.getHeight());
                case RIGHT -> j1.deplacerADroite(k.getWidth());
                case LEFT -> j1.deplacerAGauche();
                case Z -> j2.deplacerEnHaut();
                case S -> j2.deplacerEnBas(k.getHeight());
                case D -> j2.deplacerADroite(k.getWidth());
                case Q -> j2.deplacerAGauche();
            }
        });
    }

    public void retourMenu(ActionEvent actionEvent) {
    }
}