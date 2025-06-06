package org.bomberman;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.Optional;

public class PacMan_JeuMain extends Application {

    private Scene scene;
    private static final ArrayList<Obstacle> lesObstacles = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();

        //Acteurs du jeu
        PacMan_Personnage pacman = new Pacman();
        //positionnement du pacman
        pacman.setLayoutX(0);
        pacman.setLayoutY(12 * PacMan_Personnage.LARGEUR_PERSONNAGE);
        PacMan_Personnage fantome = new PacMan_Fantome();

        // positionnement du fantôme
        fantome.setLayoutX(31 * PacMan_Personnage.LARGEUR_PERSONNAGE);
        fantome.setLayoutY(12 * PacMan_Personnage.LARGEUR_PERSONNAGE);

        //panneau du jeu
        Pane jeu = new Pane();
        jeu.setPrefSize(640, 480);
        jeu.getChildren().add(pacman);
        jeu.getChildren().add(fantome);
        root.setCenter(jeu);


        //ajout d'un obstacle au milieu

        Obstacle obstacleCentral = new Obstacle(60, 300, Paint.valueOf("blue"));
        obstacleCentral.setStyle("-fx-fill: brown;");
        obstacleCentral.setLayoutX(300);
        obstacleCentral.setLayoutY(80);
        root.getChildren().add(obstacleCentral);
        lesObstacles.add(obstacleCentral);
        //on construit une scene 640 * 480 pixels
        scene = new Scene(root, 640, 480);

        //Gestion du déplacement du personnage
        deplacer(pacman, fantome);

        primaryStage.setTitle("... Pac Man ...");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Permet de gérer les événements de type clavier, pression des touches
     * pour le j1(up,down, right, left), pour le j2( z,q,s,d)
     *
     * @param j1
     * @param j2
     */
    private void deplacer(PacMan_Personnage j1, PacMan_Personnage j2) {

        scene.setOnKeyPressed((KeyEvent event) -> {

            switch (event.getCode()) {
                case UP -> j1.deplacerEnHaut();
                case DOWN -> j1.deplacerEnBas(scene.getHeight());
                case RIGHT -> j1.deplacerADroite(scene.getWidth());
                case LEFT -> j1.deplacerAGauche();
                case Z -> j2.deplacerEnHaut();
                case S -> j2.deplacerEnBas(scene.getHeight());
                case D -> j2.deplacerADroite(scene.getWidth());
                case Q -> j2.deplacerAGauche();
            }
            if (j1.estEnCollision(j2)) finDuJeu();
        });
    }

    public static ArrayList<Obstacle> getLesObstacles(){
        return lesObstacles;
    }

    public void finDuJeu() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Il y a eu collision entre les 2 personnages", ButtonType.CLOSE);
        alert.setTitle("Information");
        alert.initStyle(StageStyle.UNDECORATED);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            Platform.exit();
        }
    }
}