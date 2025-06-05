package org.bomberman;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.awt.*;
import java.util.ArrayList;

public class JeuMain extends Application {

    private Scene scene;
    private BorderPane root;


    public static ArrayList<Obstacle> obstacles = new ArrayList<>();
    Obstacle mur1 = new Obstacle(100, 200, 200,200);
    Obstacle mur2 = new Obstacle(400, 0, 300,100);


    @Override
    public void start(Stage primaryStage) {

        root = new BorderPane();

        //Acteurs du jeu
        Personnage pacman = new Pacman();
        //Personnage fantome = new Fantome();

        pacman.setLayoutY(0);
        pacman.setLayoutX(0);
        // on positionne le fantôme
        //fantome.setLayoutY(460);
        //fantome.setLayoutX(620);
        //panneau du jeu
        Pane jeu = new Pane();
        jeu.setPrefSize(640, 480);
        jeu.getChildren().add(pacman);
        //jeu.getChildren().add(fantome);

        JeuMain.obstacles.add(mur1);
        root.getChildren().addAll(mur1);
        JeuMain.obstacles.add(mur2);
        root.getChildren().addAll(mur2);


        root.setCenter(jeu);
        //on construit une scene 640 * 480 pixels
        scene = new Scene(root);

        //Gestion du déplacement du personnage
        //deplacer(pacman, fantome);

        primaryStage.setTitle("... Pac Man ...");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Permet de gérer les événements dDARKGRAYe type clavier, pression des touches
     * pour le j1(up,down, right, left), pour le j2( z,q,s,d)
     *
     * @param j1
     * @param j2
     */
    private void deplacer(Personnage j1, Personnage j2) {
        scene.setOnKeyPressed((KeyEvent event) -> {
            double oldX1 = j1.getLayoutX();
            double oldY1 = j1.getLayoutY();

            double oldX2 = j2.getLayoutX();
            double oldY2 = j2.getLayoutY();

            switch (event.getCode()) {
                //Pacmman
                case LEFT:
                    j1.deplacerAGauche(obstacles);
                    break;
                case RIGHT:
                    j1.deplacerADroite(scene.getWidth(),obstacles);
                    break;
                case UP:
                    j1.deplacerEnHaut(obstacles);
                    break;
                case DOWN:
                    j1.deplacerEnBas(scene.getWidth(),obstacles);
                    break;
                //Fantome
                case Z:
                    j2.deplacerEnHaut(obstacles);
                    break;
                case S:
                    j2.deplacerEnBas(scene.getWidth(),obstacles);
                    break;
                case Q:
                    j2.deplacerAGauche(obstacles);
                    break;
                case D:
                    j2.deplacerADroite(scene.getWidth(),obstacles);
                    break;

            }
            if (j1.estEnCollision(j2) ) {
                System.out.println("Collision....");
                Label label = new Label("Game Over...");
                label.setStyle("-fx-font-size: 36px; -fx-text-fill: #873333; -fx-alignment: center;");
                root.setCenter(label);

            }

        });
    }


}
