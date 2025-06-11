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
import org.bomberman.entite.Bonus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class soloGameController {

    @FXML
    private VBox pauseMenuContainer;

    private boolean isPaused = false;

    @FXML

    private VBox gameAreaStackPane; // Référence au  FXML
    Game game = new Game();
    GameGrid gameGridDisplay = new GameGrid(game);

    @FXML
    private Button startButton;
    @FXML
    private Label timerLabel;
    @FXML
    private VBox finMenuContainer;

    // NOUVEAU: Label pour afficher le statut de la partie
    @FXML
    private Label gameStatusLabel;

    //  NOUVEAU: Label pour le résultat dans le menu de fin
    @FXML
    private Label resultLabel;

    private Timeline gameTimer;
    private Timeline botTimer;
    private int tempsRestant = 120;

    private List<PacMan_Personnage> joueurs = new ArrayList<>();
    private List<Bot_Personnage> bot = new ArrayList<>();
    private List<Bombe> listeBombes = new ArrayList<>();

    public soloGameController() throws IOException {
    }

    @FXML
    public void startGame() throws IOException {
        lancerTimer(); // debut du timer
        lancerTimerBots();

        gameGridDisplay = new GameGrid(game);

        gameAreaStackPane.getChildren().clear();
        gameAreaStackPane.getChildren().add(gameGridDisplay);

        //Acteurs du jeu
        PacMan_Personnage pacman = new Pacman(game, 12, 0, 1);
        Bot_Personnage bot1 = new Bot_Personnage(game, 12, 10, 1, 2);
        Bot_Personnage bot2 = new Bot_Personnage(game, 0, 0, 2, 3);
        Bot_Personnage bot3 = new Bot_Personnage(game, 0, 10, 3, 4);

        joueurs.add(pacman);
        bot.add(bot1);
        bot.add(bot2);
        bot.add(bot3);

        gameGridDisplay.getChildren().addAll(pacman, bot1, bot2, bot3);
        gameGridDisplay.requestFocus();

        Scene scene = gameAreaStackPane.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                }

                if (!isPaused) {
                    handlePlayerMovement(event, pacman);
                    verifierFinDePartie();
                }
            });
        }
    }

    private void lancerTimerBots() {
        botTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (!isPaused) {
                for (Bot_Personnage botPersonnage : bot) {
                    if (botPersonnage.estVivant()) {
                        PacMan_Personnage joueurPrincipal = joueurs.isEmpty() ? null : joueurs.get(0);
                        botPersonnage.agir(joueurPrincipal, joueurs, gameGridDisplay, bot);
                    }
                }
                verifierFinDePartie(); // Vérifier après chaque action des bots
            }
        }));
        botTimer.setCycleCount(Timeline.INDEFINITE);
        botTimer.play();
    }

    private void handlePlayerMovement(KeyEvent event, PacMan_Personnage j1) {
        GameGrid k = gameGridDisplay;

        switch (event.getCode()) {
            case Z -> {
                j1.deplacerEnHaut();
                checkBonusCollision(j1);
            }
            case S -> {
                j1.deplacerEnBas(k.getHeight());
                checkBonusCollision(j1);
            }
            case D -> {
                j1.deplacerADroite(k.getWidth());
                checkBonusCollision(j1);
            }
            case Q -> {
                j1.deplacerAGauche();
                checkBonusCollision(j1);
            }
            case A -> {
                int px = j1.getGridX();
                int py = j1.getGridY();

                if (game.getGrid()[py][px] == 0 && j1.estVivant() && j1.peutPlacerBombe()) {
                    System.out.println("Bombe posée par le joueur");
                    int rayon = j1.aBonusRayon() ? 2 : 1;
                    if (j1.aBonusRayon()) {
                        j1.consommerBonusRayon();
                    }
                    new Bombe(px, py, rayon, game, gameGridDisplay, joueurs, bot, j1, listeBombes);
                    j1.marquerBombePlacee();
                    gameGridDisplay.refresh();
                } else if (!j1.peutPlacerBombe()) {
                    long tempsRestant = j1.getTempsRestantCooldown();
                    System.out.println("Cooldown actif - attendez " + (tempsRestant/1000.0) + " secondes");
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
            if (botTimer != null) {
                botTimer.pause();
            }
        } else {
            if (gameTimer != null) {
                gameTimer.play();
            }
            if (botTimer != null) {
                botTimer.play();
            }
        }
    }

    @FXML
    public void retourMenu(ActionEvent event) {
        if (botTimer != null) {
            botTimer.stop();
        }
        if (gameTimer != null) {
            gameTimer.stop();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("menu.fxml"));
            Parent menuRoot = loader.load();
            Scene menuScene = new Scene(menuRoot, 820, 650);

            String cssPath = getClass().getResource("/styleMenu.css").toExternalForm();
            if (cssPath != null) {
                menuScene.getStylesheets().add(cssPath);
            } else {
                System.err.println("Erreur: Le fichier CSS 'styleMenu.css' n'a pas été trouvé.");
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(menuScene);
            stage.setTitle("Super Bomberman");
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
        if (botTimer != null) {
            botTimer.play();
        }
    }

    @FXML
    public void quittertout() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (botTimer != null) {
            botTimer.stop();
        }

        Platform.exit();
        System.exit(0);
    }

    private void lancerTimer() {
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tempsRestant--;
            int minutes = tempsRestant / 60;
            int secondes = tempsRestant % 60;
            String tempsFormate = String.format("TIMEUR : %02d:%02d", minutes, secondes);

            Platform.runLater(() -> timerLabel.setText(tempsFormate));
            verifierFinDePartie();

            if (tempsRestant <= 0) {
                gameTimer.stop();
                if (botTimer != null) {
                    botTimer.stop();
                }
                finDePartie("TEMPS ÉCOULÉ !");
                timerLabel.setText("TIMEUR : 00:00");
            }
        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    // NOUVELLE LOGIQUE: Vérification spécifique pour le mode solo
    private void verifierFinDePartie() {
        long joueursEnVie = joueurs.stream().filter(PacMan_Personnage::estVivant).count();
        long botsEnVie = bot.stream().filter(Bot_Personnage::estVivant).count();

        // CAS 1: Le joueur est mort = DÉFAITE
        if (joueursEnVie == 0) {
            System.out.println("Le joueur est mort ! Défaite !");
            arreterJeu();
            finDePartie("VOUS AVEZ PERDU !");
            return;
        }

        // CAS 2: Tous les bots sont morts = VICTOIRE
        if (botsEnVie == 0) {
            System.out.println("Tous les bots sont morts ! Victoire !");
            arreterJeu();
            finDePartie("VOUS AVEZ GAGNÉ !");
            return;
        }

        // CAS 3: Plus qu'un seul survivant au total (inclut le cas où seul le joueur survit)
        if (joueursEnVie + botsEnVie <= 1) {
            arreterJeu();
            if (joueursEnVie == 1) {
                finDePartie("VOUS AVEZ GAGNÉ !");
            } else {
                finDePartie("VOUS AVEZ PERDU !");
            }
        }
    }

    // NOUVELLE MÉTHODE: Arrêter le jeu immédiatement
    private void arreterJeu() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (botTimer != null) {
            botTimer.stop();
        }
    }

    // MÉTHODE MODIFIÉE: Accepte un message personnalisé et type de résultat
    private void finDePartie(String message) {
        System.out.println("Fin de partie: " + message);

        Platform.runLater(() -> {
            // Configurer l'affichage selon le type de message
            configurerAffichageFinDePartie(message);

            finMenuContainer.setVisible(true);
            finMenuContainer.setManaged(true);
        });
    }


    private void configurerAffichageFinDePartie(String message) {
        boolean victoire = message.contains("GAGNÉ") || message.contains("VICTOIRE");

        //  Configuration du label principal
        if (gameStatusLabel != null) {
            gameStatusLabel.setText(message);
            gameStatusLabel.setVisible(true);
            gameStatusLabel.setManaged(true);

            if (victoire) {
                // Style pour la victoire (vert doré)
                gameStatusLabel.setStyle(
                        "-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: white; " +
                                "-fx-background-color: linear-gradient(to bottom, #228B22, #32CD32); " +
                                "-fx-padding: 40px; -fx-background-radius: 20px; -fx-alignment: center; " +
                                "-fx-effect: dropshadow(gaussian, black, 20, 0, 0, 0);"
                );
            } else {
                // Style pour la défaite (rouge)
                gameStatusLabel.setStyle(
                        "-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: white; " +
                                "-fx-background-color: linear-gradient(to bottom, #DC143C, #B22222); " +
                                "-fx-padding: 40px; -fx-background-radius: 20px; -fx-alignment: center; " +
                                "-fx-effect: dropshadow(gaussian, black, 20, 0, 0, 0);"
                );
            }
        }

        // Configuration du label dans le menu de fin
        if (resultLabel != null) {
            if (victoire) {
                resultLabel.setText("FÉLICITATIONS ! ");
                resultLabel.setStyle(
                        "-fx-font-size: 32px; -fx-font-weight: bold; " +
                                "-fx-text-fill: gold; -fx-effect: dropshadow(gaussian, green, 5, 0, 0, 0);"
                );
            } else {
                resultLabel.setText(" GAME OVER ");
                resultLabel.setStyle(
                        "-fx-font-size: 32px; -fx-font-weight: bold; " +
                                "-fx-text-fill: red; -fx-effect: dropshadow(gaussian, darkred, 5, 0, 0, 0);"
                );
            }
        }
    }

    // SURCHARGE: Garder la compatibilité avec l'ancienne méthode
    private void finDePartie() {
        finDePartie("PARTIE TERMINÉE");
    }

    @FXML
    public void replayGame() throws IOException {
        // Réinitialiser les listes
        joueurs.clear();
        bot.clear();

        // Réinitialiser le timer
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (botTimer != null) {
            botTimer.stop();
        }

        // Masquer les messages de statut
        if (gameStatusLabel != null) {
            gameStatusLabel.setVisible(false);
            gameStatusLabel.setManaged(false);
        }
        if (resultLabel != null) {
            resultLabel.setText("Résultat de la partie");
        }

        joueurs.clear();
        bot.clear();

        tempsRestant = 120;
        timerLabel.setText("TIMEUR : 02:00");

        finMenuContainer.setVisible(false);
        finMenuContainer.setManaged(false);

        game = new Game();
        gameGridDisplay = new GameGrid(game);

        gameAreaStackPane.getChildren().clear();
        gameAreaStackPane.getChildren().add(gameGridDisplay);

        PacMan_Personnage pacman = new Pacman(game, 0, 0, 1);
        Bot_Personnage bot1 = new Bot_Personnage(game, 12, 10, 1, 2);
        Bot_Personnage bot2 = new Bot_Personnage(game, 12, 0, 2, 3);
        Bot_Personnage bot3 = new Bot_Personnage(game, 0, 10, 3, 4);

        joueurs.add(pacman);
        bot.add(bot1);
        bot.add(bot2);
        bot.add(bot3);

        gameGridDisplay.getChildren().addAll(pacman, bot1, bot2, bot3);

        gameGridDisplay.requestFocus();
        Scene scene = gameAreaStackPane.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                }

                if (!isPaused) {
                    handlePlayerMovement(event, pacman);
                }
            });
        }

        lancerTimer();
        lancerTimerBots();
    }

    private void checkBonusCollision(PacMan_Personnage joueur) {
        List<Bonus> activeBonuses = game.getActiveBonuses();
        for (int i = activeBonuses.size() - 1; i >= 0; i--) {
            Bonus bonus = activeBonuses.get(i);
            if (bonus.getBonusX() == joueur.getGridX() && bonus.getBonusY() == joueur.getGridY()) {
                bonus.appliquerBonus(joueur);
                break;
            }
        }
    }
}