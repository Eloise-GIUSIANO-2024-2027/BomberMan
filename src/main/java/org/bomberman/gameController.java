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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import org.bomberman.entite.Bombe;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

public class gameController {

    @FXML
    private VBox pauseMenuContainer;
    @FXML
    private VBox finMenuContainer;

    private Timeline gameTimer;
    private int tempsRestant = 120;

    private List<PacMan_Personnage> joueurs = new ArrayList<>();
    private List<Bot_Personnage> bot = new ArrayList<>();

    private boolean paused = false;

    @FXML
    private VBox gameArea;
    Game game = new Game();

    private GameGrid gameGridDisplay;
    @FXML
    private Button startButton; // Référence au bouton démarrer

    @FXML
    private Label timerLabel;

    @FXML
    public void startGame() throws IOException {
        lancerTimer(); // debut du timer

        // Crée une instance de ta GameGrid personnalisée
        gameGridDisplay = new GameGrid(game);

        gameArea.getChildren().clear();

        // Créer un conteneur avec couches
        StackPane gameContainer = new StackPane();

        // Ajouter la grille de terrain
        gameContainer.getChildren().add(gameGridDisplay);

        // Ajouter la couche pour les entités (personnages + bombes)
        Pane entityLayer = gameGridDisplay.getEntityLayer();
        gameContainer.getChildren().add(entityLayer);

        gameArea.getChildren().add(gameContainer);

        // Créer les personnages
        PacMan_Personnage pacman = new Pacman(game, 0, 0);
        PacMan_Personnage fantome = new Pacman(game, 12, 10);
        PacMan_Personnage pacman2 = new Pacman(game, 12, 0);
        PacMan_Personnage pacman3 = new Pacman(game, 0, 10);

        joueurs.add(pacman);
        joueurs.add(fantome);
        joueurs.add(pacman2);
        joueurs.add(pacman3);

        // Ajouter les personnages DIRECTEMENT à la grille comme avant
        gameGridDisplay.getChildren().addAll(joueurs);

        // Focus et événements
        gameContainer.requestFocus();
        gameContainer.setFocusTraversable(true);

        Scene scene = gameArea.getScene();
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

    private void handlePlayerMovement(KeyEvent event, PacMan_Personnage j1, PacMan_Personnage j2, PacMan_Personnage j3, PacMan_Personnage j4) {
        GameGrid k = gameGridDisplay;

        switch (event.getCode()) {
            //Joueur 1
            case T -> j1.deplacerEnHaut();
            case G -> j1.deplacerEnBas(k.getHeight());
            case H -> j1.deplacerADroite(k.getWidth());
            case F -> j1.deplacerAGauche();
            case U -> {
                int px = j1.getGridX();
                int py = j1.getGridY();

                if (game.getGrid()[px][py] == 0) {
                    System.out.println("Bombe");
                    new Bombe(px, py, 2, game, gameGridDisplay, joueurs, bot);
                    gameGridDisplay.refresh();
                }
            }

            //Joueur 2
            case Z -> j2.deplacerEnHaut();
            case S -> j2.deplacerEnBas(k.getHeight());
            case D -> j2.deplacerADroite(k.getWidth());
            case Q -> j2.deplacerAGauche();
            case A -> {
                int px = j2.getGridX();
                int py = j2.getGridY();

                if (game.getGrid()[py][px] == 0) {
                    System.out.println("Bombe");
                    // Le constructeur de Bombe attend (x, y) où x est la colonne et y est la ligne, donc (px, py) est correct ici
                    new Bombe(px, py, 2, game, gameGridDisplay, joueurs,bot);
                    gameGridDisplay.refresh();
                }
            }

            //Joueur 3
            case O -> j3.deplacerEnHaut();
            case L -> j3.deplacerEnBas(k.getHeight());
            case M -> j3.deplacerADroite(k.getWidth());
            case K -> j3.deplacerAGauche();
            case P -> {
                int px = j3.getGridX();
                int py = j3.getGridY();

                if (game.getGrid()[py][px] == 0) {
                    System.out.println("Bombe");

                    new Bombe(px, py, 2, game, gameGridDisplay, joueurs, bot);
                    gameGridDisplay.refresh();
                }
            }

            //Joueur 4
            case NUMPAD5 -> j4.deplacerEnHaut();
            case NUMPAD2 -> j4.deplacerEnBas(k.getHeight());
            case NUMPAD3 -> j4.deplacerADroite(k.getWidth());
            case NUMPAD1 -> j4.deplacerAGauche();
            case NUMPAD4 -> {
                int px = j4.getGridX();
                int py = j4.getGridY();

                if (game.getGrid()[py][px] == 0) {
                    System.out.println("Bombe");

                    new Bombe(px, py, 2, game, gameGridDisplay, joueurs, bot);
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