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
import org.bomberman.entite.Bonus;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Iterator;
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

    public void initialize() {
        System.out.println("gameController initialisé.");
        if (startButton != null) {
            startButton.setVisible(true);
            startButton.setManaged(true);
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

        // Recréer les joueurs
        PacMan_Personnage pacman = new Pacman(game, 0, 0,1);
        PacMan_Personnage fantome = new Pacman(game, 12, 10,2);
        PacMan_Personnage pacman2 = new Pacman(game, 12, 0,3);
        PacMan_Personnage pacman3 = new Pacman(game, 0, 10,4);

        joueurs.add(pacman);
        joueurs.add(fantome);
        joueurs.add(pacman2);
        joueurs.add(pacman3);

        gameGridDisplay.getChildren().addAll(joueurs);

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

    private void checkBonusCollision(PacMan_Personnage joueur) {
        List<Bonus> activeBonuses = game.getActiveBonuses();
        for (int i = activeBonuses.size() - 1; i >= 0; i--) {
            Bonus bonus = activeBonuses.get(i);
            if (bonus.getBonusX() == joueur.getGridX() && bonus.getBonusY() == joueur.getGridY()) {
                // Utiliser la nouvelle méthode générique
                bonus.appliquerBonus(joueur);
                break;
            }
        }
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


    private void finDePartie() {
        System.out.println("Temps écoulé ! Partie terminée.");
        Platform.runLater(() -> {
            // afficher un message ou recharger la scène
            finMenuContainer.setVisible(true);
            finMenuContainer.setManaged(true);
        });
    }


    private void handlePlayerMovement(KeyEvent event, PacMan_Personnage j1, PacMan_Personnage j2, PacMan_Personnage j3, PacMan_Personnage j4) {
        GameGrid k = gameGridDisplay;

        switch (event.getCode()) {
            //Joueur 1
            case T -> {
                j1.deplacerEnHaut();
                checkBonusCollision(j1);
            }
            case G -> {
                j1.deplacerEnBas(k.getHeight());
                checkBonusCollision(j1);
            }
            case H -> {
                j1.deplacerADroite(k.getWidth());
                checkBonusCollision(j1); // ← AJOUTER
            }
            case F -> {
                j1.deplacerAGauche();
                checkBonusCollision(j1); // ← AJOUTER
            }
            case U -> {
                int px = j1.getGridX();
                int py = j1.getGridY();

                if (game.getGrid()[px][py] == 0 && j1.peutPlacerBombe()) {
                    System.out.println("Bombe");
                    // ← MODIFIER : Vérifier si le joueur a le bonus rayon
                    int rayon = j1.aBonusRayon() ? 2 : 1;
                    if (j1.aBonusRayon()) {
                        j1.consommerBonusRayon(); // Consommer le bonus
                    }
                    new Bombe(px, py, rayon, game, gameGridDisplay, joueurs, bot, j1, listeBombes);
                    j1.marquerBombePlacee();
                    gameGridDisplay.refresh();
                }
            }

                //Joueur 2
                case Z -> {
                    j2.deplacerEnHaut();
                    checkBonusCollision(j2); // ← AJOUTER
                }

                case S -> {
                    j2.deplacerEnBas(k.getHeight());
                    checkBonusCollision(j2); // ← AJOUTER
                }
                case D -> {
                    j2.deplacerADroite(k.getWidth());
                    checkBonusCollision(j2); // ← AJOUTER
                }
                case Q -> {
                    j2.deplacerAGauche();
                    checkBonusCollision(j2); // ← AJOUTER
                }
                case A -> {
                    int px2 = j2.getGridX();
                    int py2 = j2.getGridY();

                    if (game.getGrid()[py2][px2] == 0 && j2.peutPlacerBombe()) {
                        System.out.println("Bombe");
                        // ← MODIFIER : Vérifier si le joueur a le bonus rayon
                        int rayon = j2.aBonusRayon() ? 2 : 1;
                        if (j2.aBonusRayon()) {
                            j2.consommerBonusRayon();
                        }
                        new Bombe(px2, py2, rayon, game, gameGridDisplay, joueurs, bot, j2, listeBombes);
                        j2.marquerBombePlacee();
                        gameGridDisplay.refresh();
                    }
                }

                //Joueur 3
                case O -> {
                    j3.deplacerEnHaut();
                    checkBonusCollision(j3); // ← AJOUTER
                }
                case L -> {
                    j3.deplacerEnBas(k.getHeight());
                    checkBonusCollision(j3); // ← AJOUTER
                }
                case M -> {
                    j3.deplacerADroite(k.getWidth());
                    checkBonusCollision(j3); // ← AJOUTER
                }
                case K -> {
                    j3.deplacerAGauche();
                    checkBonusCollision(j3); // ← AJOUTER
                }
                case P -> {
                    int px3 = j3.getGridX();
                    int py3 = j3.getGridY();

                    if (game.getGrid()[py3][px3] == 0 && j3.peutPlacerBombe()) {
                        System.out.println("Bombe");
                        // ← MODIFIER : Vérifier si le joueur a le bonus rayon
                        int rayon = j3.aBonusRayon() ? 2 : 1;
                        if (j3.aBonusRayon()) {
                            j3.consommerBonusRayon();
                        }
                        new Bombe(px3, py3, rayon, game, gameGridDisplay, joueurs, bot, j3, listeBombes);
                        j3.marquerBombePlacee();
                        gameGridDisplay.refresh();
                    }
                }

                //Joueur 4
                case NUMPAD5 -> {
                    j4.deplacerEnHaut();
                    checkBonusCollision(j4); // ← AJOUTER
                }
                case NUMPAD2 -> {
                    j4.deplacerEnBas(k.getHeight());
                    checkBonusCollision(j4); // ← AJOUTER
                }
                case NUMPAD3 -> {
                    j4.deplacerADroite(k.getWidth());
                    checkBonusCollision(j4); // ← AJOUTER
                }
                case NUMPAD1 -> {
                    j4.deplacerAGauche();
                    checkBonusCollision(j4); // ← AJOUTER
                }
                case NUMPAD4 -> {
                    int px4 = j4.getGridX();
                    int py4 = j4.getGridY();

                    if (game.getGrid()[py4][px4] == 0 && j4.peutPlacerBombe()) {
                        System.out.println("Bombe");
                        // ← MODIFIER : Vérifier si le joueur a le bonus rayon
                        int rayon = j4.aBonusRayon() ? 2 : 1;
                        if (j4.aBonusRayon()) {
                            j4.consommerBonusRayon();
                        }
                        new Bombe(px4, py4, rayon, game, gameGridDisplay, joueurs, bot, j4, listeBombes);
                        j4.marquerBombePlacee();
                        gameGridDisplay.refresh();
                    }
                }
            }
        }
    }