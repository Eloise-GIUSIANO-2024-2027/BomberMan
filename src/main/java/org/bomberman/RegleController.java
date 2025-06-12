package org.bomberman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Le contrôleur {@code RegleController} gère les interactions de l'utilisateur
 * sur la page des règles du jeu.
 * Il permet notamment de revenir au menu principal.
 */
public class RegleController {
    /**
     * Gère l'action de retour au menu principal.
     * Cette méthode est appelée lorsqu'un événement (par exemple, un clic sur un bouton)
     * est déclenché sur l'élément FXML associé.
     * Elle charge la scène du menu, y applique une feuille de style CSS,
     * puis affiche cette nouvelle scène dans la fenêtre principale du jeu.
     *
     * @param event L'événement d'action qui a déclenché cet appel de méthode.
     * Il est utilisé pour récupérer la fenêtre (Stage) actuelle.
     */
    @FXML
    public void retourMenu(ActionEvent event) {
        try {
            // Charger le FXML du menu
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("menu.fxml"));
            Parent menuRoot = loader.load();
            Scene menuScene = new Scene(menuRoot, 820, 650);
//
//            // Charger le CSS (assure-toi que ton fichier CSS est accessible et partagé)
//            menuScene.getStylesheets().add(getClass().getResource("styleMenu.css").toExternalForm());
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
}