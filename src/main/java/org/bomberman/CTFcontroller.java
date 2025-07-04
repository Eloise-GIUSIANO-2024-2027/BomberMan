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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bomberman.entite.Bombe;
import org.bomberman.entite.Bonus;
import java.util.Comparator;

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
 * Contrôleur principal pour le mode de jeu Capture The Flag (CTF) du jeu Bomberman.
 * Cette classe gère l'interface utilisateur, les interactions des joueurs, la logique de jeu et les scores.
 * Elle hérite des fonctionnalités JavaFX pour la gestion des événements et l'affichage.
 *
 * @author Votre nom
 * @version 1.0
 */
public class CTFcontroller {

    /** Conteneur pour le menu de pause affiché lorsque le jeu est en pause. */
    @FXML
    private VBox pauseMenuContainer;
    /** Conteneur pour le menu de fin de partie affiché à la fin du jeu. */
    @FXML
    private VBox finMenuContainer;
    /** Label affichant le message de fin de partie (victoire, défaite, temps écoulé). */
    @FXML
    private Label messageFinPartieLabel;

    /** Timeline gérant le timer principal du jeu (compte à rebours). */
    private Timeline gameTimer;
    /** Temps restant de la partie en secondes (initialisé à 120 secondes). */
    private int tempsRestant = 120;

    /** Liste des joueurs participant à la partie. */
    private List<Joueur_Personnage> joueurs = new ArrayList<>();
    /** Liste des bots participant à la partie. */
    private List<Bot_Personnage> bot = new ArrayList<>();
    /** Liste des bombes actives sur le terrain de jeu. */
    private List<Bombe> listeBombes = new ArrayList<>();
    /** Liste des drapeaux présents sur le terrain de jeu. */
    private List<Drapeau> listeDrapeaux = new ArrayList<>();
    /** Liste des bonus disponibles sur le terrain de jeu. */
    private List<Bonus> listeBonus = new ArrayList<>();

    /** Indique si le jeu est actuellement en pause. */
    private boolean paused = false;
    /** Indique si la partie est terminée. */
    private boolean partieTerminee = false;

    /** Zone de jeu principale contenant la grille de jeu. */
    @FXML
    private VBox gameArea;
    /** Instance du jeu contenant la logique principale. */
    Game game = new Game();

    /** Affichage de la grille de jeu. */
    private GameGrid gameGridDisplay;
    /** Bouton de démarrage du jeu. */
    @FXML
    private Button startButton;

    // affichge des scores :
    /** Label affichant le nom et le score du joueur 1. */
    @FXML
    private Label labelJ1;
    /** Label affichant le nom et le score du joueur 2. */
    @FXML
    private Label labelJ2;
    /** Label affichant le nom et le score du joueur 3. */
    @FXML
    private Label labelJ3;
    /** Label affichant le nom et le score du joueur 4. */
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


    /** Label affichant les résultats de la partie. */
    @FXML
    private Label resultLabel;

    /** Indique si la partie est terminée (doublon de partieTerminee). */
    private boolean partieEstTerminee = false;
    // Obtention des scoresCTF.txt
    /** Liste des scores chargés depuis le fichier de scores. */
    private List<String> scores;
    /** Timer utilisé pour la gestion des explosions de bombes. */
    private int derID;

    /** Timer utilisé pour la gestion des explosions de bombes. */
    private Timer timer; // Timer pour mettre à jour le score lors de l'explosion de la bombe

    /** Label affichant le temps restant de la partie. */
    @FXML
    private Label timerLabel;

    /**
     * Démarre une nouvelle partie de jeu CTF.
     * Initialise la grille de jeu, les joueurs, les drapeaux et configure les contrôles.
     * Gère également la lecture des pseudos et l'initialisation des scores.
     *
     * @throws IOException si une erreur survient lors de la lecture du fichier de scores
     * @throws URISyntaxException si l'URI du fichier de scores est invalide
     */
    @FXML
    public void startGame() throws IOException, URISyntaxException {
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
        Joueur_Personnage pacman = new Joueur(game, player1StartX, player1StartY, 1);
        Joueur_Personnage fantome = new Joueur(game, player2StartX, player2StartY, 2);
        Joueur_Personnage pacman2 = new Joueur(game, player3StartX, player3StartY, 3);
        Joueur_Personnage pacman3 = new Joueur(game, player4StartX, player4StartY, 4);

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

        // ----- Traitement des pseudos ------
        // Chargement du fichier des scores
        URL resource = getClass().getResource("/scoresCTF.txt");
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
     * Gère les mouvements des joueurs et les actions de placement de bombes.
     * Traite les événements clavier pour contrôler les 4 joueurs avec des touches différentes.
     * Vérifie également les collisions avec les bonus et les drapeaux après chaque mouvement.
     *
     * @param event l'événement clavier déclenché
     * @param j1 le joueur 1
     * @param j2 le joueur 2
     * @param j3 le joueur 3
     * @param j4 le joueur 4
     */
    private void handlePlayerMovement(KeyEvent event, Joueur_Personnage j1, Joueur_Personnage j2, Joueur_Personnage j3, Joueur_Personnage j4) {
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
                    Bombe bomb = new Bombe( px, py, rayon, game, gameGridDisplay, joueurs, bot, j1, listeBombes); // Création de la bombe
                    startTimer(bomb, 1); // Traitement des cores de la bombe
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
                    Bombe bomb = new Bombe(px2, py2, rayon, game, gameGridDisplay, joueurs, bot, j2, listeBombes); // Création de la bombe
                    startTimer(bomb, 2); // Traitement des cores de la bombe
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
                    Bombe bomb = new Bombe( px3, py3, rayon, game, gameGridDisplay, joueurs, bot, j3, listeBombes); // Création de la bombe
                    startTimer(bomb, 3); // Traitement des cores de la bombe
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
                    Bombe bomb = new Bombe( px4, py4, rayon, game, gameGridDisplay, joueurs, bot, j4, listeBombes); // Création de la bombe
                    startTimer(bomb, 4); // Traitement des cores de la bombe                    j4.marquerBombePlacee();
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

//    /**
//     * Initialise la liste des bonus existants en récupérant les bonus actifs du jeu.
//     * Vide la liste actuelle et la remplit avec les bonus disponibles.
//     */
//    public void initialiserBonusExistants() {
//        listeBonus.clear();
//
//        if (game != null) {
//            listeBonus.addAll(game.getActiveBonuses());
//            System.out.println("Nombre de bonus récupérés via Game: " + listeBonus.size());
//            for (Bonus bonus : listeBonus) {
//                System.out.println("Bonus trouvé à (" + bonus.getBonusX() + ", " + bonus.getBonusY() + ") - Type: " + bonus.getTypeBonusString());
//            }
//        } else {
//            System.out.println("Game est null");
//        }
//    }

    /**
     * Méthode d'initialisation appelée automatiquement lors du chargement du contrôleur FXML.
     * Configure la visibilité des éléments d'interface utilisateur.
     */
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

    /**
     * Trouve la ligne correspondant à un nom de joueur dans le fichier de scores.
     *
     * @param nom le nom du joueur à rechercher
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
     * Extrait le score d'une ligne donnée du fichier de scores.
     *
     * @param numLigne le numéro de ligne à analyser
     * @return le score contenu dans la ligne
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
     * Ajoute ou met à jour un score dans la liste des scores.
     * Si le joueur n'existe pas, l'ajoute. Si il existe et que le nouveau score est meilleur, le met à jour.
     *
     * @param nom le nom du joueur
     * @param score le score à enregistrer
     * @param ligne la ligne correspondante dans le fichier
     */
    public void ajouterScore(String nom, int score, int ligne) {
        if (ligne == scores.size()-1) scores.add(nom + " " + score); // si le couple pseudo score n'est pas encore enregistré
        else if (score > getScoreLigne(ligne)){ // si le pseudo est déjà enregistré et que le score est superieur à celui enregistré
            scores.set(ligne, nom + " " + score);
        }
    }

    /**
     * Sauvegarde la liste des scores dans le fichier scoresCTF.txt.
     *
     * @param lignes la liste des lignes à sauvegarder
     * @throws IOException si une erreur survient lors de l'écriture du fichier
     */
    public void updateFile(List<String> lignes) throws IOException {
        Path cheminFichier = Paths.get("src/main/resources/scoresCTF.txt");

        if (!Files.exists(cheminFichier)) {
            throw new IOException("Le fichier n'existe pas : " + cheminFichier.toAbsolutePath());
        }

        Files.write(cheminFichier, lignes);
    }

    /**
     * Met à jour l'affichage des scores des joueurs dans l'interface utilisateur.
     */
    public void refreshScores() {
        // Maj des pseudos
        labelJ1.setText(nomJ1 + " : " + scoreJ1);
        labelJ2.setText(nomJ2 + " : " + scoreJ2);
        labelJ3.setText(nomJ3 + " : " + scoreJ3);
        labelJ4.setText(nomJ4 + " : " + scoreJ4);
    }

    /**
     * Démarre un timer pour gérer l'explosion d'une bombe et la mise à jour des scores.
     *
     * @param bomb la bombe à surveiller
     * @param joueur le numéro du joueur qui a placé la bombe
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
     * Ajoute les points obtenus par l'explosion d'une bombe au score du joueur correspondant.
     * Met à jour le fichier de scores et l'affichage.
     *
     * @param bomb la bombe qui a explosé
     * @param Joueur le numéro du joueur qui a placé la bombe
     * @throws IOException si une erreur survient lors de la sauvegarde des scores
     */
    private void ajoutScoreExplosion(Bombe bomb, int Joueur) throws IOException {
        switch (Joueur){
            case 1:
                scoreJ1 += bomb.getScoreJoueur();    // Ajout des scores de la bombe à scoreJ1
                joueurs.get(0).score = scoreJ1;           // Maj du score de pacman1 dans la classe PacMan_Personnage
                ajouterScore(nomJ1, scoreJ1, ligneJ1);      // Maj de la variable scores
                updateFile(scores);                         // sauvegarde du nouveau score
                //System.out.println(scoreJ1 + " " + bomb.getScoreJoueur());
                break;
            case 2:
                scoreJ2 += bomb.getScoreJoueur();   // Ajout des scores de la bombe à scoreJ2
                joueurs.get(1).score = scoreJ1;           // Maj du score de pacman1 dans la classe PacMan_Personnage
                ajouterScore(nomJ2, scoreJ2, ligneJ2);      // Maj de la variable scores
                updateFile(scores);                         // sauvegarde du nouveau score
                //System.out.println("Joueur 2 : " + scoreJ2);
                break;
            case 3:
                scoreJ3 += bomb.getScoreJoueur();   // Ajout des scores de la bombe à scoreJ3
                joueurs.get(2).score = scoreJ1;           // Maj du score de pacman1 dans la classe PacMan_Personnage
                ajouterScore(nomJ3, scoreJ3, ligneJ3);      // Maj de la variable scores
                updateFile(scores);                         // sauvegarde du nouveau score
                //System.out.println(scoreJ3 + " " + bomb.getScoreJoueur());
                break;
            case 4:
                scoreJ4 += bomb.getScoreJoueur();   // Ajout des scores de la bombe à scoreJ4
                joueurs.get(3).score = scoreJ1;           // Maj du score de pacman1 dans la classe PacMan_Personnage
                ajouterScore(nomJ4, scoreJ4, ligneJ4);      // Maj de la variable scores
                updateFile(scores);                         // sauvegarde du nouveau score
                //System.out.println(scoreJ4 + " " + bomb.getScoreJoueur());
                break;
        }
        refreshScores();    // Maj du bandeau des scores
    }

    /**
     * Bascule entre l'état de pause et l'état de jeu normal.
     * Affiche ou masque le menu de pause et contrôle le timer du jeu.
     */
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

    /**
     * Retourne au menu principal du jeu.
     * Arrête le timer de jeu et charge l'interface du menu principal.
     *
     * @param event l'événement qui a déclenché l'action
     */
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

    /**
     * Reprend le jeu après une pause.
     * Masque le menu de pause et relance le timer de jeu.
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
     */
    @FXML
    public void quittertout() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Lance le timer principal du jeu avec un compte à rebours.
     * Gère l'affichage du temps restant et déclenche la fin de partie si le temps s'écoule.
     */
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
                verifierFinDePartie();
            }
        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    /**
     * Vérifie les conditions de fin de partie.
     * Contrôle si un joueur a capturé tous les drapeaux ennemis ou si le temps est écoulé.
     */
    private void verifierFinDePartie() {
        if (partieTerminee) return;

        // --- Priorité 1: Victoire par capture de drapeaux ennemis ---
        // Un joueur gagne s'il a capturé tous les drapeaux ennemis
        // Le nombre de drapeaux ennemis à capturer est (nombre total de joueurs - 1)
        int nombreDeDrapeauxEnnemisACapturer = joueurs.size() - 1;

        for (Joueur_Personnage joueur : joueurs) {
            if (joueur.estVivant() && joueur.getDrapeauxCaptures() >= nombreDeDrapeauxEnnemisACapturer) {
                // Appelle finDePartie qui gère l'arrêt du jeu et l'affichage du menu
                finDePartie("VICTOIRE !",  joueur.nom + " a capturé tous les drapeaux.", true);
                return;
            }
        }

        // --- Priorité 2: Victoire par élimination (un seul joueur ou aucun joueur vivant) ---
        long joueursVivantsCount = joueurs.stream().filter(Joueur_Personnage::estVivant).count();
        if (joueursVivantsCount <= 1) {
            Joueur_Personnage dernierJoueurVivant = null;
            if (joueursVivantsCount == 1) {
                dernierJoueurVivant = joueurs.stream().filter(Joueur_Personnage::estVivant).findFirst().orElse(null);
            }

            if (dernierJoueurVivant != null) {
                finDePartie("VICTOIRE !", dernierJoueurVivant.nom + " est le dernier survivant.", true);
            } else {
                finDePartie("MATCH NUL", "Tous les joueurs ont été éliminés.", false);
            }
            return;
        }

        // --- Priorité 3: Temps écoulé ---
        if (tempsRestant <= 0) {
            Joueur_Personnage ctfWinner = determineCTFWinner(); // Détermine le gagnant par drapeaux au moment T
            if (ctfWinner != null) {
                finDePartie("VICTOIRE !", "Le temps est écoulé. " + ctfWinner.nom + " a capturé le plus de drapeaux.", true);
            } else {
                finDePartie("MATCH NUL", "Le temps est écoulé. Aucun joueur n'a capturé le plus de drapeaux ou égalité.", false);
            }
            return;
        }
    }

    /**
     * Termine la partie et déclenche l'affichage du menu de fin.
     * Cette méthode ne gère PAS directement les messages affichés, elle les reçoit en paramètres.
     *
     * @param mainMessage Le message principal à afficher (ex: "VICTOIRE!", "MATCH NUL")
     * @param resultDetailsMessage Le message de détails (ex: "Le joueur X a capturé tous les drapeaux.")
     * @param estVictoireGlobale Indique si c'est une victoire (true) ou un match nul/défaite (false)
     */
    private void finDePartie(String mainMessage, String resultDetailsMessage, boolean estVictoireGlobale) {
        if (partieTerminee) return;
        partieTerminee = true;

        System.out.println("Partie terminée."); // Log de base

        arreterJeu(); // Arrête le timer et autres processus du jeu

        // Délégue l'affichage à configurerAffichageFinDePartie
        Platform.runLater(() -> {
            configurerAffichageFinDePartie(mainMessage, resultDetailsMessage, estVictoireGlobale);

            // Rendre le conteneur du menu de fin visible
            finMenuContainer.setVisible(true);
            finMenuContainer.setManaged(true);
        });
    }

    /** Arrête le jeu immédiatement */
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

    /**
     * Vérifie quels joueurs sont encore en vie.
     * Termine la partie si tous les joueurs sont morts.
     */
    private void verifierJoueursVivants() {
        int joueursVivants = 0;
        Joueur_Personnage dernierJoueurVivant = null;
        for (Joueur_Personnage p : joueurs) {
            if (p.estVivant()) {
                joueursVivants++;
                dernierJoueurVivant = p;
            }
        }
    }
    /**
     * Nouveau: Vérifie si tous les drapeaux de la partie ont été capturés.
     *
     * @return true si tous les drapeaux sont capturés, false sinon.
     */
    private boolean verifierTousDrapeauxCaptures() {
        for (Drapeau drapeau : listeDrapeaux) {
            if (!drapeau.isCaptured()) {
                return false; // Au moins un drapeau n'est pas encore capturé
            }
        }
        return true; // Tous les drapeaux sont capturés
    }

    /**
     * Nouveau: Détermine le gagnant du mode CTF en se basant sur le nombre de drapeaux capturés.
     * En cas d'égalité, le score peut être utilisé comme critère secondaire.
     *
     * @return Le Joueur_Personnage qui a capturé le plus de drapeaux, ou null en cas d'égalité parfaite.
     */
    private Joueur_Personnage determineCTFWinner() {
        Joueur_Personnage ctfWinner = null;
        int maxDrapeauxCaptures = -1;

        for (Joueur_Personnage joueur : joueurs) {
            if (joueur.getDrapeauxCaptures() > maxDrapeauxCaptures) {
                maxDrapeauxCaptures = joueur.getDrapeauxCaptures();
                ctfWinner = joueur;
            } else if (joueur.getDrapeauxCaptures() == maxDrapeauxCaptures && ctfWinner != null) {
                // Gérer les égalités : par exemple, le joueur avec le score le plus élevé gagne
                if (joueur.getScore() > ctfWinner.getScore()) {
                    ctfWinner = joueur;
                } else if (joueur.getScore() == ctfWinner.getScore()) {
                    // Égalité parfaite, peut-être retourner null pour un match nul ou définir d'autres critères
                    ctfWinner = null; // Ou laisser le premier trouvé si aucune autre règle n'est définie
                }
            }
        }
        return ctfWinner;
    }

    /**
     * Relance une nouvelle partie avec les mêmes paramètres.
     * Réinitialise tous les éléments de jeu et redémarre le timer.
     *
     * @throws IOException si une erreur survient lors de la réinitialisation
     */
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

        Joueur_Personnage pacman = new Joueur(game, player1StartX, player1StartY, 1);
        Joueur_Personnage fantome = new Joueur(game, player2StartX, player2StartY, 2);
        Joueur_Personnage pacman2 = new Joueur(game, player3StartX, player3StartY, 3);
        Joueur_Personnage pacman3 = new Joueur(game, player4StartX, player4StartY, 4);

        Drapeau drapeau1 = new Drapeau(player1StartX, player1StartY, pacman, Color.YELLOW);
        Drapeau drapeau2 = new Drapeau(player2StartX, player2StartY, fantome, Color.CYAN);
        Drapeau drapeau3 = new Drapeau(player3StartX, player3StartY, pacman2, Color.LIGHTGREEN);
        Drapeau drapeau4 = new Drapeau(player4StartX, player4StartY, pacman3, Color.RED);

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

    /**
     * Vérifie si un joueur entre en collision avec un bonus sur le terrain.
     * Récupère les bonus actifs depuis le jeu et applique leurs effets si une collision est détectée.
     *
     * @param joueur le joueur dont il faut vérifier les collisions
     */
    private void verifierCollisionBonus(Joueur_Personnage joueur) {
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
