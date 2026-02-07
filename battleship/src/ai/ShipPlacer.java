package ai;

import java.util.List;

import model.GameConfig;
import model.Ship;

public interface ShipPlacer {

    /**
     * Posiziona le navi secondo la strategia scelta.
     * 
     * @param config configurazione della partita
     * @return lista delle navi posizionate
     */
    List<Ship> placeShips(GameConfig config);
}