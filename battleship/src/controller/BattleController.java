package controller;

import model.*;
import view.BattleView;
import placer.*;
import player.*;
import java.awt.Point;
import java.awt.Color;
import java.util.List;
import javax.swing.Timer; // Fondamentale usare questo!

public class BattleController {
    private final GameState model;
    private final BattleView view;
    private final ManualShipPlacer humanPlacer;
    private final List<ShipConfig> shipsToPlace;
    
    private int currentShipIndex = 0;
    private boolean isBattlePhase = false;

    public BattleController(GameState model, BattleView view) {
        this.model = model;
        this.view = view;
        this.humanPlacer = new ManualShipPlacer(model.getConfig());
        this.shipsToPlace = model.getConfig().getShipTypes();

        initController();
    }

    private void initController() {
        // 1. Setup IA
        AutomaticShipPlacer aiPlacer = new HardShipPlacer(model.getConfig());
        aiPlacer.placeAllShips(model, model.getAiPlayer());

        // 2. Setup Listener Umano
        int w = model.getConfig().getWidth();
        int h = model.getConfig().getHeight();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                final int x = i; final int y = j;
                view.setPlayerListener(x, y, e -> handlePlacementClick(x, y));
            }
        }
        updatePlacementStatus();
    }

    private void handlePlacementClick(int x, int y) {
        if (isBattlePhase) return;

        ShipConfig config = shipsToPlace.get(currentShipIndex);
        Ship ship = new Ship(config);
        
        if (humanPlacer.placeShip(model, model.getHumanPlayer(), ship, x, y, view.isHorizontal())) {
            renderPlacedShip(ship, x, y, view.isHorizontal());
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

    private void startBattlePhase() {
        isBattlePhase = true;
        view.hideSetup();
        view.setStatus("BATTAGLIA! Fuoco al nemico.");

        int w = model.getConfig().getWidth();
        int h = model.getConfig().getHeight();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                final int x = i; final int y = j;
                view.setEnemyListener(x, y, e -> handleBattleClick(x, y));
            }
        }
    }

    private void handleBattleClick(int x, int y) {
        if (!isBattlePhase || model.isGameOver()) return;
        
        // Controllo se la cella è già stata colpita (gestito dal model)
        MoveResult res = model.gameMove(model.getHumanPlayer(), new Point(x, y));
        
        if (res == MoveResult.ALREADY_FIRED) return; // Non fare nulla

        updateCellView(true, x, y, res);

        if (model.isGameOver()) {
            finishGame();
            return;
        }

        if (res == MoveResult.MISS) {
            view.setStatus("Mancato! Tocca alla CPU...");
            view.disableEnemyGrid(); // Metodo da aggiungere alla tua View
            
            // Ritardo di 1 secondo per realismo
            Timer timer = new Timer(1000, e -> executeAiTurn());
            timer.setRepeats(false);
            timer.start();
        } else {
            view.setStatus("COLPITO! Hai un altro colpo.");
        }
    }

    private void executeAiTurn() {
        if (model.isGameOver()) return;

        Point aiMove = model.getAiPlayer().chooseMove(model);
        MoveResult aiRes = model.gameMove(model.getAiPlayer(), aiMove);
        updateCellView(false, aiMove.x, aiMove.y, aiRes);

        if (model.isGameOver()) {
            finishGame();
            return;
        }

        if (aiRes != MoveResult.MISS) {
            view.setStatus("La CPU ha colpito! Mira ancora...");
            Timer timer = new Timer(1000, e -> executeAiTurn());
            timer.setRepeats(false);
            timer.start();
        } else {
            view.setStatus("La CPU ha mancato. Tocca a te!");
            view.enableEnemyGrid(); // Metodo da aggiungere alla tua View
        }
    }

    // --- Helpers ---

    private void renderPlacedShip(Ship ship, int x, int y, boolean horizontal) {
        for (int i = 0; i < ship.getSize(); i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;
            view.updateCell(false, cx, cy, Color.DARK_GRAY, "");
        }
    }

    private void updateCellView(boolean isEnemyGrid, int x, int y, MoveResult res) {
        Color c = (res == MoveResult.MISS) ? new Color(174, 214, 241) : Color.RED;
        String symbol = (res == MoveResult.MISS) ? "O" : "X";
        view.updateCell(isEnemyGrid, x, y, c, symbol);
        
        if (res == MoveResult.SUNK) view.setStatus("AFFONDATA!");
    }

    private void updatePlacementStatus() {
        ShipConfig next = shipsToPlace.get(currentShipIndex);
        view.setStatus("Piazza " + next.getName() + " (Lg: " + next.getSize() + ")");
    }

    private void finishGame() {
        String winner = model.getWinner().getName();
        view.setStatus("FINE! Vincitore: " + winner);
        view.disableEnemyGrid();
    }
}