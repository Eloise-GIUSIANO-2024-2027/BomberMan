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
        PacMan_Personnage fantome = new Pacman(game, 12, 10);
        PacMan_Personnage pacman2 = new Pacman(game, 12, 0);
        PacMan_Personnage pacman3 = new Pacman(game, 0, 10);


        // positionnement du fantôme

        gameGridDisplay.getChildren().addAll(pacman, fantome, pacman2, pacman3);
        deplacer(pacman,fantome,pacman2,pacman3);
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
    public void deplacer(PacMan_Personnage j1, PacMan_Personnage j2, PacMan_Personnage j3, PacMan_Personnage j4) {
        // Appliquer l'événement clavier à la scène entière
        GameGrid k = gameGridDisplay;

        gameGridDisplay.getScene().setOnKeyPressed((KeyEvent event) -> {
            switch (event.getCode()) {
                case T -> j1.deplacerEnHaut();
                case G -> j1.deplacerEnBas(k.getHeight());
                case H -> j1.deplacerADroite(k.getWidth());
                case F -> j1.deplacerAGauche();
                case Z -> j2.deplacerEnHaut();
                case S -> j2.deplacerEnBas(k.getHeight());
                case D -> j2.deplacerADroite(k.getWidth());
                case Q -> j2.deplacerAGauche();
                case O -> j3.deplacerEnHaut();
                case L -> j3.deplacerEnBas(k.getHeight());
                case M -> j3.deplacerADroite(k.getWidth());
                case K -> j3.deplacerAGauche();
                case NUMPAD5 -> j4.deplacerEnHaut();
                case NUMPAD2 -> j4.deplacerEnBas(k.getHeight());
                case NUMPAD3 -> j4.deplacerADroite(k.getWidth());
                case NUMPAD1 -> j4.deplacerAGauche();
            }
        });
    }

    public void retourMenu(ActionEvent actionEvent) {
    }
}