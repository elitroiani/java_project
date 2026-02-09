package ai;

import java.util.List;
import java.util.Scanner;

import model.GameConfig;
import model.GameState;
import model.Ship;
import player.Player;

/**
 * ShipPlacer manuale
 * L'utente sceglie le coordinate e l'orientamento delle navi
 */
public class ManualShipPlacer extends AbstractShipPlacer {

    private final Scanner scanner = new Scanner(System.in);

    public ManualShipPlacer(GameConfig config) {
        super(config);
    }

    @Override
    public int getStartX(GameState gameState, Player player, Ship ship) {
        System.out.println(player.getName() + ", inserisci X per " + ship.getConfig().getName() + " (size=" + ship.getSize() + "): ");
        return readCoordinate(gameState.getOpponent(player).getGrid().getWidth());
    }

    @Override
    public int getStartY(GameState gameState, Player player, Ship ship) {
        System.out.println(player.getName() + ", inserisci Y per " + ship.getConfig().getName() + " (size=" + ship.getSize() + "): ");
        return readCoordinate(gameState.getOpponent(player).getGrid().getHeight());
    }

    @Override
    public boolean isHorizontal(GameState gameState, Player player, Ship ship) {
        System.out.println(player.getName() + ", la nave sarà orizzontale? (true/false): ");
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true")) {
            	return true;
            }
            if (input.equals("false")) {
            	return false;
            }
            System.out.println("Risposta non valida. Inserisci true o false: ");
        }
    }

    // --- metodo helper per leggere coordinate valide ---
    private int readCoordinate(int max) {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= 0 && value < max) return value;
            } catch (NumberFormatException ignored) {}
            System.out.println("Coordinata non valida. Inserisci un numero tra 0 e " + (max - 1) + ": ");
        }
    }
}
