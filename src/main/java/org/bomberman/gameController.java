// src/main/java/org/bomberman/gameController.java
package org.bomberman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Contrôleur principal du jeu Bomberman gérant l'interface utilisateur,
 * la logique de jeu, les déplacements des joueurs et la gestion des scores.
 *
 * Cette classe utilise JavaFX FXML pour l'interface utilisateur et gère :
 * - Le démarrage et l'arrêt du jeu
 * - Les déplacements des 4 joueurs
 * - Le système de scores et de pseudos
 * - Les menus de pause et de fin de partie
 * - Le timer de jeu
 * - Les bombes et bonus
 *
 * @author Lou, Eloïse, Gustin
 * @version 1.0
 * @since 1.0
 */
public class gameController {
    /** Conteneur du menu de pause */
    @FXML
    private VBox pauseMenuContainer;
    /** Conteneur du menu de fin de partie */
    @FXML
    private VBox finMenuContainer;
    /** Label affichant le message de fin de partie */
    @FXML
    private Label messageFinPartieLabel;
    /** Timeline gérant le timer de jeu */
    private Timeline gameTimer;
    /** Temps restant en secondes (initialisé à 120 secondes / 2 minutes) */
    private int tempsRestant = 120;
    /** Liste des joueurs humains */
    private List<Joueur_Personnage> joueurs = new ArrayList<>();
    /** Liste des bots (non utilisée dans cette version) */
    private List<Bot_Personnage> bot = new ArrayList<>();
    /** Liste des bombes actives sur le terrain */
    private List<Bombe> listeBombes = new ArrayList<>();
    /** Indicateur de l'état de pause du jeu */
    private boolean paused = false;
    /** Zone de jeu principale */
    @FXML
    private VBox gameArea;
    /** Instance de la logique de jeu */
    Game game = new Game();

    /** Indicateur de fin de partie */
    private boolean partieTerminee = false;

    /** Affichage de la grille de jeu */
    private GameGrid gameGridDisplay;
    /** Bouton de démarrage du jeu */
    @FXML
    private Button startButton; // Référence au bouton démarrer


    // Labels d'affichage des scores
    /** Label du score du joueur 1 */
    @FXML
    private Label labelJ1;
    /** Label du score du joueur 2 */
    @FXML
    private Label labelJ2;
    /** Label du score du joueur 3 */
    @FXML
    private Label labelJ3;
    /** Label du score du joueur 4 */
    @FXML
    private Label labelJ4;


    // Zone de saisi des pseudo
    /** Champ de saisie du pseudo du joueur 1 */
    @FXML
    private TextField saisiJ1;
    /** Nom du joueur 1 */
    private String nomJ1;
    /** Ligne du joueur 1 dans le fichier de scores */
    private int ligneJ1;
    /** Score actuel du joueur 1 */
    private int scoreJ1 = 0;

    /** Champ de saisie du pseudo du joueur 2 */
    @FXML
    private TextField saisiJ2;
    /** Nom du joueur 2 */
    private String nomJ2;
    /** Ligne du joueur 2 dans le fichier de scores */
    private int ligneJ2;
    /** Score actuel du joueur 2 */
    private int scoreJ2 = 0;

    /** Champ de saisie du pseudo du joueur 3 */
    @FXML
    private TextField saisiJ3;
    /** Nom du joueur 3 */
    private String nomJ3;
    /** Ligne du joueur 3 dans le fichier de scores */
    private int ligneJ3;
    /** Score actuel du joueur 3 */
    private int scoreJ3 = 0;

    /** Champ de saisie du pseudo du joueur 4 */
    @FXML
    private TextField saisiJ4;
    /** Nom du joueur 4 */
    private String nomJ4;
    /** Ligne du joueur 4 dans le fichier de scores */
    private int ligneJ4;
    /** Score actuel du joueur 4 */
    private int scoreJ4 = 0;

    /** Lable qui contient le résultat de la partie */
    @FXML
    private Label resultLabel;

    /** Boolean qui dit si la partie est terminé ou non */
    private boolean partieEstTerminee = false;


    // Obtention des scoresMulti.txt
    /** Liste des scores chargés depuis le fichier */
    private List<String> scores;
    /** Dernier ID utilisé pour générer des noms automatiques */
    private int derID;


    // Partie modifiée de gameController.java
    /** Label affichant le timer */
    @FXML
    private Label timerLabel;

    /** Timer pour la gestion des bombes */
    private Timer timer; // Timer pour mettre à jour le score lors de l'explosion de la bombe

    /**
     * Démarre une nouvelle partie.
     * Initialise la grille de jeu, crée les joueurs, configure les événements
     * et traite les pseudos des joueurs.
     *
     * @throws IOException Si une erreur survient lors de la lecture/écriture des fichiers
     * @throws URISyntaxException Si l'URI du fichier de scores est invalide
     */
    @FXML
    public void startGame() throws IOException, URISyntaxException {
        arreterJeu();
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
        Joueur_Personnage pacman1 = new Joueur(game, 0, 0,1);
        Joueur_Personnage pacman2 = new Joueur(game, 12, 10,2);
        Joueur_Personnage pacman3 = new Joueur(game, 12, 0,3);
        Joueur_Personnage pacman4 = new Joueur(game, 0, 10,4);

        joueurs.add(pacman1);
        joueurs.add(pacman2);
        joueurs.add(pacman3);
        joueurs.add(pacman4);

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
                    handlePlayerMovement(event, pacman1, pacman2, pacman3, pacman4);
                }
            });
        }

        // ----- Traitement des pseudos ------
        // Chargement du fichier des scores
        URL resource = getClass().getResource("/scoresMulti.txt");
        if (resource != null) {
            scores = Files.readAllLines(Paths.get(resource.toURI()));
        }
        derID = Integer.parseInt(scores.get(1))+1;



        if (saisiJ1.getLength() != 0) {
            nomJ1 = saisiJ1.getText();
            joueurs.get(0).nom = nomJ1;
            ligneJ1 = getLigneNom(nomJ1);
            scoreJ1 = getScoreLigne(ligneJ1);
            ajouterScore(nomJ1, 0, ligneJ1);
            updateFile(scores);
        } else {
            nomJ1 = "Joueur_" + derID;
            joueurs.get(0).nom = nomJ1;
            scores.set(1, derID + "");
            ++derID;
            ligneJ1 = getLigneNom(nomJ1);
            ajouterScore(nomJ1, 0, ligneJ1);
            updateFile(scores);

        }
        if (saisiJ2.getLength() != 0) {
            nomJ2 = saisiJ2.getText();
            joueurs.get(1).nom = nomJ2;
            ligneJ2 = getLigneNom(nomJ2);
            scoreJ2 = getScoreLigne(ligneJ2);
            ajouterScore(nomJ2, 0, ligneJ2);
            updateFile(scores);
        } else {
            nomJ2 = "Joueur_" + derID;
            joueurs.get(1).nom = nomJ2;
            scores.set(1, derID + "");
            ++derID;
            ligneJ2 = getLigneNom(nomJ2);
            ajouterScore(nomJ2, 0, ligneJ2);
            updateFile(scores);
        }
        if (saisiJ3.getLength() != 0) {
            nomJ3 = saisiJ3.getText();
            joueurs.get(2).nom = nomJ3;
            ligneJ3 = getLigneNom(nomJ3);
            scoreJ3 = getScoreLigne(ligneJ3);
            ajouterScore(nomJ3, 0, ligneJ3);
            updateFile(scores);
        } else {
            nomJ3 = "Joueur_" + derID;
            joueurs.get(2).nom = nomJ3;
            scores.set(1, derID + "");
            ++derID;
            ligneJ3 = getLigneNom(nomJ3);
            ajouterScore(nomJ3, 0, ligneJ3);
            updateFile(scores);
        }
        if (saisiJ4.getLength() != 0) {
            nomJ4 = saisiJ4.getText();
            joueurs.get(3).nom = nomJ4;
            ligneJ4 = getLigneNom(nomJ4);
            scoreJ4 = getScoreLigne(ligneJ4);
            ajouterScore(nomJ4, 0, ligneJ4);
            updateFile(scores);
        } else {
            nomJ4 = "Joueur_" + derID;
            joueurs.get(3).nom = nomJ4;
            scores.set(1, derID + "");
            ++derID;
            ligneJ4 = getLigneNom(nomJ4);
            ajouterScore(nomJ4, 0, ligneJ4);
            updateFile(scores);
        }

        refreshScores();
    }


    /**
     * Vérifie si un joueur entre en collision avec un bonus et l'applique.
     *
     * @param joueur Le joueur à vérifier pour les collisions avec les bonus
     */
    private void checkBonusCollision(Joueur_Personnage joueur) {
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



    /**
     * Recherche le numéro de ligne d'un nom dans le fichier de scores.
     *
     * @param nom Le nom à rechercher
     * @return Le numéro de ligne du nom, ou la dernière ligne si non trouvé
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
     * Extrait le score d'une ligne spécifique du fichier de scores.
     *
     * @param numLigne Le numéro de ligne à analyser
     * @return Le score extrait de la ligne
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
     * Ajoute ou met à jour un score pour un joueur.
     *
     * @param nom Le nom du joueur
     * @param score Le nouveau score
     * @param ligne La ligne dans le fichier de scores
     */
    public void ajouterScore(String nom, int score, int ligne) {
        System.out.println(nom + " " + score + " " + getScoreLigne(ligne) + "   " + ligne);
        if (ligne == scores.size()-1) scores.add(nom + " " + score); // si le couple pseudo score n'est pas encore enregistré
        else if (score > getScoreLigne(ligne)){ // si le pseudo est déjà enregistré et que le score est superieur à celui enregistré
            scores.set(ligne, nom + " " + score);
        }
        System.out.println(nom + " " + score + " " + getScoreLigne(ligne) + "   " + ligne);
    }

    /**
     * Met à jour le fichier de scores avec les nouvelles données.
     *
     * @param lignes La liste des lignes à écrire dans le fichier
     * @throws IOException Si une erreur survient lors de l'écriture du fichier
     */
    public void updateFile(List<String> lignes) throws IOException {
        Path cheminFichier = Paths.get("src/main/resources/scoresMulti.txt");

        if (!Files.exists(cheminFichier)) {
            throw new IOException("Le fichier n'existe pas : " + cheminFichier.toAbsolutePath());
        }

        Files.write(cheminFichier, lignes);
    }

    /**
     * Met à jour l'affichage des scores dans l'interface utilisateur.
     */
    public void refreshScores() {
        // Maj des pseudos
        labelJ1.setText(nomJ1 + " : " + scoreJ1);
        labelJ2.setText(nomJ2 + " : " + scoreJ2);
        labelJ3.setText(nomJ3 + " : " + scoreJ3);
        labelJ4.setText(nomJ4 + " : " + scoreJ4);
    }

    /**
     * Lance un timer pour traiter l'explosion d'une bombe après 2 secondes.
     *
     * @param bomb La bombe à traiter
     * @param joueur Le numéro du joueur qui a placé la bombe
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
     * Ajoute les points obtenus par l'explosion d'une bombe au score du joueur.
     *
     * @param bomb La bombe qui a explosé
     * @param Joueur Le numéro du joueur (1, 2, 3 ou 4)
     * @throws IOException Si une erreur survient lors de la sauvegarde des scores
     */
    private void ajoutScoreExplosion(Bombe bomb, int Joueur) throws IOException {
        switch (Joueur){
            case 1:
                scoreJ1 += bomb.getScoreJoueur();
                joueurs.get(0).score = scoreJ1; // ← Correction: était scoreJ1 au lieu de scoreJ1
                ajouterScore(nomJ1, scoreJ1, ligneJ1);
                updateFile(scores);
                break;
            case 2:
                scoreJ2 += bomb.getScoreJoueur();
                joueurs.get(1).score = scoreJ2; // ← Correction: était scoreJ1 au lieu de scoreJ2
                ajouterScore(nomJ2, scoreJ2, ligneJ2);
                updateFile(scores);
                break;
            case 3:
                scoreJ3 += bomb.getScoreJoueur();
                joueurs.get(2).score = scoreJ3; // ← Correction: était scoreJ1 au lieu de scoreJ3
                ajouterScore(nomJ3, scoreJ3, ligneJ3);
                updateFile(scores);
                break;
            case 4:
                scoreJ4 += bomb.getScoreJoueur();
                joueurs.get(3).score = scoreJ4; // ← Correction: était scoreJ1 au lieu de scoreJ4
                ajouterScore(nomJ4, scoreJ4, ligneJ4);
                updateFile(scores);
                break;
        }
        refreshScores();    // Maj du bandeau des scores
    }

    /**
     * Active ou désactive la pause du jeu.
     * Affiche/masque le menu de pause et gère le timer.
     */
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


    /**
     * Gère les déplacements et actions des 4 joueurs en fonction des touches pressées.
     *
     * Contrôles :
     * - Joueur 1 : T/G/F/H pour se déplacer, U pour poser une bombe
     * - Joueur 2 : Z/S/Q/D pour se déplacer, A pour poser une bombe
     * - Joueur 3 : O/L/K/M pour se déplacer, P pour poser une bombe
     * - Joueur 4 : Pavé numérique 5/2/1/3 pour se déplacer, 4 pour poser une bombe
     *
     * @param event L'événement de pression de touche
     * @param j1 Le joueur 1
     * @param j2 Le joueur 2
     * @param j3 Le joueur 3
     * @param j4 Le joueur 4
     */
    private void handlePlayerMovement(KeyEvent event, Joueur_Personnage j1, Joueur_Personnage j2, Joueur_Personnage j3, Joueur_Personnage j4) {
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
                checkBonusCollision(j1);
            }
            case F -> {
                j1.deplacerAGauche();
                checkBonusCollision(j1);
            }
            case U -> {
                int px = j1.getGridX();
                int py = j1.getGridY();

                if (game.getGrid()[px][py] == 0 && j1.peutPlacerBombe()) {
                    System.out.println("Bombe");
                    int rayon = j1.aBonusRayon() ? 2 : 1;
                    if (j1.aBonusRayon()) {
                        j1.consommerBonusRayon();
                    }
                    Bombe bomb = new Bombe( px, py, rayon, game, gameGridDisplay, joueurs, bot, j1, listeBombes); // Création de la bombe
                    startTimer(bomb, 1);
                    j1.marquerBombePlacee();
                    gameGridDisplay.refresh();
                }
            }

            //Joueur 2
            case Z -> {
                j2.deplacerEnHaut();
                checkBonusCollision(j2);
            }

            case S -> {
                j2.deplacerEnBas(k.getHeight());
                checkBonusCollision(j2);
            }
            case D -> {
                j2.deplacerADroite(k.getWidth());
                checkBonusCollision(j2);
            }
            case Q -> {
                j2.deplacerAGauche();
                checkBonusCollision(j2);
            }
            case A -> {
                int px2 = j2.getGridX();
                int py2 = j2.getGridY();

                if (game.getGrid()[py2][px2] == 0 && j2.peutPlacerBombe()) {
                    System.out.println("Bombe");
                    int rayon = j2.aBonusRayon() ? 2 : 1;
                    if (j2.aBonusRayon()) {
                        j2.consommerBonusRayon();
                    }
                    Bombe bomb = new Bombe(px2, py2, rayon, game, gameGridDisplay, joueurs, bot, j2, listeBombes);
                    startTimer(bomb, 2);
                    j2.marquerBombePlacee();
                    gameGridDisplay.refresh();
                }
            }

            //Joueur 3
            case O -> {
                j3.deplacerEnHaut();
                checkBonusCollision(j3);
            }
            case L -> {
                j3.deplacerEnBas(k.getHeight());
                checkBonusCollision(j3);
            }
            case M -> {
                j3.deplacerADroite(k.getWidth());
                checkBonusCollision(j3);
            }
            case K -> {
                j3.deplacerAGauche();
                checkBonusCollision(j3);
            }
            case P -> {
                int px3 = j3.getGridX();
                int py3 = j3.getGridY();

                if (game.getGrid()[py3][px3] == 0 && j3.peutPlacerBombe()) {
                    System.out.println("Bombe");
                    int rayon = j3.aBonusRayon() ? 2 : 1;
                    if (j3.aBonusRayon()) {
                        j3.consommerBonusRayon();
                    }
                    Bombe bomb = new Bombe(px3, py3, rayon, game, gameGridDisplay, joueurs, bot, j3, listeBombes);
                    startTimer(bomb, 3);
                    j3.marquerBombePlacee();
                    gameGridDisplay.refresh();
                }
            }

            //Joueur 4
            case NUMPAD5 -> {
                j4.deplacerEnHaut();
                checkBonusCollision(j4);
            }
            case NUMPAD2 -> {
                j4.deplacerEnBas(k.getHeight());
                checkBonusCollision(j4);
            }
            case NUMPAD3 -> {
                j4.deplacerADroite(k.getWidth());
                checkBonusCollision(j4);
            }
            case NUMPAD1 -> {
                j4.deplacerAGauche();
                checkBonusCollision(j4);
            }
            case NUMPAD4 -> {
                int px4 = j4.getGridX();
                int py4 = j4.getGridY();

                if (game.getGrid()[py4][px4] == 0 && j4.peutPlacerBombe()) {
                    System.out.println("Bombe");
                    int rayon = j4.aBonusRayon() ? 2 : 1;
                    if (j4.aBonusRayon()) {
                        j4.consommerBonusRayon();
                    }
                    Bombe bomb = new Bombe(px4, py4, rayon, game, gameGridDisplay, joueurs, bot, j4, listeBombes);
                    startTimer(bomb, 4);
                    j4.marquerBombePlacee();
                    gameGridDisplay.refresh();
                }
            }
        }
    }



    /**
     * Relance une nouvelle partie.
     * Réinitialise tous les éléments du jeu et recrée les joueurs.
     *
     * @throws IOException Si une erreur survient lors de la création de la partie
     */
    @FXML
    public void replayGame() throws IOException {
        // Réinitialiser les listes de joueurs
        arreterJeu();
        joueurs.clear();
        partieEstTerminee = false;

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
        Joueur_Personnage pacman = new Joueur(game, 0, 0,1);
        Joueur_Personnage fantome = new Joueur(game, 12, 10,2);
        Joueur_Personnage pacman2 = new Joueur(game, 12, 0,3);
        Joueur_Personnage pacman3 = new Joueur(game, 0, 10,4);

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

        lancerTimer();
    }

    /**
     * Retourne au menu principal.
     * Charge le FXML du menu et applique les styles CSS.
     *
     * @param event L'événement déclenché par le clic sur le bouton
     */
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

    /**
     * Reprend le jeu après une pause.
     * Masque le menu de pause et relance le timer.
     */
    @FXML
    public void resumeGame() {
        paused = false;
        pauseMenuContainer.setVisible(false);
        pauseMenuContainer.setManaged(false);
        if (gameTimer != null) {
            gameTimer.play();
        }
    }

    /**
     * Quitte complètement l'application.
     * Ferme JavaFX et termine la JVM.
     */
    @FXML
    public void quittertout() {
        Platform.exit(); // Fait sortir l'application JavaFX
        System.exit(0); // Optionnel: Assure la terminaison complète de la JVM (utile si des threads tournent en arrière-plan)

    }


    /**
     * Lance le timer de jeu avec un décompte de 2 minutes.
     * Met à jour l'affichage chaque seconde et vérifie les conditions de fin de partie.
     */
    private void lancerTimer() {
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            // Ne pas décrémenter ni vérifier si la partie est déjà terminée pour éviter des appels multiples
            if (partieEstTerminee) {
                return; // Sortir si la partie est déjà finie
            }

            tempsRestant--;
            int minutes = tempsRestant / 60;
            int secondes = tempsRestant % 60;
            String tempsFormate = String.format("TIMEUR : %02d:%02d", minutes, secondes);

            Platform.runLater(() -> timerLabel.setText(tempsFormate));

            // Toujours vérifier la fin de partie par élimination à chaque tic du timer

            // C'EST ICI QUE LA FIN DE PARTIE PAR TEMPS ÉCOULÉ DOIT ÊTRE GÉRÉE
            if (tempsRestant <= 0) {
                timerLabel.setText("TIMEUR : 00:00");
                finDePartieParTemps(); // Appelez la méthode spécifique pour la fin par temps
            }

            verifierFinDePartieParElimination();
        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }


    // NOUVELLE MÉTHODE: Vérification de l'état des joueurs pendant le jeu (fin par élimination)

    /**
     * La fonction vérifie si la partie est terminé en vérifiant le nombre de joueur encore en vie,
     * puis si elle l'est, met à jour partieEstTermine
     * et affiche le menu de fin.
     */
    private void verifierFinDePartieParElimination() {
        if (partieEstTerminee) { // S'assurer que la logique ne s'exécute pas si déjà terminé
            return;
        }

        List<Joueur_Personnage> joueursHumainsEnVie = joueurs.stream()
                .filter(Joueur_Personnage::estVivant)
                .collect(Collectors.toList());

        if (joueursHumainsEnVie.size() <= 1) { // Il ne reste plus qu'un ou aucun joueur humain
            arreterJeu(); // Arrêter le timer du jeu (ceci mettra 'partieEstTerminee' à true via finDePartie... ou devrait)

            // Définir le message de fin de partie basé sur le nombre de survivants
            String mainMessage;
            String resultDetailsMessage;
            boolean victoireGlobale;

            if (joueursHumainsEnVie.size() == 1) {
                // Un seul joueur est vivant : c'est le vainqueur par élimination
                Joueur_Personnage vainqueur = joueursHumainsEnVie.get(0);
                mainMessage = vainqueur.nom + " GAGNE !";
                resultDetailsMessage = "FÉLICITATIONS " + vainqueur.nom + " !";
                victoireGlobale = true;
            } else {
                // Aucun joueur humain n'est vivant : défaite générale
                mainMessage = "GAME OVER !";
                resultDetailsMessage = "AUCUN GAGNANT. Tout le monde a été éliminé.";
                victoireGlobale = false;
            }

            // Déclencher l'affichage du menu de fin avec le message approprié
            Platform.runLater(() -> {
                configurerAffichageFinDePartie(mainMessage, resultDetailsMessage, victoireGlobale);
                finMenuContainer.setVisible(true);
                finMenuContainer.setManaged(true);
            });
            partieEstTerminee = true; // Marquez la partie comme terminée ici après avoir déclenché l'affichage
        }
        // Si plus d'un joueur est en vie, la partie continue.
    }

    /**
     * La fonction vérifie si la partie est terminé en vérifiant si le temps max a été atteind,
     * puis si elle l'est, met à jour partieEstTermine
     * et affiche le menu de fin.
     */
    private void finDePartieParTemps() {
        if (partieEstTerminee) {
            return;
        }
        partieEstTerminee = true;
        arreterJeu();
        List<Joueur_Personnage> joueursVivants = joueurs.stream()
                .filter(Joueur_Personnage::estVivant)
                .collect(Collectors.toList());

        String mainMessage;
        String resultMenuMessage;
        boolean victoireGlobale = false;

        if (joueursVivants.isEmpty()) {
            // Personne n'a survécu avant la fin du temps
            mainMessage = "TEMPS ÉCOULÉ !";
            resultMenuMessage = "AUCUN GAGNANT.";
            victoireGlobale = false; // Considéré comme une défaite générale
        } else if (joueursVivants.size() == 1) {
            // Un seul joueur a survécu jusqu'à la fin du temps
            Joueur_Personnage vainqueur = joueursVivants.get(0);
            mainMessage = "TEMPS ÉCOULÉ ! " + vainqueur.nom + " EST LE DERNIER SURVIVANT !";
            resultMenuMessage = "FÉLICITATIONS " + vainqueur.nom + " !";
            victoireGlobale = true;
        } else {
            // Plusieurs joueurs sont encore vivants à la fin du temps : CLASSEMENT !
            joueursVivants.sort(Comparator.comparingInt(Joueur_Personnage::getScore).reversed());

            StringBuilder classement = new StringBuilder("TEMPS ÉCOULÉ ! CLASSEMENT FINAL :\n");
            for (int i = 0; i < joueursVivants.size(); i++) {
                Joueur_Personnage j = joueursVivants.get(i);
                classement.append((i + 1)).append(".").append(j.nom);
                if (j.getScore() != 0) { // Vérifiez si getScore() est pertinent
                    classement.append(" (Score: ").append(j.getScore()).append(")");
                }
                classement.append("\n");
            }
            mainMessage = "TEMPS ÉCOULÉ !";
            resultMenuMessage = classement.toString();
            victoireGlobale = true; // Ceux qui sont encore vivants sont considérés comme des "survivants"
        }

        boolean finalVictoireGlobale = victoireGlobale;
        Platform.runLater(() -> {
            configurerAffichageFinDePartie(mainMessage, resultMenuMessage, finalVictoireGlobale);
            finMenuContainer.setVisible(true);
            finMenuContainer.setManaged(true);
        });
    }


    // MÉTHODE Arrêter le jeu immédiatement
    /** Attête le jeu */
    private void arreterJeu() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    // MÉTHODE Accepte les messages et le statut de victoire (cette méthode est bonne)

    /**
     * La fonction personalise le menu de fin en fonction du vaincueur.
     *
     * @param mainMessage
     * @param resultDetailsMessage
     * @param estVictoireGlobale
     */
    private void configurerAffichageFinDePartie(String mainMessage, String resultDetailsMessage, boolean estVictoireGlobale) {
        if (messageFinPartieLabel != null) {
            messageFinPartieLabel.setText(mainMessage);
            messageFinPartieLabel.setVisible(true);
            messageFinPartieLabel.setManaged(true);

            messageFinPartieLabel.getStyleClass().add("game-status-label");
            messageFinPartieLabel.getStyleClass().remove("victoire");
            messageFinPartieLabel.getStyleClass().remove("defaite");

            if (estVictoireGlobale) {
                messageFinPartieLabel.getStyleClass().add("victoire");
            } else {
                messageFinPartieLabel.getStyleClass().add("defaite");
            }
        }

        if (resultLabel != null) {
            resultLabel.setText(resultDetailsMessage);
            resultLabel.getStyleClass().add("result-label");
            resultLabel.getStyleClass().remove("victoire");
            resultLabel.getStyleClass().remove("defaite");

            if (estVictoireGlobale) {
                resultLabel.getStyleClass().add("victoire");
            } else {
                resultLabel.getStyleClass().add("defaite");
            }
        }
    }

}