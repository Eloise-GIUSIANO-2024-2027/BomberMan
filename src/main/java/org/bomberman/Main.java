package org.bomberman;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("menu.fxml"));
    Scene scene = new Scene(loader.load(),800 , 630);

    public Main() throws IOException {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {



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
