// src/main/java/org/bomberman/gameController.java
package org.bomberman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class gameController {

    @FXML
    private VBox pauseMenuContainer;
    @FXML
    private VBox finMenuContainer;

    private Timeline gameTimer;
    private int tempsRestant = 120;

    private boolean paused = false;

    @FXML
    private VBox gameAreaStackPane; // Référence au StackPane dans FXML
    Game game = new Game();


    // Crée une instance de ta GameGrid personnalisée
    // L'instance de ta classe GameGrid
    GameGrid gameGridDisplay = new GameGrid(game);
    @FXML
    private Button startButton; // Référence au bouton démarrer

    @FXML
    private Label timerLabel;

    @FXML
    public void startGame() throws IOException {
        // Initialise la logique du jeu
        // L'instance de la logique du jeu
        game.startGame(); // Appelle la méthode de démarrage de ta logique de jeu
        lancerTimer(); // debut du timer

        // Crée une instance de ta GameGrid personnalisée
        gameGridDisplay = new GameGrid(game);

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
        //deplacer(pacman,fantome,pacman2,pacman3);
        // Donne le focus à gameGridDisplay pour recevoir les touches
        gameGridDisplay.requestFocus();

        // Ajoute le gestionnaire de touches sur la scène
        Scene scene = gameAreaStackPane.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                }

                if (!paused) {
                    // Appelle ta méthode de déplacement
                    handlePlayerMovement(event, pacman, fantome, pacman2, pacman3);
                }
            });
        }

    }
        // positionnement du fantôme

    private void handlePlayerMovement(KeyEvent event, PacMan_Personnage j1, PacMan_Personnage j2, PacMan_Personnage j3, PacMan_Personnage j4) {
        GameGrid k = gameGridDisplay;

        switch (event.getCode()) {
            //Joueur 1
            case T -> j1.deplacerEnHaut();
            case G -> j1.deplacerEnBas(k.getHeight());
            case H -> j1.deplacerADroite(k.getWidth());
            case F -> j1.deplacerAGauche();

            //Joueur 2
            case Z -> j2.deplacerEnHaut();
            case S -> j2.deplacerEnBas(k.getHeight());
            case D -> j2.deplacerADroite(k.getWidth());
            case Q -> j2.deplacerAGauche();

            //Joueur 3
            case O -> j3.deplacerEnHaut();
            case L -> j3.deplacerEnBas(k.getHeight());
            case M -> j3.deplacerADroite(k.getWidth());
            case K -> j3.deplacerAGauche();

            //Joueur 4
            case NUMPAD5 -> j4.deplacerEnHaut();
            case NUMPAD2 -> j4.deplacerEnBas(k.getHeight());
            case NUMPAD3 -> j4.deplacerADroite(k.getWidth());
            case NUMPAD1 -> j4.deplacerAGauche();
        }
    }


    public void initialize() {
        System.out.println("gameController initialisé.");
        if (startButton != null) {
            startButton.setVisible(true);
            startButton.setManaged(true);
        }
    }

    private void togglePause() {
        paused = !paused;


        pauseMenuContainer.setVisible(paused);
        pauseMenuContainer.setManaged(paused);

        if (paused) {
            if (gameTimer != null) {
                gameTimer.pause();
            }
        } else {
            if (gameTimer != null) {
                gameTimer.play();
            }
        }
    }

    @FXML
    public void retourMenu(ActionEvent event) {
        try {
            // Charger le FXML du menu
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("menu.fxml"));
            Parent menuRoot = loader.load();
            Scene menuScene = new Scene(menuRoot, 820, 650);

            String cssPath = getClass().getResource("/styleMenu.css").toExternalForm();
            if (cssPath != null) {
                menuScene.getStylesheets().add(cssPath);
            } else {
                System.err.println("Erreur: Le fichier CSS 'styleMenu.css' n'a pas été trouvé. Vérifiez le chemin '/org/bomberman/styleMenu.css'.");
            }
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
    public void resumeGame() {
        paused = false;
        pauseMenuContainer.setVisible(false);
        pauseMenuContainer.setManaged(false);
        if (gameTimer != null) {
            gameTimer.play();
        }
    }

    @FXML
    public void quittertout() {
        Platform.exit(); // Fait sortir l'application JavaFX
        System.exit(0); // Optionnel: Assure la terminaison complète de la JVM (utile si des threads tournent en arrière-plan)

    }

    private void lancerTimer() {
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tempsRestant--;
            int minutes = tempsRestant / 60;
            int secondes = tempsRestant % 60;
            String tempsFormate = String.format("TIMEUR : %02d:%02d", minutes, secondes);

            // Met à jour le texte du Label dans l'interface
            Platform.runLater(() -> timerLabel.setText(tempsFormate));


            if (tempsRestant <= 0) {
                gameTimer.stop();
                finDePartie();
                timerLabel.setText("TIMEUR : 00:00");
            }
        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    private void finDePartie() {
        System.out.println("Temps écoulé ! Partie terminée.");
        Platform.runLater(() -> {
            // afficher un message ou recharger la scène
            finMenuContainer.setVisible(true);
            finMenuContainer.setManaged(true);
        });
    }
    @FXML
    public void replayGame() {

    }

}