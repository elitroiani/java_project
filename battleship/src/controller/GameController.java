package controller;

import java.awt.Point;
import java.util.Scanner;

import ai.EasyReasoner;
import ai.ExpertReasoner;
import ai.HardReasoner;
import ai.MediumReasoner;
import model.*;
import placer.*;
import player.*;

public class GameController {

    private final GameState gameState;
    private Player currentPlayer;
    private final Scanner scanner;

    public GameController(GameState gameState, Player startingPlayer, Scanner scanner) {
        this.gameState = gameState;
        this.currentPlayer = startingPlayer;
        this.scanner = scanner;
    }

    // ============================
    // AVVIO PARTITA
    // ============================
    public void startGame(String aiDifficulty) {

        System.out.println("=== BATTAGLIA NAVALE ===");

        setupHumanShips();
        setupAI(aiDifficulty);
        gameLoop();

        System.out.println("\nVincitore: " + gameState.getWinner().getName());
    }

    // ============================
    // SETUP NAVI UMANO
    // ============================
    private void setupHumanShips() {

        HumanPlayer human = (HumanPlayer) gameState.getHumanPlayer();
        ManualShipPlacer manualPlacer = new ManualShipPlacer(gameState.getConfig());

        for (ShipConfig config : gameState.getConfig().getShipTypes()) {
            for (int i = 0; i < config.getCount(); i++) {

                boolean placed = false;

                while (!placed) {

                    System.out.println("\nDevi piazzare: " + config.getName() +
                            " (dimensione " + config.getSize() + ")");

                    printOwnGrid(human.getGrid());

                    int x = readInt("Inserisci X: ");
                    int y = readInt("Inserisci Y: ");
                    boolean horizontal = readOrientation();

                    Ship ship = new Ship(config);
                    placed = manualPlacer.placeShip(gameState, human, ship, x, y, horizontal);

                    if (!placed) {
                        System.out.println("Posizione non valida, riprova.");
                    }
                }
            }
        }

        System.out.println("\nTutte le navi piazzate!");
    }

    // ============================
    // SETUP AI
    // ============================
    private void setupAI(String aiDifficulty) {

        HumanPlayer human = (HumanPlayer) gameState.getHumanPlayer();
        AIPlayer ai = (AIPlayer) gameState.getAiPlayer();

        AbstractAutomaticShipPlacer aiPlacer;

        switch (aiDifficulty.toLowerCase()) {
            case "easy" -> {
                aiPlacer = new RandomShipPlacer(gameState.getConfig());
                ai.setReasoner(new EasyReasoner(human, gameState.getConfig()));
            }
            case "medium" -> {
                aiPlacer = new HardShipPlacer(gameState.getConfig());
                ai.setReasoner(new MediumReasoner(human, gameState.getConfig()));
            }
            case "hard" -> {
                aiPlacer = new HardShipPlacer(gameState.getConfig());
                ai.setReasoner(new HardReasoner(human, gameState.getConfig()));
            }
            case "expert" -> {
                aiPlacer = new HardShipPlacer(gameState.getConfig());
                ai.setReasoner(new ExpertReasoner(human, gameState.getConfig()));
            }
            default -> {
                aiPlacer = new RandomShipPlacer(gameState.getConfig());
                ai.setReasoner(new EasyReasoner(human, gameState.getConfig()));
            }
        }

        aiPlacer.placeAllShips(gameState, ai);
    }

    // ============================
    // LOOP PARTITA
    // ============================
    private void gameLoop() {
        while (!gameState.isGameOver()) {
            playTurn();
            switchTurn();
        }
    }

    // ============================
    // TURNO
    // ============================
    private void playTurn() {

        System.out.println("\nTurno di: " + currentPlayer.getName());

        printOwnGrid(currentPlayer.getGrid());
        printEnemyGrid(gameState.getEnemyGrid(currentPlayer));

        Point move;
        boolean validMove = false;

        do {
            try {
                move = currentPlayer.chooseMove(gameState);
            } catch (UnsupportedOperationException e) {
                move = handleHumanMove();
            }

            MoveResult result = gameState.gameMove(currentPlayer, move);

            switch (result) {
                case ALREADY_FIRED -> System.out.println("Cella già colpita! Riprova.");
                default -> {
                    printMoveResult(result, move);
                    printShipsRemaining();
                    validMove = true;
                }
            }

        } while (!validMove);
    }

    // ============================
    // INPUT UMANO
    // ============================
    private Point handleHumanMove() {
        int x = readInt("Inserisci X (0-" +
                (gameState.getEnemyGrid(currentPlayer).getWidth() - 1) + "): ");
        int y = readInt("Inserisci Y (0-" +
                (gameState.getEnemyGrid(currentPlayer).getHeight() - 1) + "): ");
        return new Point(x, y);
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine(); // pulizia buffer
                return value;
            }
            scanner.next(); // scarta input errato
            System.out.println("Inserisci un numero valido");
        }
    }

    private boolean readOrientation() {
        while (true) {
            System.out.print("Orizzontale? (y/n): ");
            String input = scanner.next().toLowerCase();
            scanner.nextLine();

            if (input.equals("y")) return true;
            if (input.equals("n")) return false;

            System.out.println("Inserisci 'y' o 'n'");
        }
    }

    // ============================
    // CAMBIO TURNO
    // ============================
    private void switchTurn() {
        currentPlayer = gameState.getOpponent(currentPlayer);
    }

    // ============================
    // OUTPUT
    // ============================
    private void printMoveResult(MoveResult result, Point move) {
        switch (result) {
            case MISS -> System.out.println("Acqua in (" + move.x + "," + move.y + ")");
            case HIT -> System.out.println("Colpito!");
            case SUNK -> System.out.println("Nave affondata!");
            case ALREADY_FIRED -> System.out.println("Cella già colpita!");
        }
    }

    private void printEnemyGrid(Grid grid) {
        System.out.println("\nGriglia nemica:");
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell c = grid.getCell(x, y);
                System.out.print(c.toSymbol() + " ");
            }
            System.out.println();
        }
    }

    private void printOwnGrid(Grid grid) {
        System.out.println("\nLa tua griglia:");
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell c = grid.getCell(x, y);
                if (c.hasShip()) {
                    System.out.print(
                            c.getState() == CellState.HIT ? "X " : "S "
                    );
                } else {
                    System.out.print(c.toSymbol() + " ");
                }
            }
            System.out.println();
        }
    }

    private void printShipsRemaining() {
        System.out.println("\nNavi nemiche rimanenti: "
                + gameState.enemyShipsRemaining(currentPlayer).size());
    }
}