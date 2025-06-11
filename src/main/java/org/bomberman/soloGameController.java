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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bomberman.entite.Bombe;
import org.bomberman.entite.Bonus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    // Zone de saisi des pseudo
    @FXML
    private Label labelJoueur;

    @FXML
    private TextField saisiJoueur;
    private String nomJoueur;
    private int ligneJoueur;
    private int scoreJoueur = 0;

    // Obtention des scoresSolo.txt
    private List<String> scores;
    private int derID;
    private Timer timer;

    public soloGameController() throws IOException {
    }

    @FXML
    public void startGame() throws URISyntaxException, IOException {
        lancerTimer(); // debut du timer
        lancerTimerBots();
        // Crée une instance de ta GameGrid personnalisée
        gameGridDisplay = new GameGrid(game);

        // Ajoute la GameGrid au StackPane central
        gameAreaStackPane.getChildren().clear(); // Vide le StackPane
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

        // ----- Traitement des pseudos ------
        // Chargement du fichier des scores
        URL resource = getClass().getResource("/scoresMulti.txt");
        if (resource != null) {
            scores = Files.readAllLines(Paths.get(resource.toURI()));
        }
        derID = Integer.parseInt(scores.get(1)) + 1;


        if (saisiJoueur.getLength() != 0) {
            nomJoueur = saisiJoueur.getText();
            ligneJoueur = getLigneNom(nomJoueur);
            scoreJoueur = getScoreLigne(ligneJoueur);
            ajouterScore(nomJoueur, 0, ligneJoueur);
            updateFile(scores);
        } else {
            nomJoueur = "Joueur_" + derID;
            scores.set(1, derID + "");
            ++derID;
            ligneJoueur = getLigneNom(nomJoueur);
            ajouterScore(nomJoueur, 0, ligneJoueur);
            updateFile(scores);
        }
        refreshScores();
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

                if (game.getGrid()[py][px] == 0 && j1.peutPlacerBombe()) {
                    System.out.println("Bombe");
                    // ← MODIFIER : Vérifier si le joueur a le bonus rayon
                    int rayon = j1.aBonusRayon() ? 2 : 1;
                    if (j1.aBonusRayon()) {
                        j1.consommerBonusRayon(); // Consommer le bonus
                    }
                    Bombe bomb = new Bombe(px, py, rayon, game, gameGridDisplay, joueurs, bot, j1, listeBombes);
                    startTimer(bomb, 1);
                    j1.marquerBombePlacee();
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

            // Appliquer la classe de base
            gameStatusLabel.getStyleClass().add("game-status-label");

            if (victoire) {
                gameStatusLabel.getStyleClass().remove("defaite"); // S'assurer que l'autre classe est retirée
                gameStatusLabel.getStyleClass().add("victoire");
            } else {
                gameStatusLabel.getStyleClass().remove("victoire"); // S'assurer que l'autre classe est retirée
                gameStatusLabel.getStyleClass().add("defaite");
            }
        }

        // Configuration du label dans le menu de fin
        if (resultLabel != null) {
            resultLabel.getStyleClass().add("result-label"); // Appliquer la classe de base

            if (victoire) {
                resultLabel.setText("FÉLICITATIONS ! ");
                resultLabel.getStyleClass().remove("defaite");
                resultLabel.getStyleClass().add("victoire");
            } else {
                resultLabel.setText(" GAME OVER ");
                resultLabel.getStyleClass().remove("victoire");
                resultLabel.getStyleClass().add("defaite");
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

    public int getLigneNom(String nom){
        String nomLigne;
        for (int i = 0; i < scores.size(); i++) {
            String ligne = scores.get(i);
            nomLigne = "";
            if (ligne.charAt(0) != '#') {
                for (int j = 0; j < ligne.length() && ligne.charAt(j) != ' '; j++) {
                    nomLigne += ligne.charAt(j);
                }
            }
            if (nomLigne.equals(nom)) return i;
        }
        return scores.size()-1;
    }

    public int getScoreLigne(int numLigne) {
        String ligne = scores.get(numLigne);
        String scoresLigne = "";
        for (int j = ligne.length()-1; ligne.charAt(j) != ' '; j--) {
            scoresLigne = ligne.charAt(j) + scoresLigne;
        }
        return Integer.parseInt(scoresLigne);
    }

    public void ajouterScore(String nom, int score, int ligne) {
        //System.out.println(nom + " " + score + " " + getScoreLigne(ligne) + "   " + ligne);
        if (ligne == scores.size()-1) scores.add(nom + " " + score); // si le couple pseudo score n'est pas encore enregistré
        else if (score > getScoreLigne(ligne)){ // si le pseudo est déjà enregistré et que le score est superieur à celui enregistré
            scores.set(ligne, nom + " " + score);
        }
        //System.out.println(nom + " " + score + " " + getScoreLigne(ligne) + "   " + ligne);
    }

    public void updateFile(List<String> lignes) throws IOException {
        Path cheminFichier = Paths.get("src/main/resources/scoresSolo.txt");

        if (!Files.exists(cheminFichier)) {
            throw new IOException("Le fichier n'existe pas : " + cheminFichier.toAbsolutePath());
        }

        Files.write(cheminFichier, lignes);
    }

    public void refreshScores() {
        // Maj des pseudos
        labelJoueur.setText(nomJoueur + " : " + scoreJoueur);
    }

    private void startTimer(Bombe bomb, int joueur) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {                       // Attend que la bombe ait exploser pour
                    try {                                       // mettre à jour le score des joueurs
                        ajoutScoreExplosion(bomb, joueur);      //
                    } catch (IOException e) {                   //
                        throw new RuntimeException(e);          //
                    }
                });
            }
        }, 2010); // 2.01 secondes
    }

    private void ajoutScoreExplosion(Bombe bomb, int Joueur) throws IOException {
        scoreJoueur += bomb.getScoreJoueur();  // Ajout des scores de la bombe à scoreZ
        ajouterScore(nomJoueur, scoreJoueur, ligneJoueur);    // Maj de la variable scores
        updateFile(scores);     // sauvegarde du nouveau score
        //System.out.println(scoreJoueur + " " + bomb.getScoreJoueur());
        refreshScores();    // Maj du bandeau des scores
    }
}
