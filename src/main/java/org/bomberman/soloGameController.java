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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Contrôleur principal du jeu Bomberman en mode solo gérant l'interface utilisateur,
 * la logique de jeu, les déplacements des joueurs et la gestion des scores.
 *
 * Cette classe utilise JavaFX FXML pour l'interface utilisateur et gère :
 * - Le démarrage et l'arrêt du jeu
 * - Les déplacements du joueur et des bots
 * - Le système de scores et de pseudos
 * - Les menus de pause et de fin de partie
 * - Le timer de jeu (120 secondes par défaut)
 * - La collision avec les bonus
 * - La gestion des bombes et explosions
 *
 * Le jeu oppose un joueur humain à trois bots IA. La partie se termine quand :
 * - Le joueur meurt (défaite)
 * - Tous les bots meurent (victoire)
 * - Le temps s'écoule (défaite)
 *
 * @author Lou, Eloïse, Gustin
 * @version 1.0
 * @since 1.0
 */
public class soloGameController {
    /** Conteneur du menu de pause affiché lors de l'appui sur ÉCHAP */
    @FXML
    private VBox pauseMenuContainer;

    /** État de pause du jeu */
    private boolean isPaused = false;


    /** Zone de jeu principale contenant la grille de jeu */
    @FXML
    private VBox gameAreaStackPane; // Référence au  FXML
    /** Instance de la logique de jeu */
    Game game = new Game();
    /** Instance d'affichage de la grille de jeu */
    GameGrid gameGridDisplay = new GameGrid(game);

    /** Bouton de démarrage du jeu */
    @FXML
    private Button startButton;
    /** Label affichant le timer de la partie */
    @FXML
    private Label timerLabel;
    /** Conteneur du menu de fin de partie */
    @FXML
    private VBox finMenuContainer;

    /** Label pour afficher le statut de la partie (victoire/défaite) */
    @FXML
    private Label gameStatusLabel;

    /** Label pour le résultat dans le menu de fin */
    @FXML
    private Label resultLabel;

    /** Timeline gérant le timer principal du jeu */
    private Timeline gameTimer;
    /** Timeline gérant les actions des bots */
    private Timeline botTimer;
    /** Temps restant en secondes (120 par défaut) */
    private int tempsRestant = 120;

    /** Liste des joueurs humains (un seul en mode solo) */
    private List<Joueur_Personnage> joueurs = new ArrayList<>();
    /** Liste des bots IA (trois bots par défaut) */
    private List<Bot_Personnage> bot = new ArrayList<>();
    /** Liste des bombes actives sur le terrain */
    private List<Bombe> listeBombes = new ArrayList<>();

    // Zone de saisi des pseudo
    /** Label affichant le pseudo du joueur */
    @FXML
    private Label labelJoueur;

    /** Champ de saisie pour le pseudo du joueur */
    @FXML
    private TextField saisiJoueur;
    /** Nom du joueur actuel */
    private String nomJoueur;
    /** Ligne du joueur dans le fichier de scores */
    private int ligneJoueur;
    /** Score actuel du joueur */
    private int scoreJoueur = 0;

    // Obtention des scoresSolo.txt
    /** Liste des scores chargés depuis le fichier */
    private List<String> scores;
    /** Dernier ID utilisé pour les joueurs anonymes */
    private int derID;
    /** Timer pour la gestion des scores lors des explosions */
    private Timer timer; // Timer pour mettre à jour le score lors de l'explosion de la bombe

    /**
     * Constructeur par défaut du contrôleur.
     *
     * @throws IOException si une erreur survient lors de l'initialisation
     */
    public soloGameController() throws IOException {
    }

    /**
     * Démarre une nouvelle partie de jeu.
     *
     * Cette méthode :
     * - Lance les timers de jeu et des bots
     * - Initialise la grille de jeu
     * - Crée le joueur et les trois bots aux positions de départ
     * - Configure les contrôles clavier
     * - Charge et traite les pseudos et scores
     *
     * @throws URISyntaxException si l'URI du fichier de scores est malformée
     * @throws IOException si une erreur survient lors de la lecture du fichier de scores
     */
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
        Joueur_Personnage pacman = new Joueur(game, 12, 0, 1);
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

    /**
     * Lance le timer contrôlant les actions des bots.
     *
     * Les bots agissent toutes les secondes s'ils sont vivants et si le jeu n'est pas en pause.
     * Après chaque action, vérifie les conditions de fin de partie.
     */
    private void lancerTimerBots() {
        botTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (!isPaused) {
                for (Bot_Personnage botPersonnage : bot) {
                    if (botPersonnage.estVivant()) {
                        Joueur_Personnage joueurPrincipal = joueurs.isEmpty() ? null : joueurs.get(0);
                        botPersonnage.agir(joueurPrincipal, joueurs, gameGridDisplay, bot);
                    }
                }
                verifierFinDePartie(); // Vérifier après chaque action des bots
            }
        }));
        botTimer.setCycleCount(Timeline.INDEFINITE);
        botTimer.play();
    }

    /**
     * Gère les mouvements du joueur en fonction des touches pressées.
     *
     * Contrôles supportés :
     * - Z : Déplacement vers le haut
     * - S : Déplacement vers le bas
     * - D : Déplacement vers la droite
     * - Q : Déplacement vers la gauche
     * - A : Placement d'une bombe
     *
     * @param event l'événement clavier
     * @param j1 le joueur à déplacer
     */
    private void handlePlayerMovement(KeyEvent event, Joueur_Personnage j1) {
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

    /**
     * Initialise le contrôleur après le chargement du FXML.
     * Configure la visibilité du bouton de démarrage.
     */
    public void initialize() {
        System.out.println("gameController initialisé.");
        if (startButton != null) {
            startButton.setVisible(true);
            startButton.setManaged(true);
        }
    }

    /**
     * Bascule entre l'état de pause et de jeu.
     *
     * En pause :
     * - Affiche le menu de pause
     * - Met en pause les timers de jeu et des bots
     *
     * En reprise :
     * - Masque le menu de pause
     * - Relance les timers
     */
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

    /**
     * Retourne au menu principal du jeu.
     *
     * Arrête tous les timers actifs et charge la scène du menu principal
     * avec ses styles CSS associés.
     *
     * @param event l'événement déclencheur (clic sur bouton)
     */
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

    /**
     * Reprend le jeu après une pause.
     * Masque le menu de pause et relance les timers.
     */
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

    /**
     * Quitte complètement l'application.
     * Arrête tous les timers et ferme la JVM.
     */
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

    /**
     * Lance le timer principal du jeu (120 secondes).
     *
     * Le timer :
     * - Décrémente le temps restant chaque seconde
     * - Met à jour l'affichage du timer
     * - Vérifie les conditions de fin de partie
     * - Termine la partie si le temps s'écoule
     */
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

    /**
     * Vérifie les conditions de fin de partie en mode solo.
     *
     * Conditions de fin :
     * - Le joueur est mort → DÉFAITE
     * - Tous les bots sont morts → VICTOIRE
     * - Plus qu'un seul survivant au total
     */
    private void verifierFinDePartie() {
        long joueursEnVie = joueurs.stream().filter(Joueur_Personnage::estVivant).count();
        long botsEnVie = bot.stream().filter(Bot_Personnage::estVivant).count();

        // CAS 1: Le joueur est mort = DÉFAITE
        if (joueursEnVie == 0) {
            arreterJeu();
            finDePartie("VOUS AVEZ PERDU !");
            return;
        }

        // CAS 2: Tous les bots sont morts = VICTOIRE
        if (botsEnVie == 0) {
            arreterJeu();
            finDePartie("Tous les bots sont morts ! \n VOUS AVEZ GAGNÉ !");
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

    /**
     * Arrête immédiatement le jeu en stoppant tous les timers.
     */
    private void arreterJeu() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (botTimer != null) {
            botTimer.stop();
        }
    }

    /**
     * Termine la partie avec un message personnalisé.
     *
     * Affiche le menu de fin avec le message approprié et
     * configure l'apparence selon le résultat (victoire/défaite).
     *
     * @param message le message à afficher (ex: "VOUS AVEZ GAGNÉ !")
     */
    private void finDePartie(String message) {
        Platform.runLater(() -> {
            // Configurer l'affichage selon le type de message
            configurerAffichageFinDePartie(message);

            finMenuContainer.setVisible(true);
            finMenuContainer.setManaged(true);
        });
    }


    /**
     * Configure l'affichage de fin de partie selon le résultat.
     *
     * Applique les styles CSS appropriés selon que le message
     * indique une victoire ou une défaite.
     *
     * @param message le message de fin de partie
     */
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

    /**
     * Termine la partie avec un message par défaut.
     *
     * @deprecated Utiliser finDePartie(String) avec un message spécifique
     */
    private void finDePartie() {
        finDePartie("PARTIE TERMINÉE");
    }

    /**
     * Relance une nouvelle partie.
     *
     * Réinitialise :
     * - Les listes de joueurs et bots
     * - Les timers et le temps restant
     * - La grille de jeu
     * - Les positions des personnages
     * - L'interface utilisateur
     *
     * @throws IOException si une erreur survient lors de la réinitialisation
     */
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

        Joueur_Personnage pacman = new Joueur(game, 0, 0, 1);
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

    /**
     * Vérifie et traite les collisions entre le joueur et les bonus.
     *
     * Parcourt la liste des bonus actifs et applique l'effet
     * du bonus si le joueur se trouve sur la même case.
     *
     * @param joueur le joueur dont vérifier les collisions
     */
    private void checkBonusCollision(Joueur_Personnage joueur) {
        List<Bonus> activeBonuses = game.getActiveBonuses();
        for (int i = activeBonuses.size() - 1; i >= 0; i--) {
            Bonus bonus = activeBonuses.get(i);
            if (bonus.getBonusX() == joueur.getGridX() && bonus.getBonusY() == joueur.getGridY()) {
                bonus.appliquerBonus(joueur);
                break;
            }
        }
    }

    /**
     * Recherche la ligne d'un nom dans la liste des scores.
     *
     * @param nom le nom à rechercher
     * @return l'index de la ligne contenant le nom, ou la dernière ligne si non trouvé
     */
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

    /**
     * Extrait le score d'une ligne du fichier de scores.
     *
     * @param numLigne le numéro de ligne
     * @return le score trouvé dans la ligne
     */
    public int getScoreLigne(int numLigne) {
        String ligne = scores.get(numLigne);
        String scoresLigne = "";
        for (int j = ligne.length()-1; ligne.charAt(j) != ' '; j--) {
            scoresLigne = ligne.charAt(j) + scoresLigne;
        }
        return Integer.parseInt(scoresLigne);
    }

    /**
     * Ajoute ou met à jour un score dans la liste.
     *
     * Si le pseudo n'existe pas, l'ajoute avec le score.
     * Si le pseudo existe et le nouveau score est supérieur, met à jour.
     *
     * @param nom le nom du joueur
     * @param score le score à enregistrer
     * @param ligne la ligne où enregistrer/mettre à jour
     */
    public void ajouterScore(String nom, int score, int ligne) {
        //System.out.println(nom + " " + score + " " + getScoreLigne(ligne) + "   " + ligne);
        if (ligne == scores.size()-1) scores.add(nom + " " + score); // si le couple pseudo score n'est pas encore enregistré
        else if (score > getScoreLigne(ligne)){ // si le pseudo est déjà enregistré et que le score est superieur à celui enregistré
            scores.set(ligne, nom + " " + score);
        }
        //System.out.println(nom + " " + score + " " + getScoreLigne(ligne) + "   " + ligne);
    }

    /**
     * Sauvegarde la liste des scores dans le fichier scoresSolo.txt.
     *
     * @param lignes les lignes à écrire dans le fichier
     * @throws IOException si une erreur survient lors de l'écriture
     */
    public void updateFile(List<String> lignes) throws IOException {
        Path cheminFichier = Paths.get("src/main/resources/scoresSolo.txt");

        if (!Files.exists(cheminFichier)) {
            throw new IOException("Le fichier n'existe pas : " + cheminFichier.toAbsolutePath());
        }

        Files.write(cheminFichier, lignes);
    }

    /**
     * Met à jour l'affichage des scores dans l'interface.
     * Actualise le label du joueur avec son nom et score actuel.
     */
    public void refreshScores() {
        // Maj des pseudos
        labelJoueur.setText(nomJoueur + " : " + scoreJoueur);
    }

    /**
     * Démarre un timer pour gérer les scores après explosion d'une bombe.
     *
     * Le timer attend 2,01 secondes (après l'explosion) puis
     * met à jour le score du joueur selon les dégâts causés.
     *
     * @param bomb la bombe dont surveiller l'explosion
     * @param joueur l'ID du joueur (utilisé pour l'historique)
     */
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

    /**
     * Ajoute les points gagnés lors d'une explosion au score du joueur.
     *
     * Met à jour :
     * - Le score du joueur dans la classe
     * - Le score dans l'objet personnage
     * - La liste des scores
     * - Le fichier de sauvegarde
     * - L'affichage à l'écran
     *
     * @param bomb la bombe qui a explosé
     * @param Joueur l'ID du joueur (paramètre non utilisé mais conservé pour compatibilité)
     * @throws IOException si une erreur survient lors de la sauvegarde
     */
    private void ajoutScoreExplosion(Bombe bomb, int Joueur) throws IOException {
        scoreJoueur += bomb.getScoreJoueur();  // Ajout des scores de la bombe à scoreZ
        joueurs.get(0).score = scoreJoueur;           // Maj du score de pacman1 dans la classe PacMan_Personnage
        ajouterScore(nomJoueur, scoreJoueur, ligneJoueur);    // Maj de la variable scores
        updateFile(scores);     // sauvegarde du nouveau score
        //System.out.println(scoreJoueur + " " + bomb.getScoreJoueur());
        refreshScores();    // Maj du bandeau des scores
    }
}
