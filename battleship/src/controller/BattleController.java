package controller;

import model.*;
import view.BattleView;
import view.StartView;
import placer.*;
import player.*;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import ai.EasyReasoner;
import ai.ExpertReasoner;
import ai.HardReasoner;
import ai.MediumReasoner;
import ai.Reasoner;

/**
 * The BattleController class acts as the Mediator between the Game Model and the BattleView.
 * It manages the game lifecycle, transitioning from the ship placement phase to the 
 * tactical battle phase, while coordinating Human and AI turns.
 */
public class BattleController {
    private GameState model;
    private BattleView view;
    private Runnable exitAction;				// Callback to return to the main menu
    
    private ManualShipPlacer humanPlacer;
    private List<ShipConfig> shipsToPlace;
    
    private int currentShipIndex = 0;
    private boolean isBattlePhase = false;
    private Timer aiTimer;							// Timer to simulate CPU "thinking" time and prevent UI freezing

    /**
     * Constructor initializes the controller, sets up listeners, and prepares 
     * the initial placement state.
     */
    public BattleController() {
    	this.launchGame();
    }

    /**
     * Attaches MouseListeners to every cell in both player and enemy grids.
     */
    private void initGridListeners() {
        int w = model.getConfig().getWidth();
        int h = model.getConfig().getHeight();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                final int x = i; final int y = j;
                
                // PLAYER GRID: Used for manual ship placement
                view.setGridListener(false, x, y, e -> handlePlacementClick(x, y));
                
                // ENEMY GRID: Used for firing at the opponent
                view.setGridListener(true, x, y, e -> handleBattleClick(x, y));
            }
        }
    }

    // --- PLACEMENT LOGIC ---

    /**
     * Handles the logic for placing ships on the human player's grid.
     * Validates placement via the Placer module and updates the View accordingly.
     */
    private void handlePlacementClick(int x, int y) {
        if (isBattlePhase || currentShipIndex >= shipsToPlace.size()) return;

        ShipConfig config = shipsToPlace.get(currentShipIndex);
        Ship ship = new Ship(config);
        boolean horizontal = view.isHorizontal();
        
        // Validation check through the specialized Placer helper
        if (humanPlacer.placeShip(model, model.getHumanPlayer(), ship, x, y, horizontal)) {
            renderPlacedShip(ship, x, y, horizontal);
            currentShipIndex++;

            if (currentShipIndex < shipsToPlace.size()) {
                updatePlacementStatus();
            } else {
                startBattlePhase();
            }
        } else {
            view.setStatus("Posizione non valida!");
        }
    }

    /**
     * Visualizes the placed ship on the UI grid after successful validation.
     */
    private void renderPlacedShip(Ship ship, int x, int y, boolean horizontal) {
        for (int i = 0; i < ship.getSize(); i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;
            view.updateCell(false, cx, cy, java.awt.Color.DARK_GRAY, "");
        }
    }

    /**
     * Switches the game state from 'Placement' to 'Battle'.
     * Triggers automatic CPU ship placement using a specialized algorithm.
     */
    private void startBattlePhase() {
        isBattlePhase = true;
        view.switchToPlayMode();
        
        // CPU Auto-Setup: HardShipPlacer ensures an optimized and unpredictable fleet layout
        AutomaticShipPlacer aiPlacer = new HardShipPlacer(model.getConfig());
        aiPlacer.placeAllShips(model, model.getAiPlayer());
        
        view.setStatus("BATTAGLIA! Fuoco al nemico.");
    }

    // --- BATTLE LOGIC ---

    /**
     * Handles the human player's attack logic.
     * Updates the Model and coordinates the UI response based on the MoveResult.
     */
    private void handleBattleClick(int x, int y) {
        if (!isBattlePhase || model.isGameOver()) return;
        
        // Execute move via Model: Centralized state management
        MoveResult res = model.gameMove(model.getHumanPlayer(), new Cell(x, y));
        if (res == MoveResult.ALREADY_FIRED) return;

        processMoveResult(true, x, y, res);

        // If human misses, disable interaction and trigger CPU turn
        if (!model.isGameOver() && res == MoveResult.MISS) {
            view.setStatus("Mancato! Tocca alla CPU...");
            view.disableInteraction();
            startAiTurn();
        }
    }

    /**
     * Manages the AI turn logic using a Swing Timer.
     * The timer ensures the CPU doesn't fire instantly, providing a better user experience.
     */
    private void startAiTurn() {
        aiTimer = new Timer(1000, e -> {
            Cell aiMove = model.getAiPlayer().chooseMove(model);
            MoveResult aiRes = model.gameMove(model.getAiPlayer(), aiMove);
            
            processMoveResult(false, aiMove.getX(), aiMove.getY(), aiRes);

            if (model.isGameOver()) {
                finishGame();
            } else if (aiRes != MoveResult.MISS) {
                view.setStatus("La CPU ha colpito! Mira ancora...");
                aiTimer.restart(); // CPU gets another turn if it hits
            } else {
                view.setStatus("La CPU ha mancato. Tocca a te!");
                view.enableInteraction();
            }
        });
        aiTimer.setRepeats(false);
        aiTimer.start();
    }

    /**
     * Core UI update logic. Translates Model outcomes (MoveResult) into 
     * visual feedback (colors, symbols, and status messages).
     */
    private void processMoveResult(boolean isEnemyGrid, int x, int y, MoveResult res) {
        // 1. Determine color and symbol based on the outcome of the shot
        Color color;
        String symbol;

        if (res == MoveResult.MISS) {
            color = new Color(174, 214, 241); // Light blue for water
            symbol = "O";
            view.setStatus(isEnemyGrid ? "Hai mancato! Tocca al nemico." : "Il nemico ha mancato!");
        } else {
            color = Color.RED; // Red for hit
            symbol = "X";
            view.setStatus(isEnemyGrid ? "Colpita! Ottimo colpo." : "Sei stato colpito!");
        }

        // 2. Update the specific cell in the View
        view.updateCell(isEnemyGrid, x, y, color, symbol);

        // --- SMART LOGIC: Sunk Ship Management ---
        if (res == MoveResult.SUNK) {
            view.setStatus(isEnemyGrid ? "COLPITA E AFFONDATA! Grande comandante!" : "Attenzione: flotta danneggiata!");

            // Recover the grid of the player who suffered the blow
            Grid targetGrid = isEnemyGrid ? model.getAiPlayer().getGrid() : model.getHumanPlayer().getGrid();
            
            // Highlight the sunken ship visually
            Optional<Ship> sunkShip = targetGrid.getShipAt(x, y);
            if (sunkShip != null) {
                view.renderSunkenShip(isEnemyGrid, convertToPoints(sunkShip.get().getCells()));
            }

            // SMART SCAN: Automatically disable "Buffer cells" around the sunken ship.
            // Since ships cannot be adjacent, these cells are guaranteed to be empty.
            int w = model.getConfig().getWidth();
            int h = model.getConfig().getHeight();

            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    // If the cell is NO longer a potential target (because it is close to a sunken ship) 
                	// and has NOT yet been hit (it is still in the NOTFIRED state)
                    if (!targetGrid.isPotentialTarget(i, j) && targetGrid.getCellState(i, j) == CellState.NOTFIRED) {
                        view.disableSmartCell(isEnemyGrid, i, j);
                    }
                }
            }
        }

        // 4. Force graphical window update
        view.refreshView();

        // 5. End-of-match control
        if (model.isGameOver()) {
            finishGame();
        }
    }

    // --- UTILITY ---

    private void resetHumanPlacement() {
        if (isBattlePhase) return;
        currentShipIndex = 0;
        model.getHumanPlayer().getGrid().reset();
        view.resetGrids();
        updatePlacementStatus();
    }

    private void returnToMenu() {
        if (aiTimer != null) {
        	aiTimer.stop();
        }
        view.dispose();
        if (exitAction != null) {
        	exitAction.run();
        }
    }

    private void updatePlacementStatus() {
        ShipConfig next = shipsToPlace.get(currentShipIndex);
        view.setStatus("Piazza " + next.getName() + " (Lg: " + next.getSize() + ")");
    }

    private void finishGame() {
        if (aiTimer != null) {
        	aiTimer.stop();
        }
        view.showResults(model.getWinner().getName());
    }
    
    
    private List<Point> convertToPoints(List<Cell> cells){
		List<Point> points = new ArrayList<>();
		for(Cell c : cells) {
			points.add(new Point(c.getX(), c.getY()));
		}
    	return points;
    }
    
    
    
    public void launchGame() {
    	// Launch the application by displaying the difficulty selection menu
        showDifficultyMenu();
    }
    
    /**
     * Initializes and displays the Start Menu.
     * This method is wrapped in SwingUtilities.invokeLater to ensure 
     * Thread-Safety within the Event Dispatch Thread (EDT).
     */
    public void showDifficultyMenu() {
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
    private void launchGame(StartView startScreen, String difficulty) {
        
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
        this.model = new GameState(human, ai, config);
        this.view = new BattleView(10, 10);

        // 6. Callback Definition: Define the action to be performed when exiting the game 
        // (returning to the main menu).
        this.exitAction = () -> showDifficultyMenu();

        this.humanPlacer = new ManualShipPlacer(model.getConfig());
        this.shipsToPlace = model.getConfig().getShipTypes();

        // Functional Listeners for UI buttons using Lambda expressions
        this.view.setResetPlacementListener(e -> resetHumanPlacement());
        this.view.setMenuListener(e -> returnToMenu());
        this.view.setRestartListener(e -> returnToMenu());
        
        initGridListeners();
        updatePlacementStatus();
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