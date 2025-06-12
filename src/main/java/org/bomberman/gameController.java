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

public class gameController {

    @FXML
    private VBox pauseMenuContainer;
    @FXML
    private VBox finMenuContainer;
    @FXML
    private Label messageFinPartieLabel;
    @FXML
    private Label gameStatusLabel;
    private Timeline gameTimer;
    private int tempsRestant = 120;
    private List<Joueur_Personnage> joueurs = new ArrayList<>();
    private List<Bot_Personnage> bot = new ArrayList<>();
    private List<Bombe> listeBombes = new ArrayList<>();
    private boolean paused = false;
    @FXML
    private VBox gameArea;
    Game game = new Game();

    private boolean partieTerminee = false;

    private GameGrid gameGridDisplay;
    @FXML
    private Button startButton; // Référence au bouton démarrer


    // affichge des scores :
    @FXML
    private Label labelJ1;
    @FXML
    private Label labelJ2;
    @FXML
    private Label labelJ3;
    @FXML
    private Label labelJ4;


    // Zone de saisi des pseudo
    @FXML
    private TextField saisiJ1;
    private String nomJ1;
    private int ligneJ1;
    private int scoreJ1 = 0;
    @FXML
    private TextField saisiJ2;
    private String nomJ2;
    private int ligneJ2;
    private int scoreJ2 = 0;
    @FXML
    private TextField saisiJ3;
    private String nomJ3;
    private int ligneJ3;
    private int scoreJ3 = 0;
    @FXML
    private TextField saisiJ4;
    private String nomJ4;
    private int ligneJ4;
    private int scoreJ4 = 0;


    @FXML
    private Label resultLabel;

    private boolean partieEstTerminee = false;
    // Obtention des scoresMulti.txt
    private List<String> scores;
    private int derID;


    // Partie modifiée de gameController.java
    @FXML
    private Label timerLabel;

    private Timer timer; // Timer pour mettre à jour le score lors de l'explosion de la bombe

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
        System.out.println(nom + " " + score + " " + getScoreLigne(ligne) + "   " + ligne);
        if (ligne == scores.size()-1) scores.add(nom + " " + score); // si le couple pseudo score n'est pas encore enregistré
        else if (score > getScoreLigne(ligne)){ // si le pseudo est déjà enregistré et que le score est superieur à celui enregistré
            scores.set(ligne, nom + " " + score);
        }
        System.out.println(nom + " " + score + " " + getScoreLigne(ligne) + "   " + ligne);
    }

    public void updateFile(List<String> lignes) throws IOException {
        Path cheminFichier = Paths.get("src/main/resources/scoresMulti.txt");

        if (!Files.exists(cheminFichier)) {
            throw new IOException("Le fichier n'existe pas : " + cheminFichier.toAbsolutePath());
        }

        Files.write(cheminFichier, lignes);
    }

    public void refreshScores() {
        // Maj des pseudos
        labelJ1.setText(nomJ1 + " : " + scoreJ1);
        labelJ2.setText(nomJ2 + " : " + scoreJ2);
        labelJ3.setText(nomJ3 + " : " + scoreJ3);
        labelJ4.setText(nomJ4 + " : " + scoreJ4);
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
                    Bombe bomb = new Bombe( px, py, rayon, game, gameGridDisplay, joueurs, bot, j1, listeBombes); // Création de la bombe
                    startTimer(bomb, 1); // Traitement des cores de la bombe
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
                    Bombe bomb = new Bombe(px2, py2, rayon, game, gameGridDisplay, joueurs, bot, j2, listeBombes); // Création de la bombe
                    startTimer(bomb, 2); // Traitement des cores de la bombe
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
                    Bombe bomb = new Bombe(px3, py3, rayon, game, gameGridDisplay, joueurs, bot, j3, listeBombes); // Création de la bombe
                    startTimer(bomb, 3); // Traitement des cores de la bombe
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
                    Bombe bomb = new Bombe(px4, py4, rayon, game, gameGridDisplay, joueurs, bot, j4, listeBombes); // Création de la bombe
                    startTimer(bomb, 4); // Traitement des cores de la bombe
                    j4.marquerBombePlacee();
                    gameGridDisplay.refresh();
                }
            }
        }
    }



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

        // Redémarrer le timer
        lancerTimer();
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
    private void arreterJeu() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    // MÉTHODE Accepte les messages et le statut de victoire (cette méthode est bonne)
    private void configurerAffichageFinDePartie(String mainMessage, String resultDetailsMessage, boolean estVictoireGlobale) {
        // ... (votre code existant pour configurer les labels)
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