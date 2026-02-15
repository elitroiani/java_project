package controller;

import model.*;
import view.BattleView;
import placer.*;
import player.*;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import javax.swing.Timer;

public class BattleController {
    private final GameState model;
    private final BattleView view;
    private final Runnable exitAction;
    
    private final ManualShipPlacer humanPlacer;
    private final List<ShipConfig> shipsToPlace;
    
    private int currentShipIndex = 0;
    private boolean isBattlePhase = false;
    private Timer aiTimer;

    public BattleController(GameState model, BattleView view, Runnable exitAction) {
        this.model = model;
        this.view = view;
        this.exitAction = exitAction;
        
        this.humanPlacer = new ManualShipPlacer(model.getConfig());
        this.shipsToPlace = model.getConfig().getShipTypes();

        // Listener dei tasti funzione
        this.view.setResetPlacementListener(e -> resetHumanPlacement());
        this.view.setMenuListener(e -> returnToMenu());
        this.view.setRestartListener(e -> returnToMenu());
        
        initGridListeners();
        updatePlacementStatus();
    }

    private void initGridListeners() {
        int w = model.getConfig().getWidth();
        int h = model.getConfig().getHeight();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                final int x = i; final int y = j;
                
                // Listener sulla griglia GIOCATORE (per il piazzamento)
                view.setGridListener(false, x, y, e -> handlePlacementClick(x, y));
                
                // Listener sulla griglia NEMICA (per il fuoco)
                view.setGridListener(true, x, y, e -> handleBattleClick(x, y));
            }
        }
    }

    // --- LOGICA PIAZZAMENTO ---

    private void handlePlacementClick(int x, int y) {
        if (isBattlePhase || currentShipIndex >= shipsToPlace.size()) return;

        ShipConfig config = shipsToPlace.get(currentShipIndex);
        Ship ship = new Ship(config);
        boolean horizontal = view.isHorizontal();
        
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

    private void renderPlacedShip(Ship ship, int x, int y, boolean horizontal) {
        for (int i = 0; i < ship.getSize(); i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;
            view.updateCell(false, cx, cy, java.awt.Color.DARK_GRAY, "");
        }
    }

    private void startBattlePhase() {
        isBattlePhase = true;
        view.switchToPlayMode();
        
        // Setup automatico IA (se non fatto prima)
        AutomaticShipPlacer aiPlacer = new HardShipPlacer(model.getConfig());
        aiPlacer.placeAllShips(model, model.getAiPlayer());
        
        view.setStatus("BATTAGLIA! Fuoco al nemico.");
    }

    // --- LOGICA BATTAGLIA ---

    private void handleBattleClick(int x, int y) {
        if (!isBattlePhase || model.isGameOver()) return;
        
        // Verifica mossa tramite model
        MoveResult res = model.gameMove(model.getHumanPlayer(), new Point(x, y));
        if (res == MoveResult.ALREADY_FIRED) return;

        processMoveResult(true, x, y, res);

        if (!model.isGameOver() && res == MoveResult.MISS) {
            view.setStatus("Mancato! Tocca alla CPU...");
            view.disableInteraction();
            startAiTurn();
        }
    }

    private void startAiTurn() {
        aiTimer = new Timer(1000, e -> {
            Point aiMove = model.getAiPlayer().chooseMove(model);
            MoveResult aiRes = model.gameMove(model.getAiPlayer(), aiMove);
            
            processMoveResult(false, aiMove.x, aiMove.y, aiRes);

            if (model.isGameOver()) {
                finishGame();
            } else if (aiRes != MoveResult.MISS) {
                view.setStatus("La CPU ha colpito! Mira ancora...");
                aiTimer.restart(); // L'IA spara di nuovo dopo un secondo
            } else {
                view.setStatus("La CPU ha mancato. Tocca a te!");
                view.enableInteraction();
            }
        });
        aiTimer.setRepeats(false);
        aiTimer.start();
    }

    private void processMoveResult(boolean isEnemyGrid, int x, int y, MoveResult res) {
        // 1. Determina colore e simbolo in base all'esito del colpo
        Color color;
        String symbol;

        if (res == MoveResult.MISS) {
            color = new Color(174, 214, 241); // Azzurro chiaro per l'acqua
            symbol = "O";
            view.setStatus(isEnemyGrid ? "Hai mancato! Tocca al nemico." : "Il nemico ha mancato!");
        } else {
            color = Color.RED; // Rosso per colpita/affondata
            symbol = "X";
            view.setStatus(isEnemyGrid ? "Colpita! Ottimo colpo." : "Sei stato colpito!");
        }

        // 2. Aggiorna la cella specifica nella View
        view.updateCell(isEnemyGrid, x, y, color, symbol);

        // 3. LOGICA SMART: Gestione Nave Affondata
        if (res == MoveResult.SUNK) {
            view.setStatus(isEnemyGrid ? "COLPITA E AFFONDATA! Grande comandante!" : "Attenzione: flotta danneggiata!");

            // Recuperiamo la griglia del giocatore che ha subito il colpo
            Grid targetGrid = isEnemyGrid ? model.getAiPlayer().getGrid() : model.getHumanPlayer().getGrid();
            
            // Troviamo la nave affondata per applicare l'effetto visivo (teschi)
            Ship sunkShip = targetGrid.getShipAt(x, y);
            if (sunkShip != null) {
                view.renderSunkenShip(isEnemyGrid, sunkShip.getPositions());
            }

            // SCANSIONE SMART: Disabilitiamo le celle buffer (inutili da colpire)
            // Usiamo i tuoi nuovi metodi isPotentialTarget e isAreaClearOfSunkenShips
            int w = model.getConfig().getWidth();
            int h = model.getConfig().getHeight();

            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    // Se la cella NON è più un bersaglio potenziale (perché vicina a una nave affondata)
                    // e NON è ancora stata colpita (è ancora nello stato NOTFIRED)
                    if (!targetGrid.isPotentialTarget(i, j) && targetGrid.getCellState(i, j) == CellState.NOTFIRED) {
                        view.disableSmartCell(isEnemyGrid, i, j);
                    }
                }
            }
        }

        // 4. Forza l'aggiornamento grafico della finestra
        view.refreshView();

        // 5. Controllo fine partita
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
        if (aiTimer != null) aiTimer.stop();
        view.dispose();
        if (exitAction != null) exitAction.run();
    }

    private void updatePlacementStatus() {
        ShipConfig next = shipsToPlace.get(currentShipIndex);
        view.setStatus("Piazza " + next.getName() + " (Lg: " + next.getSize() + ")");
    }

    private void finishGame() {
        if (aiTimer != null) aiTimer.stop();
        view.showResults(model.getWinner().getName());
    }
}