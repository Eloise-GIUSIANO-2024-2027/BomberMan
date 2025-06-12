/**
 * Package principal de l'application Bomberman.
 */
package org.bomberman;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale du Jeu Bomberman.
 * Point d'entrée de l'application JavaFX.
 * Charge le menu principal et initialise la scène principale.
 */
public class Main extends Application {

    /**
     * Méthode start() de l'application JavaFX.
     * C'est la méthode principale où l'interface utilisateur est construite et affichée.
     *
     * @param primaryStage Le Stage principal de l'application.
     * @throws Exception Si une erreur survient lors du chargement du FXML ou du CSS.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("menu.fxml"));
        Font.loadFont(getClass().getResourceAsStream("/police/PressStart2P-Regular.ttf"), 5);
        Scene scene = new Scene(loader.load(),820 , 650);

        String cssPath = getClass().getResource("/styleMenu.css").toExternalForm();
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        } else {
            System.err.println("Erreur: Le fichier CSS 'styleMenu.css' n'a pas été trouvé. Vérifiez le chemin '/org/bomberman/styleMenu.css'.");
        }


        primaryStage.setTitle("Bomberman - Multijoueur Local");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Focus sur la scène pour capturer les événements clavier
        scene.getRoot().requestFocus();
    }

    /**
     * Méthode main() de l'application.
     * Point de départ de l'exécution du programme.
     *
     * @param args Les arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
