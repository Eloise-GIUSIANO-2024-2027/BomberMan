package org.bomberman.entite;

import javafx.scene.layout.GridPane;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

// Assurez-vous que Joueur est correctement importé si cette classe n'est pas dans le même package
// import org.bomberman.entite.Joueur; // Cette ligne est nécessaire si Bonus n'est pas dans le même package que Joueur

public class Bonus {

    public void appliquerBonusVitesse(Joueur joueur, GridPane map, int bonusX, int bonusY) {
        // ... (ton code pour la suppression visuelle du bonus)

        double vitesseInitiale = joueur.vitesse; // <-- Changer int en double ici
        joueur.vitesse = vitesseInitiale / 2.0; // <-- Utiliser 2.0 pour une division flottante

        // Tu peux ajouter une vérification si tu veux une vitesse minimale (ex: ne pas passer sous 0.05)
        // if (joueur.vitesse < 0.05) {
        //     joueur.vitesse = 0.05;
        // }

        PauseTransition pause = new PauseTransition(Duration.seconds(15));
        pause.setOnFinished(event -> {
            joueur.vitesse = vitesseInitiale;
            System.out.println("Bonus de vitesse terminé. Vitesse rétablie pour le joueur.");
        });
        pause.play();

        System.out.println("Bonus de vitesse activé ! Vitesse du joueur augmentée pour 15 secondes.");
    }
}