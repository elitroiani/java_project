package battleship;

import model.*;
import view.*;
import controller.BattleController;
import player.*;
import ai.*;
import javax.swing.SwingUtilities;

/**
 * Entry point of the Battleship application.
 * This class handles the initial application bootstrapping, the transition between 
 * the main menu and the game session, and the dynamic injection of AI strategies.
 */
public class Main {

    public static void main(String[] args) {
    	// Launch the application by displaying the difficulty selection menu
        showDifficultyMenu();
    }

    /**
     * Initializes and displays the Start Menu.
     * This method is wrapped in SwingUtilities.invokeLater to ensure 
     * Thread-Safety within the Event Dispatch Thread (EDT).
     */
    public static void showDifficultyMenu() {
        SwingUtilities.invokeLater(() -> {
            StartView startScreen = new StartView();
            startScreen.setVisible(true);

            // Configure Difficulty Listeners: Each button choice triggers the game launch 
            // with a specific AI strategy.
            startScreen.setDifficultyListener("easy", e -> launchGame(startScreen, "EASY"));
            startScreen.setDifficultyListener("medium", e -> launchGame(startScreen, "MEDIUM"));
            startScreen.setDifficultyListener("hard", e -> launchGame(startScreen, "HARD"));
            startScreen.setDifficultyListener("expert", e -> launchGame(startScreen, "EXPERT"));
        });
    }

    /**
     * Orchestrates the setup of a new game session.
     * It handles the transition from the menu to the main game view, 
     * initializing the Model, View, and Controller (MVC) components.
     * @param startScreen The reference to the menu window to be disposed.
     * @param difficulty  The selected difficulty level string.
     */
    private static void launchGame(StartView startScreen, String difficulty) {
        
        startScreen.dispose();
        // 1. Cleanup: Dispose of the menu view to free resources
    	if (startScreen != null) startScreen.setVisible(false);
        
    	// 2. Domain Initialization: Set up game rules and grid environments
        GameConfig config = new GameConfig(); 
        Grid humanGrid = new Grid(10, 10);
        Grid aiGrid = new Grid(10, 10);

        // 3. Player Setup: Instantiate the human commander and the CPU opponent
        Player human = new HumanPlayer("Comandante", humanGrid);
        AIPlayer ai = new AIPlayer("CPU " + difficulty, aiGrid);

        // 4. Strategy Injection: Use a Factory method to create the AI 'brain' 
        // based on the chosen difficulty and inject it into the AIPlayer instance.
        Reasoner brain = createReasoner(difficulty, ai, config);
        ai.setReasoner(brain);

        // 5. MVC Assembly: Instantiate the GameState (Model) and the BattleView (View)
        GameState state = new GameState(human, ai, config);
        BattleView view = new BattleView(10, 10);

        // 6. Callback Definition: Define the action to be performed when exiting the game 
        // (returning to the main menu).
        Runnable backToMenuAction = () -> showDifficultyMenu();
        
        // 7. Controller Handover: Initialize the BattleController to manage the game loop.
        new BattleController(state, view, backToMenuAction);

        // 8. Execution: Display the main game board
        view.setVisible(true);
        
        if (startScreen != null) startScreen.dispose();
    }

    /**
     * Factory Method for Reasoner instances.
     * Implements the Strategy Pattern by returning the appropriate AI logic 
     * based on the user's difficulty selection.
     * @param level  The difficulty level selected by the user.
     * @param ai     The AIPlayer context for the reasoner.
     * @param config The game configuration parameters.
     * @return A concrete implementation of the Reasoner interface.
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