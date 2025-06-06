package org.bomberman;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

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

    public static void main(String[] args) {
        launch(args);
    }
}
