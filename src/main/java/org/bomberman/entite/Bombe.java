package org.bomberman.entite;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.application.Platform;
import org.bomberman.Bot_Personnage;
import org.bomberman.Game;
import org.bomberman.GameGrid;
import org.bomberman.PacMan_Personnage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Classe représentant une bombe dans le jeu Bomberman.
 * Une bombe hérite d'ImageView pour l'affichage graphique et gère son explosion
 * avec un rayon d'effet, pouvant détruire des murs, tuer des joueurs/bots,
 * détruire des bonus existants et générer de nouveaux bonus.
 *
 * <p>La bombe explose automatiquement après 2 secondes ou peut exploser
 * immédiatement si touchée par une autre explosion (réaction en chaîne).</p>
 *
 * @author Lou Contrucci, Eloïse Giusiano, Gustin Mailhe
 */
public class Bombe extends ImageView {

    /** Position X de la bombe sur la grille de jeu */
    private int x, y;
    /** Rayon d'explosion de la bombe (nombre de cases affectées dans chaque direction) */
    private int rayon;
    /** Référence vers l'instance principale du jeu */
    private Game game;
    /** Référence vers la grille de jeu pour l'affichage */
    private GameGrid gameGrid;
    /** Liste des joueurs présents dans le jeu */
    private List<PacMan_Personnage> joueurs;
    /** Liste des bots présents dans le jeu */
    private List<Bot_Personnage> botList;
    /** Joueur qui a posé cette bombe (peut être null si posée par un bot) */
    private PacMan_Personnage poseurJoueur;
    /** Bot qui a posé cette bombe (peut être null si posée par un joueur) */
    private Bot_Personnage poseurBot;
    /** Liste de toutes les bombes actives dans le jeu */
    private List<Bombe> bombes;
    /** Indique si la bombe est encore présente sur le terrain */
    private boolean estPresent = true;
    /** Indique si la bombe a déjà explosé (évite les explosions multiples) */
    private boolean aExplose = false;
    /** Timer gérant le délai d'explosion automatique */
    private Timer timer;
    /** Score accumulé par cette bombe (destruction de murs, élimination d'entités) */
    private int scoreJoueur;

    /**
     * Constructeur de la classe Bombe.
     * Crée une nouvelle bombe à la position spécifiée avec le rayon d'explosion donné.
     * La bombe est automatiquement ajoutée à la grille et programmée pour exploser après 2 secondes.
     *
     * @param x position X de la bombe sur la grille (coordonnée de colonne)
     * @param y position Y de la bombe sur la grille (coordonnée de ligne)
     * @param rayon rayon d'explosion de la bombe (nombre de cases affectées dans chaque direction)
     * @param game référence vers l'instance principale du jeu
     * @param gameGrid référence vers la grille de jeu pour l'affichage
     * @param joueurs liste des joueurs présents dans le jeu
     * @param botList liste des bots présents dans le jeu
     * @param poseurJoueur joueur qui pose cette bombe (null si posée par un bot)
     * @param bombes liste de toutes les bombes actives dans le jeu
     *
     * @throws RuntimeException si le chargement de l'image de la bombe échoue
     * @throws IllegalArgumentException si la position spécifiée est hors des limites de la grille
     */
    public Bombe(int x, int y, int rayon, Game game, GameGrid gameGrid, List<PacMan_Personnage> joueurs, List<Bot_Personnage> botList, PacMan_Personnage poseurJoueur, List<Bombe> bombes) {
        this.x = x;
        this.y = y;
        this.rayon = rayon;
        this.game = game;
        this.gameGrid = gameGrid;
        this.joueurs = joueurs;
        this.botList = botList;
        this.poseurJoueur = poseurJoueur;
        this.poseurBot = poseurBot;
        this.bombes = bombes;

        try {
            Image bombeImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/fxs/imgBombe.gif")), 48, 48, false, false);
            this.setImage(bombeImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image de la bombe: " + e.getMessage());
        }

        int[][] grid = game.getGrid();
        if (x >= 0 && x < grid[0].length && y >= 0 && y < grid.length) {
            // Marquer la case comme occupée par une bombe
            grid[y][x] = 3; // 3 = bombe
            game.setGrid(grid);

            System.out.println("Bombe créée aux coordonnées: " + x + "," + y);

            // Ajouter à la grille
            Platform.runLater(() -> {
                gameGrid.getChildren().add(this);
                GridPane.setColumnIndex(this, x); // x est la colonne
                GridPane.setRowIndex(this, y);
                this.toFront(); // Assurer que la bombe est visible
            });

            startTimer();
        } else {
            System.err.println("Position invalide pour la bombe: " + x + "," + y);
        }
    }

    /**
     * Démarre le timer d'explosion automatique de la bombe.
     * La bombe explosera automatiquement après 2 secondes si elle n'est pas
     * déclenchée plus tôt par une explosion en chaîne.
     *
     * @implNote Utilise un Timer Java avec une TimerTask pour gérer le délai.
     *           L'explosion s'exécute sur le thread JavaFX via Platform.runLater().
     */
    private void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> explode());
            }
        }, 2000); // 2 secondes
    }

    /**
     * Force l'explosion immédiate de la bombe.
     * Cette méthode est utilisée pour les explosions en chaîne lorsqu'une bombe
     * est touchée par l'explosion d'une autre bombe.
     *
     * @implNote Annule le timer d'explosion automatique avant de déclencher l'explosion.
     */
    public void exploserImmediatement() {
        if (timer != null) {
            timer.cancel(); // Annuler le timer normal
        }
        Platform.runLater(() -> explode());
    }

    /**
     * Gère l'explosion de la bombe et tous ses effets.
     *
     * <p>Cette méthode complexe gère les aspects suivants de l'explosion :</p>
     * <ul>
     *   <li>Propagation de l'explosion dans les 4 directions selon le rayon</li>
     *   <li>Destruction des murs cassables et attribution de points</li>
     *   <li>Affichage des effets visuels d'explosion</li>
     *   <li>Vérification et application des dégâts aux joueurs et bots</li>
     *   <li>Destruction des bonus existants dans la zone d'explosion</li>
     *   <li>Déclenchement des explosions en chaîne sur les autres bombes</li>
     *   <li>Génération aléatoire de nouveaux bonus sur les murs détruits</li>
     * </ul>
     *
     * @implNote L'explosion se propage jusqu'à rencontrer un mur incassable ou
     *           détruire un mur cassable. Les effets visuels sont gérés avec des
     *           timers séparés pour leur suppression après 300ms.
     */
    private void explode() {
        int[][] grid = game.getGrid();
        aExplose = true;

        // AJOUTÉ : Liste pour stocker les positions où générer des bonus plus tard
        List<int[]> positionsBonusPotentiels = new ArrayList<>();

        // Centre - retirer la bombe
        grid[y][x] = 0;
        List<int[]> affectedCells = new ArrayList<>();
        affectedCells.add(new int[]{y, x});

        // Définir les quatre directions : Droite, Gauche, Bas, Haut (dy, dx)
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        // Propager l'explosion dans chaque direction
        for (int[] dir : directions) {
            int dy = dir[0];
            int dx = dir[1];

            for (int i = 1; i <= rayon; i++) { // Itérer vers l'extérieur jusqu'au rayon
                int ny = y + dy * i;
                int nx = x + dx * i;

                // Vérifier les limites de la grille
                if (ny >= 0 && ny < grid.length && nx >= 0 && nx < grid[0].length) {
                    if (grid[ny][nx] == 1) { // Mur incassable
                        break; // Arrêter l'explosion dans cette direction
                    } else if (grid[ny][nx] == 2) { // Bloc cassable
                        System.out.println("Mur destructible détruit à (" + nx + ", " + ny + ")");

                        // MODIFIÉ : Stocker la position pour génération de bonus plus tard
                        positionsBonusPotentiels.add(new int[]{nx, ny});

                        grid[ny][nx] = 0; // Casser le bloc
                        scoreJoueur += 100; // Points pour le cassage du mur
                        affectedCells.add(new int[]{ny, nx});
                        break; // Arrêter l'explosion dans cette direction après avoir cassé le bloc
                    } else { // Espace vide
                        affectedCells.add(new int[]{ny, nx});
                    }
                } else { // Hors des limites de la grille
                    break; // Arrêter l'explosion dans cette direction
                }
            }
        }

        for (int[] cell : affectedCells) {
            int currentY = cell[0];
            int currentX = cell[1];


            ImageView explosionFx = new ImageView( new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("/fxs/explosion.gif")), 48, 48, false, false));
            explosionFx.setFitWidth(48); // Assurez-vous que la taille correspond à votre grille

            explosionFx.setFitHeight(48);

            Platform.runLater(() -> {
                gameGrid.getChildren().add(explosionFx);
                GridPane.setColumnIndex(explosionFx, currentX);
                GridPane.setRowIndex(explosionFx, currentY);
                explosionFx.toFront();
            });

            // Timer pour retirer l'image d'explosion après un court délai
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (explosionFx.getParent() instanceof Pane pane) {
                            pane.getChildren().remove(explosionFx);
                        }
                    });
                }
            }, 300);
        }

        // Vérifier les dégâts sur les joueurs
        for (PacMan_Personnage joueur : joueurs) {
            // Vérifier si le joueur est toujours vivant
            if (joueur.estVivant()) {
                int joueurGridX = joueur.getGridX(); // Colonne du joueur
                int joueurGridY = joueur.getGridY(); // Ligne du joueur

                // Vérifier si la position du joueur est dans les cellules affectées par l'explosion
                for (int[] cell : affectedCells) {
                    int affectedRow = cell[0];
                    int affectedCol = cell[1];

                    if (joueurGridY == affectedRow && joueurGridX == affectedCol) {
                        joueur.disparait(); // Appeler la méthode pour marquer le joueur comme non-vivant
                        gameGrid.getEntityLayer().getChildren().remove(joueur); // Supprimer visuellement le joueur
                        scoreJoueur += 250; // Ajout des points pour le kill
                        System.out.println("Le joueur à la position (" + joueurGridX + ", " + joueurGridY + ") a été tué par la bombe !");
                        break; // Un joueur ne peut être tué qu'une fois par explosion, pas besoin de vérifier d'autres cellules
                    }
                }
            }
        }

        // Vérifier les dégâts sur les bots
        for (Bot_Personnage bot : botList) {
            if (bot.estVivant()) {
                int botGridX = bot.getGridX();
                int botGridY = bot.getGridY();

                // Vérifier si la position du joueur est dans les cellules affectées par l'explosion
                for (int[] cell : affectedCells) {
                    int affectedRow = cell[0];
                    int affectedCol = cell[1];

                    if (botGridY == affectedRow && botGridX == affectedCol) {
                        bot.disparait();
                        gameGrid.getEntityLayer().getChildren().remove(bot); // Supprimer visuellement le joueur
                        scoreJoueur += 250; // Ajout des points pour le kill
                        System.out.println("Bot à la position (" + botGridX + ", " + botGridY + ") a été tué par la bombe !");
                        break;
                    }
                }
            }
        }

        List<Bonus> bonusADetruire = new ArrayList<>();
        List<Bonus> bonusActifs = game.getActiveBonuses(); // Récupérer tous les bonus actifs

        for (Bonus bonus : bonusActifs) {
            int bonusGridX = bonus.getBonusX();
            int bonusGridY = bonus.getBonusY();

            // Vérifier si la position du bonus est dans les cellules affectées par l'explosion
            for (int[] cell : affectedCells) {
                int affectedRow = cell[0];
                int affectedCol = cell[1];

                if (bonusGridY == affectedRow && bonusGridX == affectedCol) {
                    bonusADetruire.add(bonus);
                    System.out.println("Bonus " + bonus.getTypeBonusString() +
                            " détruit par l'explosion à (" + bonusGridX + ", " + bonusGridY + ")");
                    break; // Un bonus ne peut être détruit qu'une fois
                }
            }
        }

        // Détruire les bonus touchés
        for (Bonus bonus : bonusADetruire) {
            // Supprimer visuellement le bonus
            Platform.runLater(() -> {
                if (bonus.getImageView().getParent() != null) {
                    ((Pane) bonus.getImageView().getParent()).getChildren().remove(bonus.getImageView());
                }
            });

            // Retirer le bonus de la liste des bonus actifs du jeu
            game.removeBonus(bonus);
        }

        // EXPLOSION EN CHAÎNE : Vérifier l'impact sur les autres bombes
        List<Bombe> bombesToExplode = new ArrayList<>();
        for (Bombe autreBombe : bombes) {
            if (autreBombe != this && autreBombe.estPresent() && !autreBombe.aExplose) {
                int bombeGridX = autreBombe.getGridX();
                int bombeGridY = autreBombe.getGridY();

                for (int[] cell : affectedCells) {
                    int affectedRow = cell[0];
                    int affectedCol = cell[1];

                    if (bombeGridY == affectedRow && bombeGridX == affectedCol) {
                        bombesToExplode.add(autreBombe);
                        System.out.println("Bombe en chaîne détectée aux coordonnées: " + bombeGridX + "," + bombeGridY);
                        break;
                    }
                }
            }
        }

        // Déclencher l'explosion des bombes touchées
        for (Bombe bombeEnChaine : bombesToExplode) {
            bombeEnChaine.exploserImmediatement();
        }

        game.setGrid(grid);
        gameGrid.refresh(); // Recréer seulement la grille de terrain

        // Supprimer visuellement la bombe
        Platform.runLater(() -> {
            if (this.getParent() instanceof Pane pane) {
                pane.getChildren().remove(this);
            }
            if (poseurJoueur != null) {
                //  CORRECTION : Utiliser le nouveau système de cooldown
                poseurJoueur.activerCooldownBombe();
            }
        });

        // AJOUTÉ : Générer les nouveaux bonus APRÈS que l'explosion soit terminée
        for (int[] position : positionsBonusPotentiels) {
            int bonusX = position[0];
            int bonusY = position[1];
            generateBonusChance(bonusX, bonusY);
        }
    }

    /**
     * Marque la bombe comme disparue et la rend invisible.
     * Cette méthode est utilisée pour désactiver une bombe sans déclencher son explosion.
     */
    public void disparait() {
        this.estPresent = false;
        this.setVisible(false);
    }

    /**
     * Vérifie si la bombe est encore présente sur le terrain.
     *
     * @return true si la bombe est présente, false sinon
     */
    public boolean estPresent() {
        return this.estPresent;
    }

    /**
     * Retourne la position X de la bombe sur la grille.
     *
     * @return la coordonnée X (colonne) de la bombe
     */
    public int getGridX() {
        return x;
    }

    /**
     * Retourne la position Y de la bombe sur la grille.
     *
     * @return la coordonnée Y (ligne) de la bombe
     */
    public int getGridY() {
        return y;
    }

    /**
     * Génère aléatoirement un bonus à la position spécifiée.
     * Il y a 25% de chance (1/4) qu'un bonus soit généré à chaque appel.
     * Le type de bonus (VITESSE ou RAYON) est choisi aléatoirement avec une probabilité égale.
     *
     * @param bonusX position X où générer le bonus potentiel
     * @param bonusY position Y où générer le bonus potentiel
     *
     * @implNote Cette méthode affiche des messages de debug pour tracer la génération des bonus.
     *           Le bonus généré est automatiquement ajouté à la grille et à la liste des bonus actifs.
     */
    private void generateBonusChance(int bonusX, int bonusY) {
        double randomValue = Math.random();
        System.out.println("Tentative de génération de bonus à (" + bonusX + ", " + bonusY + ") - Valeur aléatoire: " + randomValue);

        if (randomValue < 0.25) { // 1 chance sur 4
            // Choisir aléatoirement le type de bonus (50% vitesse, 50% rayon)
            Bonus.TypeBonus type = Math.random() < 0.5 ? Bonus.TypeBonus.VITESSE : Bonus.TypeBonus.RAYON;

            Bonus bonus = new Bonus(game, bonusX, bonusY, gameGrid, type);
            game.addBonus(bonus);

            Platform.runLater(() -> {
                gameGrid.getChildren().add(bonus.getImageView());
                GridPane.setColumnIndex(bonus.getImageView(), bonusX);
                GridPane.setRowIndex(bonus.getImageView(), bonusY);
                bonus.getImageView().toFront();
            });

            System.out.println("Bonus " + type + " généré à la position: " + bonusX + ", " + bonusY);
        } else {
            System.out.println(" Pas de bonus généré à (" + bonusX + ", " + bonusY + ")");
        }
    }

    /**
     * Retourne le score total accumulé par cette bombe.
     * Le score inclut les points pour la destruction de murs (100 points chacun)
     * et l'élimination de joueurs/bots (250 points chacun).
     *
     * @return le score total accumulé par cette bombe
     */
    public int getScoreJoueur() {
        return scoreJoueur;
    }
}