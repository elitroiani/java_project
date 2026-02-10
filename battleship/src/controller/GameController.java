package controller;

import java.awt.Point;
import java.util.Scanner;

import ai.EasyReasoner;
import ai.ExpertReasoner;
import ai.HardReasoner;
import ai.MediumReasoner;
import model.Cell;
import model.CellState;
import model.GameState;
import model.Grid;
import model.MoveResult;
import placer.AbstractAutomaticShipPlacer;
import placer.HardShipPlacer;
import placer.ManualShipPlacer;
import placer.RandomShipPlacer;
import player.AIPlayer;
import player.HumanPlayer;
import player.Player;


public class GameController {

    private final GameState gameState;
    private Player currentPlayer;
    private final Scanner scanner;

    public GameController(GameState gameState, Player startingPlayer, Scanner scanner) {
        this.gameState = gameState;
        this.currentPlayer = startingPlayer;
        this.scanner = scanner;
    }

    // --- AVVIO PARTITA ---
    public void startGame(String aiDifficulty) {
        System.out.println("=== BATTAGLIA NAVALE ===");

        Player humanPlayer = gameState.getHumanPlayer();
        AIPlayer aiPlayer = (AIPlayer) gameState.getAiPlayer();

        // --- PIAZZAMENTO NAVI UMANO ---
        ManualShipPlacer manualPlacer = new ManualShipPlacer(gameState.getConfig());
        manualPlacer.placeAllShips(gameState, humanPlayer); // GUI gestisce i click

        // impostazione AI in base alla difficoltà scelta
        AbstractAutomaticShipPlacer aiPlacer;
        switch (aiDifficulty.toLowerCase()) {
            case "easy" -> {
                aiPlacer = new RandomShipPlacer(gameState.getConfig());
                aiPlayer.setReasoner(new EasyReasoner(humanPlayer, null, gameState));
            }
            case "medium" -> {
                aiPlacer = new HardShipPlacer(gameState.getConfig());
                aiPlayer.setReasoner(new MediumReasoner(humanPlayer, null));
            }
            case "hard" -> {
                aiPlacer = new HardShipPlacer(gameState.getConfig());
                aiPlayer.setReasoner(new HardReasoner(humanPlayer, null));
            }
            case "expert" -> {
                aiPlacer = new HardShipPlacer(gameState.getConfig());
                aiPlayer.setReasoner(new ExpertReasoner(humanPlayer, null));
            }
            default -> {
                aiPlacer = new RandomShipPlacer(gameState.getConfig());
                aiPlayer.setReasoner(new EasyReasoner(humanPlayer, null, gameState));
            }
        }
        aiPlacer.placeAllShips(gameState, aiPlayer);

        // --- CICLO GIOCO ---
        while (!gameState.isGameOver()) {
            playTurn();
            switchTurn();
        }

        System.out.println("\nVincitore: " + gameState.getWinner().getName());
    }

    // --- TURNO ---
    private void playTurn() {
        System.out.println("\nTurno di: " + currentPlayer.getName());

        printOwnGrid(currentPlayer.getGrid());
        printEnemyGrid(gameState.getEnemyGrid(currentPlayer));

        Point move;
        boolean validMove = false;

        do {
            if (currentPlayer instanceof HumanPlayer) {
                move = handleHumanMove();
            } else {
                move = currentPlayer.chooseMove(gameState);
                System.out.println("AI sceglie: (" + move.x + "," + move.y + ")");
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

    // --- INPUT UMANO ---
    private Point handleHumanMove() {
        int x = readInt("Inserisci X (0-" + (gameState.getEnemyGrid(currentPlayer).getWidth() - 1) + "): ");
        int y = readInt("Inserisci Y (0-" + (gameState.getEnemyGrid(currentPlayer).getHeight() - 1) + "): ");
        return new Point(x, y);
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) return scanner.nextInt();
            scanner.next(); 										// scarta input sbagliato
            System.out.println("Inserisci un numero valido");
        }
    }

    // --- CAMBIO TURNO ---
    private void switchTurn() {
        currentPlayer = gameState.getOpponent(currentPlayer);
    }

    // --- STAMPA RISULTATI ---
    private void printMoveResult(MoveResult result, Point move) {
        switch (result) {
            case MISS -> System.out.println("Acqua in (" + move.x + "," + move.y + ")");
            case HIT -> System.out.println("Colpito!");
            case SUNK -> System.out.println("Nave affondata!");
            case ALREADY_FIRED -> System.out.println("Cella già colpita!"); // opzionale
        }
    }

    // --- STAMPA GRIGLIE ---
    private void printEnemyGrid(Grid grid) {
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell c = grid.getCell(x, y);
                System.out.print(c.toSymbol() + " ");
            }
            System.out.println();
        }
    }

    private void printOwnGrid(Grid grid) {
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell c = grid.getCell(x, y);
                if (c.hasShip()) {
                    System.out.print(c.getState() == CellState.HIT ? "X " : "S ");
                } else {
                    System.out.print(c.toSymbol() + " ");
                }
            }
            System.out.println();
        }
    }

    private void printShipsRemaining() {
        System.out.println("\nNavi nemiche rimanenti: " + gameState.enemyShipsRemaining(currentPlayer).size());
    }
}





/*
public class GameController {

    private final GameState gameState;
    private Player currentPlayer;
    private final Scanner scanner;   //classe java che serve a leggere input

    public GameController(GameState gameState, Player startingPlayer, Scanner scanner) {
        this.gameState = gameState;
        this.currentPlayer = startingPlayer;
        this.scanner = scanner; // Scanner fornito esternamente per riutilizzo
    }

    // --- AVVIO PARTITA ---
    public void startGame() {
        System.out.println("=== BATTAGLIA NAVALE ===");

        while (!gameState.isGameOver()) {
            playTurn();
            switchTurn();
        }

        Player winner = gameState.getWinner();
        System.out.println("\n Vincitore: " + winner.getName());
        //scanner.close();  DA CAPIRE SE VA O MENO, in teoria no
    }

    // --- TURNO ---
    private void playTurn() {
    	System.out.println("\nTurno di: " + currentPlayer.getName());

    	// Stampa griglie
        System.out.println("\nLa tua griglia:");
        printOwnGrid(currentPlayer.getGrid());

        System.out.println("\nGriglia nemica:");
        printEnemyGrid(gameState.getEnemyGrid(currentPlayer));

        Point move;
        boolean validMove = false;

        do {
            if (currentPlayer instanceof HumanPlayer) {
                move = handleHumanMove();
            } else {
                move = handleAIMove();
            }

            try {
                MoveResult result = gameState.gameMove(currentPlayer, move);
                printMoveResult(result, move);
                printShipsRemaining();
                validMove = true;
            } catch (IllegalStateException e) {
                System.out.println("Mossa non valida (cella già colpita), riprova");
            }

        } while (!validMove);
    
    	
    	/* 	VERSIONE VECCHIA
    	 * System.out.println("\n Turno di: " + currentPlayer.getName());

        Point move;

        if (currentPlayer instanceof HumanPlayer) {
            move = handleHumanMove();
        } else {
            move = handleAIMove();
        }

        try {
            MoveResult result = gameState.gameMove(currentPlayer, move);
            printMoveResult(result, move);
        } catch (IllegalStateException e) {
            System.out.println("Mossa non valida, riprova");
        }
    }*/
/*


	// --- MOSSA UMANO ---
    private Point handleHumanMove() {
        int x = readInt("Inserisci X (0-" + (gameState.getEnemyGrid(currentPlayer).getWidth() - 1) + "): ");
        int y = readInt("Inserisci Y (0-" + (gameState.getEnemyGrid(currentPlayer).getHeight() - 1) + "): ");
        return new Point(x, y);
    }
    
 // --- INPUT ROBUSTO ---
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            } else {
                scanner.next(); // scarta input sbagliato
                System.out.println("Inserisci un numero valido");
            }
        }
    }
    
    
    // --- MOSSA AI ---
    private Point handleAIMove() {
        Point move = currentPlayer.chooseMove(gameState);
        System.out.println("AI sceglie: (" + move.x + ", " + move.y + ")");
        return move;
    }

    // --- CAMBIO TURNO ---
    private void switchTurn() {
        currentPlayer = gameState.getOpponent(currentPlayer);
    }

    // --- OUTPUT ---
    private void printMoveResult(MoveResult result, Point move) {
        switch (result) {
            case MISS -> System.out.println("Acqua in (" + move.x + "," + move.y + ")");
            case HIT_SHIP -> System.out.println("Colpito!");
            case SUNK -> System.out.println("Nave affondata!");
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
    
 // --- STAMPA GRIGLIA PROPRIA (mostra anche le navi) ---
    private void printOwnGrid(Grid grid) {
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell c = grid.getCell(x, y);
                if (c.hasShip()) {
                    if (c.getState() == CellState.HIT) {
                        System.out.print("X "); 			// nave colpita
                    } else {
                        System.out.print("S "); 			// nave presente
                    }
                } else {
                    System.out.print(c.toSymbol() + " "); 	// acqua o colpo a vuoto
                }
            }
            System.out.println();
        }
    }
    
 // --- STAMPA NAVI RIMANENTI ---
    private void printShipsRemaining() {
        System.out.println("\nNavi nemiche rimanenti: " + gameState.enemyShipsRemaining(currentPlayer).size());
    }

}
*/
