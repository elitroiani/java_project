package ai;

import java.util.ArrayList;
import java.util.List;

import model.GameConfig;
import model.Grid;
import model.Ship;

public abstract class AbstractShipPlacer implements ShipPlacer {

    protected GameConfig config;
    protected final List<Ship> ships = new ArrayList<>();

    public AbstractShipPlacer(GameConfig config) {
        this.config = config;
    }

    /**
     * Template method per piazzare tutte le navi
     * @param grid griglia in cui piazzare
     * @return lista di navi piazzate
     */

    public List<Ship> placeShips(Grid grid) {
        List<Ship> placedShips = new ArrayList<>();

        // Cicla sui tipi di nave definiti nel GameConfig
        config.getShipTypes().forEach((type, size) -> {
        	
            boolean placed = false;
            
            while (!placed) {
                // Genera una nave (coordinata + orientamento)
                Ship ship = createShip(size, type);
                
                // Se la nave può essere piazzata nella griglia
                if (grid.canPlaceShip(ship)) {
                    // piazza fisicamente la nave
                    grid.placeShip(ship);
                    placedShips.add(ship);
                    placed = true;
                }
                // altrimenti riprova (Random o Manual decidono come generare)
            }
        });
        return placedShips;
    }

    /**
     * Metodo astratto da implementare nelle classi concrete
     * - Decide la posizione e l'orientamento della nave
     */
    protected abstract Ship createShip(int size, String type);
}
