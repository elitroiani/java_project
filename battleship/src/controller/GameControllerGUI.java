package controller;

import model.*;
import placer.*;
import player.*;
import ai.*;
import view.GameView;

import java.awt.*;

public class GameControllerGUI {

    private final GameState gameState;
    private final GameView view;

    private final ManualShipPlacer manualPlacer;

    private int currentShipTypeIndex = 0;
    private int shipsPlacedOfCurrentType = 0;

    private boolean placingPhase = true;

    public GameControllerGUI(GameState state) {

        this.gameState = state;
        this.manualPlacer = new ManualShipPlacer(state.getConfig());

        this.view = new GameView(this, state);
    }

    // ============================
    // AVVIO
    // ============================
    public void startGame(String difficulty) {

        setupAI(difficulty);
        view.refresh();
    }

    // ============================
    // CLICK GRIGLIA PLAYER (piazzamento)
    // ============================
    public void handlePlayerGridClick(int x, int y) {

        if (!placingPhase) return;

        HumanPlayer human = (HumanPlayer) gameState.getHumanPlayer();
        ShipConfig config = gameState.getConfig()
                .getShipTypes().get(currentShipTypeIndex);

        Ship ship = new Ship(config);

        boolean placed = manualPlacer.placeShip(
                gameState, human, ship, x, y, true);

        if (!placed) {
            view.setInfo("Posizione non valida!");
            return;
        }

        shipsPlacedOfCurrentType++;

        if (shipsPlacedOfCurrentType >= config.getCount()) {
            shipsPlacedOfCurrentType = 0;
            currentShipTypeIndex++;
        }

        if (currentShipTypeIndex >=
                gameState.getConfig().getShipTypes().size()) {

            placingPhase = false;
            view.setInfo("Inizia la battaglia!");
        } else {
            ShipConfig next =
                    gameState.getConfig()
                            .getShipTypes()
                            .get(currentShipTypeIndex);

            view.setInfo("Piazza: " + next.getName());
        }

        view.refresh();
    }

    // ============================
    // CLICK GRIGLIA NEMICA (spari)
    // ============================
    public void handleEnemyGridClick(int x, int y) {

        if (placingPhase) return;

        HumanPlayer human = (HumanPlayer) gameState.getHumanPlayer();

        MoveResult result =
                gameState.gameMove(human, new Point(x, y));

        if (result == MoveResult.ALREADY_FIRED) {
            view.setInfo("Cella già colpita!");
            return;
        }

        if (!gameState.isGameOver()) {
            handleAITurn();
        }

        view.refresh();

        if (gameState.isGameOver()) {
            view.setInfo("Vincitore: "
                    + gameState.getWinner().getName());
        }
    }

    // ============================
    // TURNO AI
    // ============================
    private void handleAITurn() {

        AIPlayer ai = (AIPlayer) gameState.getAiPlayer();
        Point move = ai.chooseMove(gameState);

        gameState.gameMove(ai, move);
    }

    // ============================
    // SETUP AI
    // ============================
    private void setupAI(String difficulty) {

        AIPlayer ai = (AIPlayer) gameState.getAiPlayer();

        AbstractAutomaticShipPlacer aiPlacer;

        switch (difficulty.toLowerCase()) {
            case "easy" -> {
                aiPlacer = new RandomShipPlacer(gameState.getConfig());
                ai.setReasoner(new EasyReasoner(ai, gameState.getConfig()));
            }
            case "medium" -> {
                aiPlacer = new HardShipPlacer(gameState.getConfig());
                ai.setReasoner(new MediumReasoner(ai, gameState.getConfig()));
            }
            case "hard" -> {
                aiPlacer = new HardShipPlacer(gameState.getConfig());
                ai.setReasoner(new HardReasoner(ai, gameState.getConfig()));
            }
            case "expert" -> {
                aiPlacer = new HardShipPlacer(gameState.getConfig());
                ai.setReasoner(new ExpertReasoner(ai, gameState.getConfig()));
            }
            default -> {
                aiPlacer = new RandomShipPlacer(gameState.getConfig());
                ai.setReasoner(new EasyReasoner(ai, gameState.getConfig()));
            }
        }

        aiPlacer.placeAllShips(gameState, ai);
    }
}
