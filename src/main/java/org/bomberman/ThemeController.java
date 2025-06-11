package org.bomberman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class ThemeController {
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
    public void themeChangeDefault(ActionEvent event) throws IOException {
        try (FileWriter writer = new FileWriter(new File("src/main/resources/data.txt"))) {
            writer.write("default");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture : " + e.getMessage());
            throw e;
        }

    }
    public void themeChangeWix(ActionEvent event) throws IOException {
        try (FileWriter writer = new FileWriter(new File("src/main/resources/data.txt"))) {
            writer.write("wix");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'écriture : " + e.getMessage());
            throw e;
        }
    }
}