package org.bomberman;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game.fxml"));
        Scene scene = new Scene(loader.load(),960 , 1024);

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
