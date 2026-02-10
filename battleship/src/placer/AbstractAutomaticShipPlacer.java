package placer;

import java.util.ArrayList;
import java.util.List;

import model.GameConfig;
import model.GameState;
import model.Ship;
import model.ShipConfig;
import player.Player;

/**
 * Classe base per tutti i placer automatici
 * - Condivide il metodo atomico placeShip
 * - Implementa placeAllShips chiamando placeShip
 */
public abstract class AbstractAutomaticShipPlacer implements AutomaticShipPlacer {

    protected final GameConfig config;

    public AbstractAutomaticShipPlacer(GameConfig config) {
        this.config = config;
    }

    @Override
    public boolean placeShip(GameState gameState, Player player, Ship ship, int x, int y, boolean horizontal) {
        return player.getGrid().placeShip(ship, x, y, horizontal);
    }

    @Override
    public List<Ship> placeAllShips(GameState gameState, Player player) {
        List<Ship> placedShips = new ArrayList<>();

        for (ShipConfig sc : config.getShipTypes()) {
            for (int i = 0; i < sc.getCount(); i++) {
                boolean placed = false;
                int attempts = 0;

                Ship ship = new Ship(sc); // dichiara qui, fuori dal while

                while (!placed && attempts < 100) {
                    int x = getX(gameState, player, ship);
                    int y = getY(gameState, player, ship);
                    boolean horizontal = isHorizontal(gameState, player, ship);

                    placed = placeShip(gameState, player, ship, x, y, horizontal);
                    attempts++;
                }

                if (!placed) {
                    throw new IllegalStateException("Impossibile piazzare nave " + sc.getName());
                }

                placedShips.add(ship); // ora è visibile
            }
        }

        return placedShips;
    }

    // --- Metodi astratti per strategia concreta ---
    protected abstract int getX(GameState gameState, Player player, Ship ship);
    protected abstract int getY(GameState gameState, Player player, Ship ship);
    protected abstract boolean isHorizontal(GameState gameState, Player player, Ship ship);
}
