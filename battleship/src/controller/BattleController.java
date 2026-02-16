package controller;

import model.*;
import view.BattleView;
import placer.*;
import player.*;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import java.util.Optional;

import javax.swing.Timer;

/**
 * The BattleController class acts as the Mediator between the Game Model and the BattleView.
 * It manages the game lifecycle, transitioning from the ship placement phase to the 
 * tactical battle phase, while coordinating Human and AI turns.
 */
public class BattleController {
    private final GameState model;
    private final BattleView view;
    private final Runnable exitAction;				// Callback to return to the main menu
    
    private final ManualShipPlacer humanPlacer;
    private final List<ShipConfig> shipsToPlace;
    
    private int currentShipIndex = 0;
    private boolean isBattlePhase = false;
    private Timer aiTimer;							// Timer to simulate CPU "thinking" time and prevent UI freezing

    /**
     * Constructor initializes the controller, sets up listeners, and prepares 
     * the initial placement state.
     */
    public BattleController(GameState model, BattleView view, Runnable exitAction) {
        this.model = model;
        this.view = view;
        this.exitAction = exitAction;
        
        this.humanPlacer = new ManualShipPlacer(model.getConfig());
        this.shipsToPlace = model.getConfig().getShipTypes();

        // Functional Listeners for UI buttons using Lambda expressions
        this.view.setResetPlacementListener(e -> resetHumanPlacement());
        this.view.setMenuListener(e -> returnToMenu());
        this.view.setRestartListener(e -> returnToMenu());
        
        initGridListeners();
        updatePlacementStatus();
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
        MoveResult res = model.gameMove(model.getHumanPlayer(), new Point(x, y));
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
            Point aiMove = model.getAiPlayer().chooseMove(model);
            MoveResult aiRes = model.gameMove(model.getAiPlayer(), aiMove);
            
            processMoveResult(false, aiMove.x, aiMove.y, aiRes);

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
                view.renderSunkenShip(isEnemyGrid, sunkShip.get().getPositions());
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
}