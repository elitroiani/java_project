package ai;

import java.util.ArrayList;
import java.util.List;

import model.GameConfig;
import model.GameState;
import model.Grid;
import model.Ship;
import model.ShipConfig;
import player.Player;

/**
 * Classe astratta per piazzare le navi di un giocatore
 * Implementa la logica di piazzamento generale e lascia
 * alle sottoclassi la decisione di startX, startY e orientamento
 */
public abstract class AbstractShipPlacer implements ShipPlacer {

    protected GameConfig config;

    public AbstractShipPlacer(GameConfig config) {
        this.config = config;
    }

    /**
     * Template method per piazzare tutte le navi di un giocatore
     * @param gameState stato corrente della partita
     * @param player giocatore su cui piazzare le navi
     * @return lista di navi piazzate
     */
    @Override
    public List<Ship> placeShips(GameState gameState, Player player) {
        List<Ship> placedShips = new ArrayList<>();
        Grid grid = player.getGrid(); // ottieni la griglia del giocatore

        for (ShipConfig shipConfig : config.getShipTypes()) {
            String type = shipConfig.getName();
            int size = shipConfig.getSize();
            int count = shipConfig.getCount();

            for (int i = 0; i < count; i++) {
                boolean placed = false;
                int attempts = 0;

                while (!placed && attempts < 100) {
                    Ship ship = new Ship(shipConfig);

                    int startX = getStartX(gameState, player, ship);
                    int startY = getStartY(gameState, player, ship);
                    boolean horizontal = isHorizontal(gameState, player, ship);

                    if (grid.placeShip(ship, startX, startY, horizontal)) {
                        placedShips.add(ship);
                        placed = true;
                    }

                    attempts++;
                }

                if (!placed) {
                    throw new IllegalStateException("Impossibile piazzare la nave " + type + " per il giocatore " + player.getName());
                }
            }
        }

        return placedShips;
    }

    // --- METODI ASTRATTI CHE LE SOTTOCLASSI DEVONO IMPLEMENTARE ---

    @Override
    public abstract int getStartX(GameState gameState, Player player, Ship ship);

    @Override
    public abstract int getStartY(GameState gameState, Player player, Ship ship);

    @Override
    public abstract boolean isHorizontal(GameState gameState, Player player, Ship ship);
}
