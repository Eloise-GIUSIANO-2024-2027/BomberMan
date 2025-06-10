package org.bomberman;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bomberman.entite.Bombe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class soloGameController {


    @FXML
    private VBox pauseMenuContainer;

    private boolean isPaused = false;

    @FXML
    private VBox gameAreaStackPane; // Référence au StackPane dans FXML
    Game game = new Game();
    GameGrid gameGridDisplay = new GameGrid(game);

    @FXML
    private Button startButton; // Référence au bouton démarrer
    @FXML
    private Label timerLabel;
    @FXML
    private VBox finMenuContainer;
    private Timeline gameTimer;
    private int tempsRestant = 120;

    private List<PacMan_Personnage> joueurs = new ArrayList<>();
    private List<Bot_Personnage> bot = new ArrayList<>();

    @FXML
    public void startGame() {
        lancerTimer(); // debut du timer
        // Crée une instance de ta GameGrid personnalisée
        gameGridDisplay = new GameGrid(game);

        // Ajoute la GameGrid au StackPane central
        gameAreaStackPane.getChildren().clear(); // Vide le StackPane
        gameAreaStackPane.getChildren().add(gameGridDisplay);
        //Acteurs du jeu
        PacMan_Personnage pacman = new Pacman(game, 12, 0);
        Bot_Personnage bot1 = new Bot_Personnage(game, 12, 10, 1);
        Bot_Personnage bot2 = new Bot_Personnage(game, 0, 0, 2);
        Bot_Personnage bot3 = new Bot_Personnage(game, 0, 10, 3);
        joueurs.add(pacman);

        bot.add(bot1);
        bot.add(bot2);
        bot.add(bot3);

        gameGridDisplay.getChildren().addAll(pacman, bot1, bot2, bot3);
        // Donne le focus à gameGridDisplay pour recevoir les touches
        gameGridDisplay.requestFocus();

        // Ajoute le gestionnaire de touches sur la scène
        Scene scene = gameAreaStackPane.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                }

                if (!isPaused) {
                    // Appelle ta méthode de déplacement
                    handlePlayerMovement(event, pacman);
                    bot1.agir(pacman, joueurs, gameGridDisplay, bot);
                    bot2.agir(pacman, joueurs, gameGridDisplay, bot);
                    bot3.agir(pacman, joueurs, gameGridDisplay, bot);
                    verifierFinDePartie();
                }
            });
        }

    }

    private void handlePlayerMovement(KeyEvent event, PacMan_Personnage j1) {
        GameGrid k = gameGridDisplay;

        switch (event.getCode()) {
            //Joueur 1
            case Z -> j1.deplacerEnHaut();
            case S -> j1.deplacerEnBas(k.getHeight());
            case D -> j1.deplacerADroite(k.getWidth());
            case Q -> j1.deplacerAGauche();
            case A -> {
                int px = j1.getGridX(); // px is the column
                int py = j1.getGridY(); // py is the row

                if (game.getGrid()[py][px] == 0 && j1.estVivant()) { // This access is correct: [row][column]
                    System.out.println("Bombe");
                    // THE FIX IS HERE: Pass px (column) first, then py (row)
                    new Bombe( px, py, 2, game, gameGridDisplay, joueurs, bot);
                    gameGridDisplay.refresh();
                }
            }
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
        isPaused = !isPaused;

        pauseMenuContainer.setVisible(isPaused);
        pauseMenuContainer.setManaged(isPaused);
        if (isPaused) {
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
        isPaused = false;
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
            verifierFinDePartie();

            if (tempsRestant <= 0) {
                gameTimer.stop();
                finDePartie();
                timerLabel.setText("TIMEUR : 00:00");
            }
        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    private void verifierFinDePartie() {
        long joueursEnVie = joueurs.stream().filter(PacMan_Personnage::estVivant).count();
        long botsEnVie = bot.stream().filter(Bot_Personnage::estVivant).count();

        long totalVivant = joueursEnVie + botsEnVie;

        if (totalVivant <= 1) {
            if (gameTimer != null) {
                gameTimer.stop();
            }
            finDePartie();
        }
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
        // Réinitialiser les listes
        joueurs.clear();
        bot.clear();

        // Réinitialiser le timer
        if (gameTimer != null) {
            gameTimer.stop();
        }
        tempsRestant = 120;
        timerLabel.setText("TIMEUR : 02:00");

        // Masquer le menu de fin
        finMenuContainer.setVisible(false);
        finMenuContainer.setManaged(false);

        // Réinitialiser le jeu
        game = new Game();
        gameGridDisplay = new GameGrid(game);

        // Nettoyer l'affichage
        gameAreaStackPane.getChildren().clear();
        gameAreaStackPane.getChildren().add(gameGridDisplay);

        // Créer les entités
        PacMan_Personnage pacman = new Pacman(game, 0, 0);
        Bot_Personnage bot1 = new Bot_Personnage(game, 12, 10, 1);
        Bot_Personnage bot2 = new Bot_Personnage(game, 12, 0, 2);
        Bot_Personnage bot3 = new Bot_Personnage(game, 0, 10, 3);

        joueurs.add(pacman);
        bot.add(bot1);
        bot.add(bot2);
        bot.add(bot3);

        gameGridDisplay.getChildren().addAll(pacman, bot1, bot2, bot3);

        // Focus et écouteur clavier
        gameGridDisplay.requestFocus();
        Scene scene = gameAreaStackPane.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                }

                if (!isPaused) {
                    handlePlayerMovement(event, pacman);
                    bot1.agir(pacman, joueurs, gameGridDisplay, bot);
                    bot2.agir(pacman, joueurs, gameGridDisplay, bot);
                    bot3.agir(pacman, joueurs, gameGridDisplay, bot);
                }
            });
        }

        // Relancer le timer
        lancerTimer();
    }

}
