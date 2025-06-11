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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bomberman.entite.Bombe;
import org.bomberman.entite.Bonus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CTFcontroller {

    @FXML
    private VBox pauseMenuContainer;
    @FXML
    private VBox finMenuContainer;
    @FXML
    private Label messageFinPartieLabel;

    private Timeline gameTimer;
    private int tempsRestant = 120;

    private List<PacMan_Personnage> joueurs = new ArrayList<>();
    private List<Bot_Personnage> bot = new ArrayList<>();
    private List<Bombe> listeBombes = new ArrayList<>();
    private List<Drapeau> listeDrapeaux = new ArrayList<>();
    private List<Bonus> listeBonus = new ArrayList<>();

    private boolean paused = false;
    private boolean partieTerminee = false;

    @FXML
    private VBox gameArea;
    Game game = new Game();

    private GameGrid gameGridDisplay;
    @FXML
    private Button startButton;

    @FXML
    private Label timerLabel;

    @FXML
    public void startGame() throws IOException {
        lancerTimer();

        gameGridDisplay = new GameGrid(game);
        gameArea.getChildren().clear();

        StackPane gameContainer = new StackPane();
        gameContainer.getChildren().add(gameGridDisplay);
        gameArea.getChildren().add(gameContainer);

        // Réinitialiser les listes pour un nouveau départ
        joueurs.clear();
        listeDrapeaux.clear();
        listeBombes.clear();
        partieTerminee = false;

        // Définir les positions de départ des joueurs
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

        // Créer les drapeaux avec les positions de DÉPART des joueurs et des couleurs différentes
        // Et les assigner à leurs propriétaires
        Drapeau drapeau1 = new Drapeau(player1StartX, player1StartY, pacman, Color.YELLOW);
        Drapeau drapeau2 = new Drapeau(player2StartX, player2StartY, fantome, Color.BLUE);
        Drapeau drapeau3 = new Drapeau(player3StartX, player3StartY, pacman2, Color.RED);
        Drapeau drapeau4 = new Drapeau(player4StartX, player4StartY, pacman3, Color.GREEN);

        // Ajouter les drapeaux à la liste des drapeaux gérés par le contrôleur
        listeDrapeaux.add(drapeau1);
        listeDrapeaux.add(drapeau2);
        listeDrapeaux.add(drapeau3);
        listeDrapeaux.add(drapeau4);

        // Assigner à chaque joueur son propre drapeau
        pacman.setMonDrapeau(drapeau1);
        fantome.setMonDrapeau(drapeau2);
        pacman2.setMonDrapeau(drapeau3);
        pacman3.setMonDrapeau(drapeau4);

        // Ajouter les drapeaux à la grille en premier
        gameGridDisplay.getChildren().addAll(listeDrapeaux);
        // Puis ajouter les personnages par-dessus
        gameGridDisplay.getChildren().addAll(pacman, fantome, pacman2, pacman3);

        joueurs.add(pacman);
        joueurs.add(fantome);
        joueurs.add(pacman2);
        joueurs.add(pacman3);

        // Générer des bonus aléatoirement sur la carte
        //genererBonusAleatoires();

        gameContainer.requestFocus();
        gameContainer.setFocusTraversable(true);

        Scene scene = gameArea.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                }

                if (!paused && !partieTerminee) {
                    handlePlayerMovement(event, pacman, fantome, pacman2, pacman3);
                }
            });
        }
    }



    private void handlePlayerMovement(KeyEvent event, PacMan_Personnage j1, PacMan_Personnage j2, PacMan_Personnage j3, PacMan_Personnage j4) {
        GameGrid k = gameGridDisplay;

        // Sauvegarder les anciennes positions pour vérifier si le joueur a bougé
        int oldJ1X = j1.getGridX();
        int oldJ1Y = j1.getGridY();
        int oldJ2X = j2.getGridX();
        int oldJ2Y = j2.getGridY();
        int oldJ3X = j3.getGridX();
        int oldJ3Y = j3.getGridY();
        int oldJ4X = j4.getGridX();
        int oldJ4Y = j4.getGridY();

        switch (event.getCode()) {
            //Joueur 1
            case T -> { if (j1.estVivant()) j1.deplacerEnHaut(); }
            case G -> { if (j1.estVivant()) j1.deplacerEnBas(k.getHeight()); }
            case H -> { if (j1.estVivant()) j1.deplacerADroite(k.getWidth()); }
            case F -> { if (j1.estVivant()) j1.deplacerAGauche(); }
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
            case Z -> { if (j2.estVivant()) j2.deplacerEnHaut(); }
            case S -> { if (j2.estVivant()) j2.deplacerEnBas(k.getHeight()); }
            case D -> { if (j2.estVivant()) j2.deplacerADroite(k.getWidth()); }
            case Q -> { if (j2.estVivant()) j2.deplacerAGauche(); }
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
            case O -> { if (j3.estVivant()) j3.deplacerEnHaut(); }
            case L -> { if (j3.estVivant()) j3.deplacerEnBas(k.getHeight()); }
            case M -> { if (j3.estVivant()) j3.deplacerADroite(k.getWidth()); }
            case K -> { if (j3.estVivant()) j3.deplacerAGauche(); }
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
            case NUMPAD5 -> { if (j4.estVivant()) j4.deplacerEnHaut(); }
            case NUMPAD2 -> { if (j4.estVivant()) j4.deplacerEnBas(k.getHeight()); }
            case NUMPAD3 -> { if (j4.estVivant()) j4.deplacerADroite(k.getWidth()); }
            case NUMPAD1 -> { if (j4.estVivant()) j4.deplacerAGauche(); }
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

        // Après chaque mouvement, vérifier si un joueur a capturé un drapeau
        // Vérifier les collisions avec les bonus après chaque mouvement
        if (j1.getGridX() != oldJ1X || j1.getGridY() != oldJ1Y) {
            verifierCollisionBonus(j1); // NOUVELLE LIGNE
            if (j1.tenterCaptureDrapeau(listeDrapeaux)) {
                listeDrapeaux.stream()
                        .filter(d -> d.getGridX() == j1.getGridX() && d.getGridY() == j1.getGridY())
                        .findFirst()
                        .ifPresent(drapeauCapturé -> drapeauCapturé.getProprietaire().setAEteCapture(true));
            }
        }
        if (j2.getGridX() != oldJ2X || j2.getGridY() != oldJ2Y) {
            verifierCollisionBonus(j2); // NOUVELLE LIGNE
            if (j2.tenterCaptureDrapeau(listeDrapeaux)) {
                listeDrapeaux.stream()
                        .filter(d -> d.getGridX() == j2.getGridX() && d.getGridY() == j2.getGridY())
                        .findFirst()
                        .ifPresent(drapeauCapturé -> drapeauCapturé.getProprietaire().setAEteCapture(true));
            }
        }
        if (j3.getGridX() != oldJ3X || j3.getGridY() != oldJ3Y) {
            verifierCollisionBonus(j3); // NOUVELLE LIGNE
            if (j3.tenterCaptureDrapeau(listeDrapeaux)) {
                listeDrapeaux.stream()
                        .filter(d -> d.getGridX() == j3.getGridX() && d.getGridY() == j3.getGridY())
                        .findFirst()
                        .ifPresent(drapeauCapturé -> drapeauCapturé.getProprietaire().setAEteCapture(true));
            }
        }
        if (j4.getGridX() != oldJ4X || j4.getGridY() != oldJ4Y) {
            verifierCollisionBonus(j4); // NOUVELLE LIGNE
            if (j4.tenterCaptureDrapeau(listeDrapeaux)) {
                listeDrapeaux.stream()
                        .filter(d -> d.getGridX() == j4.getGridX() && d.getGridY() == j4.getGridY())
                        .findFirst()
                        .ifPresent(drapeauCapturé -> drapeauCapturé.getProprietaire().setAEteCapture(true));
            }
        }
        verifierFinDePartie();
    }

    public void initialiserBonusExistants() {
        listeBonus.clear();

        if (game != null) {
            listeBonus.addAll(game.getActiveBonuses());
            System.out.println("Nombre de bonus récupérés via Game: " + listeBonus.size());
            for (Bonus bonus : listeBonus) {
                System.out.println("Bonus trouvé à (" + bonus.getBonusX() + ", " + bonus.getBonusY() + ") - Type: " + bonus.getTypeBonusString());
            }
        } else {
            System.out.println("Game est null");
        }
    }

    public void initialize() {
        System.out.println("CTFcontroller initialisé.");
        if (startButton != null) {
            startButton.setVisible(true);
            startButton.setManaged(true);
        }
        if (finMenuContainer != null) {
            finMenuContainer.setVisible(false);
            finMenuContainer.setManaged(false);
        }
    }

    private void togglePause() {
        if (partieTerminee) return;

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
        paused = false;
        pauseMenuContainer.setVisible(false);
        pauseMenuContainer.setManaged(false);
        if (gameTimer != null) {
            gameTimer.play();
        }
    }

    @FXML
    public void quittertout() {
        Platform.exit();
        System.exit(0);
    }

    private void lancerTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tempsRestant--;
            int minutes = tempsRestant / 60;
            int secondes = tempsRestant % 60;
            String tempsFormate = String.format("TIMEUR : %02d:%02d", minutes, secondes);

            Platform.runLater(() -> timerLabel.setText(tempsFormate));

            if (tempsRestant <= 0 && !partieTerminee) {
                gameTimer.stop();
                finDePartie("Le temps est écoulé ! Aucun vainqueur.");
            }
        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    private void verifierFinDePartie() {
        if (partieTerminee) return;

        int nombreDeJoueursVivants = (int) joueurs.stream().filter(PacMan_Personnage::estVivant).count();
        int nombreDeDrapeauxEnnemisACapturer = joueurs.size() - 1;

        for (PacMan_Personnage joueur : joueurs) {
            if (joueur.estVivant() && joueur.getDrapeauxCaptures() >= nombreDeDrapeauxEnnemisACapturer) {
                finDePartie("Le joueur " + joueur.getPlayerNumber() + " a capturé tous les drapeaux ennemis et GAGNE LA PARTIE !");
                return;
            }
        }

        if (tempsRestant <= 0) {
            finDePartie("Le temps est écoulé ! Aucun vainqueur.");
            return;
        }
    }

    private void finDePartie(String message) {
        if (partieTerminee) return;
        partieTerminee = true;

        System.out.println("Partie terminée. " + message);
        if (gameTimer != null) {
            gameTimer.stop();
        }
        Platform.runLater(() -> {
            messageFinPartieLabel.setText(message);
            finMenuContainer.setVisible(true);
            finMenuContainer.setManaged(true);
        });
    }

    @FXML

    public void replayGame() throws IOException {

        joueurs.clear();
        bot.clear();
        listeBombes.clear();
        listeDrapeaux.clear();
        partieTerminee = false;

        if (gameTimer != null) {
            gameTimer.stop();
        }
        tempsRestant = 120;
        timerLabel.setText("TIMEUR : 02:00");

        finMenuContainer.setVisible(false);
        finMenuContainer.setManaged(false);

        game = new Game();
        gameGridDisplay = new GameGrid(game);
        gameArea.getChildren().clear();

        StackPane gameContainer = new StackPane();
        gameContainer.getChildren().add(gameGridDisplay);
        gameArea.getChildren().add(gameContainer);

        int player1StartX = 0;
        int player1StartY = 0;
        int player2StartX = 12;
        int player2StartY = 10;
        int player3StartX = 12;
        int player3StartY = 0;
        int player4StartX = 0;
        int player4StartY = 10;

        PacMan_Personnage pacman = new Pacman(game, player1StartX, player1StartY, 1);
        PacMan_Personnage fantome = new Pacman(game, player2StartX, player2StartY, 2);
        PacMan_Personnage pacman2 = new Pacman(game, player3StartX, player3StartY, 3);
        PacMan_Personnage pacman3 = new Pacman(game, player4StartX, player4StartY, 4);

        Drapeau drapeau1 = new Drapeau(player1StartX, player1StartY, pacman, Color.YELLOW);
        Drapeau drapeau2 = new Drapeau(player2StartX, player2StartY, fantome, Color.BLUE);
        Drapeau drapeau3 = new Drapeau(player3StartX, player3StartY, pacman2, Color.RED);
        Drapeau drapeau4 = new Drapeau(player4StartX, player4StartY, pacman3, Color.GREEN);

        listeDrapeaux.add(drapeau1);
        listeDrapeaux.add(drapeau2);
        listeDrapeaux.add(drapeau3);
        listeDrapeaux.add(drapeau4);

        pacman.setMonDrapeau(drapeau1);
        fantome.setMonDrapeau(drapeau2);
        pacman2.setMonDrapeau(drapeau3);
        pacman3.setMonDrapeau(drapeau4);

        gameGridDisplay.getChildren().addAll(listeDrapeaux);
        gameGridDisplay.getChildren().addAll(pacman, fantome, pacman2, pacman3);

        joueurs.add(pacman);
        joueurs.add(fantome);
        joueurs.add(pacman2);
        joueurs.add(pacman3);

        gameContainer.requestFocus();
        gameContainer.setFocusTraversable(true);

        Scene scene = gameArea.getScene();
        if (scene != null) {
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    togglePause();
                }

                if (!paused && !partieTerminee) {
                    handlePlayerMovement(event, pacman, fantome, pacman2, pacman3);
                }
            });
        }

        lancerTimer();
    }

    private void verifierCollisionBonus(PacMan_Personnage joueur) {
        System.out.println("Vérification collision pour joueur " + joueur.getPlayerNumber() +
                " à (" + joueur.getGridX() + ", " + joueur.getGridY() + ")");

        // Récupérer les bonus directement depuis Game en temps réel
        List<Bonus> bonusActuels = game.getActiveBonuses();
        System.out.println("Nombre de bonus actifs dans Game: " + bonusActuels.size());

        for (Bonus bonus : bonusActuels) {
            System.out.println("Bonus à (" + bonus.getBonusX() + ", " + bonus.getBonusY() + ")");
            if (bonus.getBonusX() == joueur.getGridX() && bonus.getBonusY() == joueur.getGridY()) {
                System.out.println("COLLISION DÉTECTÉE ! Joueur " + joueur.getPlayerNumber() +
                        " ramasse un bonus " + bonus.getTypeBonusString());

                bonus.appliquerBonus(joueur);
                break; // Sortir de la boucle après avoir trouvé le bonus
            }
        }
    }


}
