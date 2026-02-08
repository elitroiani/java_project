package ai;

import java.util.List;

import model.GameConfig;
import model.Grid;
import model.Ship;

public interface ShipPlacer {

    /**
     * Posiziona le navi secondo la strategia scelta.
     * 
     * @param config configurazione della partita
     * @return lista delle navi posizionate
     */
    List<Ship> placeShips(GameConfig config);

	/**
	 * Template method per piazzare tutte le navi
	 * @param grid griglia in cui piazzare
	 * @return lista di navi piazzate
	 */
	List<Ship> placeShips(Grid grid);
}