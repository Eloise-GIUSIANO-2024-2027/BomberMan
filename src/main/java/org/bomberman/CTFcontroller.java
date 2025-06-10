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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bomberman.entite.Bombe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CTFcontroller {

    @FXML
    private VBox pauseMenuContainer;
    @FXML
    private VBox finMenuContainer;

    private Timeline gameTimer;
    private int tempsRestant = 120;

    private List<PacMan_Personnage> joueurs = new ArrayList<>();
    private List<Bot_Personnage> bot = new ArrayList<>();
    private List<Bombe> listeBombes = new ArrayList<>();

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
        PacMan_Personnage pacman = new Pacman(game, 0, 0,1);
        PacMan_Personnage fantome = new Pacman(game, 12, 10,2);
        PacMan_Personnage pacman2 = new Pacman(game, 12, 0,3);
        PacMan_Personnage pacman3 = new Pacman(game, 0, 10,4);
        Drapeau drapeau1 = new Drapeau(0, 0, pacman);
        Drapeau drapeau2 = new Drapeau(12, 10, fantome);
        Drapeau drapeau3 = new Drapeau(12, 0, pacman2);
        Drapeau drapeau4 = new Drapeau(0, 10, pacman3);
        gameGridDisplay.getChildren().addAll(drapeau1, drapeau2, drapeau3, drapeau4);
        pacman.setDrapeau(drapeau1);
        fantome.setDrapeau(drapeau2);
        pacman2.setDrapeau(drapeau3);
        pacman3.setDrapeau(drapeau4);

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

                if (game.getGrid()[px][py] == 0 && j1.estVivant()) {
                    System.out.println("Bombe");
                    new Bombe(px, py, 2, game, gameGridDisplay, joueurs, bot, listeBombes);
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

                if (game.getGrid()[py][px] == 0 && j2.estVivant()) {
                    System.out.println("Bombe");
                    // Le constructeur de Bombe attend (x, y) où x est la colonne et y est la ligne, donc (px, py) est correct ici
                    new Bombe(px, py, 2, game, gameGridDisplay, joueurs,bot, listeBombes);
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

                if (game.getGrid()[py][px] == 0 && j3.estVivant()) {
                    System.out.println("Bombe");

                    new Bombe(px, py, 2, game, gameGridDisplay, joueurs, bot, listeBombes);
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

                if (game.getGrid()[py][px] == 0 && j4.estVivant()) {
                    System.out.println("Bombe");

                    new Bombe(px, py, 2, game, gameGridDisplay, joueurs, bot,listeBombes);
                    gameGridDisplay.refresh();
                }
            }
        }
        verifierFinDePartie();
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

        if (joueursEnVie <= 1) {
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
        // Réinitialiser les listes de joueurs
        joueurs.clear();
        bot.clear(); // Même s'il n'y a pas de bots ici, garde-le pour la cohérence

        // Réinitialiser le timer
        if (gameTimer != null) {
            gameTimer.stop();
        }
        tempsRestant = 120;
        timerLabel.setText("TIMEUR : 02:00");

        // Réinitialiser l'état de fin de partie
        finMenuContainer.setVisible(false);
        finMenuContainer.setManaged(false);

        // Recréer le jeu
        game = new Game(); // recrée la logique de jeu (grille, états, etc.)
        gameGridDisplay = new GameGrid(game);
        gameArea.getChildren().clear();

        StackPane gameContainer = new StackPane();
        gameContainer.getChildren().add(gameGridDisplay);
        Pane entityLayer = gameGridDisplay.getEntityLayer();
        gameContainer.getChildren().add(entityLayer);
        gameArea.getChildren().add(gameContainer);

        int player1StartX = 0;
        int player1StartY = 0;
        int player2StartX = 12;
        int player2StartY = 10;
        int player3StartX = 12;
        int player3StartY = 0;
        int player4StartX = 0;
        int player4StartY = 10;

        // Créer les personnages AVEC LEURS POSITIONS DE DÉPART
        PacMan_Personnage pacman = new Pacman(game, player1StartX, player1StartY, 1);
        PacMan_Personnage fantome = new Pacman(game, player2StartX, player2StartY, 2);
        PacMan_Personnage pacman2 = new Pacman(game, player3StartX, player3StartY, 3);
        PacMan_Personnage pacman3 = new Pacman(game, player4StartX, player4StartY, 4);

        // Crée les drapeaux en utilisant les positions de DÉPART des joueurs
        Drapeau drapeau1 = new Drapeau(player1StartX, player1StartY, pacman);
        Drapeau drapeau2 = new Drapeau(player2StartX, player2StartY, fantome);
        Drapeau drapeau3 = new Drapeau(player3StartX, player3StartY, pacman2);
        Drapeau drapeau4 = new Drapeau(player4StartX, player4StartY, pacman3);

        entityLayer.getChildren().addAll(drapeau1, drapeau2, drapeau3, drapeau4);
        entityLayer.getChildren().addAll(pacman, fantome, pacman2, pacman3); // Add players to entityLayer too

        pacman.setDrapeau(drapeau1);
        fantome.setDrapeau(drapeau2);
        pacman2.setDrapeau(drapeau3);
        pacman3.setDrapeau(drapeau4);

        joueurs.add(pacman);
        joueurs.add(fantome);
        joueurs.add(pacman2);
        joueurs.add(pacman3);

        // Focus
        gameContainer.requestFocus();
        gameContainer.setFocusTraversable(true);

        // Gérer les touches
        Scene scene = gameArea.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                }

                if (!paused) {
                    handlePlayerMovement(event, pacman, fantome, pacman2, pacman3);
                }
            });
        }

        // Redémarrer le timer
        lancerTimer();
    }


}
