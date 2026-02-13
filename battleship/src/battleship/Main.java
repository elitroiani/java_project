package battleship;

import model.*;
import view.*;
import controller.BattleController;
import player.*;
import ai.*;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        // Avvio della GUI di selezione
        SwingUtilities.invokeLater(() -> {
            StartView startScreen = new StartView();
            startScreen.setVisible(true);

            // Configuriamo i listener per i pulsanti della difficoltà
            startScreen.setDifficultyListener("easy", e -> launchGame(startScreen, "EASY"));
            startScreen.setDifficultyListener("medium", e -> launchGame(startScreen, "MEDIUM"));
            startScreen.setDifficultyListener("hard", e -> launchGame(startScreen, "HARD"));
            startScreen.setDifficultyListener("expert", e -> launchGame(startScreen, "EXPERT"));
        });
    }

    /**
     * Metodo che orchestra la creazione del gioco dopo la scelta dell'utente.
     */
    private static void launchGame(StartView startScreen, String difficulty) {
        // 1. Chiudiamo il menu di selezione
        startScreen.dispose();

        // 2. Configurazione e Griglie
        GameConfig config = new GameConfig(); 
        Grid humanGrid = new Grid(10, 10);
        Grid aiGrid = new Grid(10, 10);

        // 3. Creazione Giocatori (Corpi)
        // Creiamo l'AI senza passare subito il Reasoner
        Player human = new HumanPlayer("Comandante", humanGrid);
        AIPlayer ai = new AIPlayer("CPU " + difficulty, aiGrid);

        // 4. Creazione del Cervello (Passando l'AI già esistente)
        Reasoner brain = createReasoner(difficulty, ai, config);
        
        // 5. Connessione: Iniettiamo il cervello nel corpo dell'AI
        ai.setReasoner(brain);

        // 6. Inizializzazione MVC
        GameState state = new GameState(human, ai, config);
        BattleView view = new BattleView(10, 10);

        // 7. Il Controller prende il controllo
        new BattleController(state, view);

        // 8. Visualizzazione finestra di gioco
        view.setVisible(true);
    }

    /**
     * Factory per creare il Reasoner corretto in base alla stringa.
     */
    private static Reasoner createReasoner(String level, AIPlayer ai, GameConfig config) {
        return switch (level.toUpperCase()) {
            case "EXPERT" -> new ExpertReasoner(ai, config);
            case "HARD"   -> new HardReasoner(ai, config);
            case "MEDIUM" -> new MediumReasoner(ai, config);
            default       -> new EasyReasoner(ai, config);
        };
    }
}