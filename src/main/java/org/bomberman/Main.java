package org.bomberman;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        // Charger le fichier FXML
//         StackPane root = FXMLLoader.load(getClass().getResource("game.fxml"));
//
//
//        // Créer une scène avec la racine chargée depuis FXML
//        Scene scene = new Scene(root, 600, 600);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("game.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        // Configurer et afficher la fenêtre
        primaryStage.setTitle("Bomberman 90's");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
