package ai;

import java.util.ArrayList;
import java.util.List;

import model.GameConfig;
import model.Grid;
import model.Ship;
import model.ShipConfig;

/**
 * Classe astratta che gestisce il piazzamento delle navi
 * Definisce il template method "placeShips" e lascia a sottoclassi
 * la decisione di posizione e orientamento per ogni nave.
 */
public abstract class AbstractShipPlacer implements ShipPlacer {

    protected GameConfig config;

    public AbstractShipPlacer(GameConfig config) {
        this.config = config;
    }

    /**
     * Template method per piazzare tutte le navi definite in GameConfig
     * @param grid griglia su cui piazzare le navi
     * @return lista di navi piazzate
     */
    public List<Ship> placeShips(Grid grid) {
        List<Ship> placedShips = new ArrayList<>();

        // Cicla su tutti i tipi di nave definiti nel GameConfig
        for (ShipConfig shipConfig : config.getShipTypes()) {
            String type = shipConfig.getName();
            int size = shipConfig.getSize();
            int count = shipConfig.getCount(); // <--- usa "count" ora

            for (int i = 0; i < count; i++) {
                boolean placed = false;
                int attempts = 0;

                while (!placed && attempts < 100) { // evita loop infinito
                    Ship ship = new Ship(shipConfig); // crea la nave

                    // Sottoclasse decide startX, startY e orientamento
                    int startX = getStartX(grid, ship);
                    int startY = getStartY(grid, ship);
                    boolean horizontal = isHorizontal(grid, ship);

                    // prova a piazzare la nave sulla griglia
                    if (grid.placeShip(ship, startX, startY, horizontal)) {
                        placedShips.add(ship);
                        placed = true;
                    }

                    attempts++;
                }

                if (!placed) {
                    throw new IllegalStateException("Impossibile piazzare la nave " + type);
                }
            }
        }

        return placedShips;
    }

    // --- METODI ASTRATTI PER LE SOTTOCLASSI ---

    /**
     * Restituisce la coordinata X di partenza per la nave
     */
    protected abstract int getStartX(Grid grid, Ship ship);

    /**
     * Restituisce la coordinata Y di partenza per la nave
     */
    protected abstract int getStartY(Grid grid, Ship ship);

    /**
     * Decide se la nave sarà orizzontale o verticale
     */
    protected abstract boolean isHorizontal(Grid grid, Ship ship);
}
