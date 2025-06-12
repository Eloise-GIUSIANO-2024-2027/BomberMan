/**
 * Package principal de l'application Bomberman.
 */
package org.bomberman;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * Contrôleur JavaFX pour le menu principal du jeu Bomberman.
 * Gère les interactions utilisateur et la navigation entre les différentes scènes (modes de jeu, sélection de thème, règles).
 */
public class MenuController {
    /**
     * Le thème actuel de l'application, lu depuis un fichier.
     * Peut être "default" ou "wix".
     */
    private String theme = "default";

    /**
     * Constructeur de la classe MenuController.
     * Initialise le thème de l'application en lisant le contenu du fichier "data.txt" situé dans les ressources.
     *
     * @throws IOException Si une erreur d'entrée/sortie survient lors de la lecture du fichier de thème.
     */
    public MenuController() throws IOException {
        Path path = Paths.get("src/main/resources/data.txt");
        this.theme = Files.readString(path);
    }

    /**
     * Méthode appelée lorsque le bouton "MULTIJOUER GAME" est cliqué.
     * Charge la scène du jeu principal (game.fxml) et applique le thème CSS approprié.
     *
     * @param event L'événement d'action déclenché par le clic sur le bouton.
     */
    // Méthode appelée lorsque le bouton "NORMAL GAME" est cliqué
    @FXML
    private void startMultiGame(ActionEvent event) {
        System.out.println("Démarrer le jeu Normal !");
        // Logique pour passer à la scène du jeu principal
        try {
            // Charge le FXML du jeu (game.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game.fxml"));
            Parent gameRoot = loader.load();
            Scene gameScene = new Scene(gameRoot, 820, 650);

            String cssdef = getClass().getResource("/styleGame.css").toExternalForm();
            String csswix = getClass().getResource("/styleWix.css").toExternalForm();

            MenuController menu = new MenuController();
            System.out.println(menu.theme);
            if (menu.theme.equals("default")) {
                gameScene.getStylesheets().add(cssdef);
            } else if (menu.theme.equals("wix")) {
                gameScene.getStylesheets().add(csswix);
            }

            // Récupère le stage actuel
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(gameScene);
            stage.setTitle("Super Bomberman - Le Jeu");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du jeu : " + e.getMessage());
        }
    }

    /**
     * Méthode appelée lorsque le bouton "SOLO MODE" est cliqué.
     * Charge la scène du jeu en mode solo (soloGame.fxml) et applique le thème CSS approprié.
     *
     * @param event L'événement d'action déclenché par le clic sur le bouton.
     */
    @FXML
    private void startSoloMode(ActionEvent event) {
        System.out.println("Démarrer le mode Solo !");
        try {
            // Charge le FXML du jeu (game.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("soloGame.fxml"));
            Parent gameRoot = loader.load();
            Scene gameScene = new Scene(gameRoot, 820, 650);

            String cssdef = getClass().getResource("/styleGame.css").toExternalForm();
            String csswix = getClass().getResource("/styleWix.css").toExternalForm();

            MenuController menu = new MenuController();
            System.out.println(menu.theme);
            if (menu.theme.equals("default")) {
                gameScene.getStylesheets().add(cssdef);
            } else if (menu.theme.equals("wix")) {
                gameScene.getStylesheets().add(csswix);
            }

            // Récupère le stage actuel
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(gameScene);
            stage.setTitle("Super Bomberman - Le Jeu");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du jeu : " + e.getMessage());
        }
    }

    /**
     * Méthode appelée lorsque le bouton "CAPTURE THE FLAG" est cliqué.
     * Charge la scène du jeu en mode Capture The Flag (CTFgame.fxml) et applique le thème CSS approprié.
     *
     * @param event L'événement d'action déclenché par le clic sur le bouton.
     */
    @FXML
    private void startCTF(ActionEvent event) {
        System.out.println("Démarrer le mode Solo !");
        try {
            // Charge le FXML du jeu (game.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("CTFgame.fxml"));
            Parent gameRoot = loader.load();
            Scene gameScene = new Scene(gameRoot, 820, 650);

            String cssdef = getClass().getResource("/styleGame.css").toExternalForm();
            String csswix = getClass().getResource("/styleWix.css").toExternalForm();

            MenuController menu = new MenuController();
            System.out.println(menu.theme);
            if (menu.theme.equals("default")) {
                gameScene.getStylesheets().add(cssdef);
            } else if (menu.theme.equals("wix")) {
                gameScene.getStylesheets().add(csswix);
            }

            // Récupère le stage actuel
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(gameScene);
            stage.setTitle("Super Bomberman - Le Jeu");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du jeu : " + e.getMessage());
        }
    }

    /**
     * Méthode appelée lorsque le bouton "THEME" est cliqué.
     * Charge la scène de sélection de thème (theme.fxml).
     *
     * @param event L'événement d'action déclenché par le clic sur le bouton.
     */
    @FXML
    private void changementTheme(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("theme.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Bomberman - selection de theme");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la page des themes : " + e.getMessage());
        }
    }

    /**
     * Méthode appelée lorsque le bouton pour les règles du jeu est cliqué.
     * Charge la scène affichant les règles du jeu (regle.fxml).
     *
     * @param event L'événement d'action déclenché par le clic sur le bouton.
     */
    @FXML
    public void regleCommande(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("regle.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Bomberman - Règles du Jeu");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Erreur lors du chargement de la page des règles : " + e.getMessage());
            }

    }

    /**
     * Méthode appelée pour quitter l'application Bomberman
     * Quand on appuis sur "EXIT".
     * Ferme l'application JavaFX et termine le processus JVM.
     */
    @FXML
    public void quittertout() {
        Platform.exit(); // Fait sortir l'application JavaFX
        System.exit(0); // Optionnel: Assure la terminaison complète de la JVM (utile si des threads tournent en arrière-plan)

    }

}
