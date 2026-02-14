package controller;

import model.*;
import view.BattleView;
import placer.*;
import player.*;
// Importa la tua classe Main qui, es: import main.Main;

import java.awt.Point;
import java.awt.Color;
import java.util.List;
import javax.swing.Timer;

import battleship.Main;

public class BattleController {
    private final GameState model;
    private final BattleView view;
    
    // Questa è la nostra "Callback": un'azione generica da eseguire per uscire
    private final Runnable exitAction;
    
    private final ManualShipPlacer humanPlacer;
    private final List<ShipConfig> shipsToPlace;
    
    private int currentShipIndex = 0;
    private boolean isBattlePhase = false;
    
    // Teniamo un riferimento al timer per poterlo fermare se usciamo
    private Timer aiTimer; 

    public BattleController(GameState model, BattleView view, Runnable exitAction) {
        this.model = model;
        this.view = view;
        this.exitAction = exitAction; // Salviamo l'azione per dopo
        
        this.humanPlacer = new ManualShipPlacer(model.getConfig());
        this.shipsToPlace = model.getConfig().getShipTypes();

        // Tasto "Reset Griglia" (durante il setup)
        this.view.setResetPlacementListener(e -> resetHumanPlacement());
        
        // Tasto "Torna al Menu" (Fine partita o tasto in alto)
        this.view.setRestartListener(e -> returnToMenu());
        // Se hai aggiunto il tasto "Nuova Partita" in alto, usa lo stesso metodo:
        // this.view.setGlobalRestartListener(e -> returnToMenu());
        this.view.setMenuListener(e -> returnToMenu());
        
        initController();
    }

    private void initController() {
        // Assicuriamoci che l'IA parta pulita
        model.getAiPlayer().getGrid().reset();
        
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

    // --- LOGICA DI NAVIGAZIONE ---

    /**
     * Pulisce solo la griglia dell'utente per permettergli di riprovare il posizionamento.
     */
    private void resetHumanPlacement() {
        if (isBattlePhase) return; 
        
        currentShipIndex = 0;
        model.getHumanPlayer().getGrid().reset();
        view.resetGrids();
        updatePlacementStatus();
        view.setStatus("Griglia pulita. Ricomincia il posizionamento.");
    }

    /**
     * Chiude la battaglia e torna al Menu Principale per cambiare difficoltà.
     */
    private void returnToMenu() {
        // 1. IMPORTANTE: Ferma l'IA se sta "pensando"
        if (aiTimer != null && aiTimer.isRunning()) {
            aiTimer.stop();
        }

        // 2. Chiude la finestra di gioco attuale
        

        if (exitAction != null) {
            exitAction.run();
        }
        // 3. Riapre il menu principale
        // ATTENZIONE: Sostituisci 'Main' con il nome della tua classe di avvio
        // e 'showDifficultyMenu' con il tuo metodo statico per mostrare il menu.
        
        // Esempio
        view.dispose();
        System.out.println("Ritorno al menu principale..."); // Log di debug
        
        // Se non hai un metodo statico, puoi istanziare il menu qui:
        // new view.StartView().setVisible(true);
    }

    // --- LOGICA DI GIOCO ---

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
        
        MoveResult res = model.gameMove(model.getHumanPlayer(), new Point(x, y));
        if (res == MoveResult.ALREADY_FIRED) return;

        updateCellView(true, x, y, res);

        if (model.isGameOver()) {
            finishGame();
            return;
        }

        if (res == MoveResult.MISS) {
            view.setStatus("Mancato! Tocca alla CPU...");
            view.disableEnemyGrid();
            
            // Usiamo il campo della classe aiTimer invece di crearne uno locale
            aiTimer = new Timer(1000, e -> executeAiTurn());
            aiTimer.setRepeats(false);
            aiTimer.start();
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
            aiTimer = new Timer(1000, e -> executeAiTurn());
            aiTimer.setRepeats(false);
            aiTimer.start();
        } else {
            view.setStatus("La CPU ha mancato. Tocca a te!");
            view.enableEnemyGrid();
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
        view.showResults(winner);
    }
}