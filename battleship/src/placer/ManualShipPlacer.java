package placer;

import java.util.List;

import model.GameConfig;
import model.GameState;
import model.Ship;
import model.ShipConfig;
import player.Player;

/**
 * Piazzamento manuale delle navi tramite click dell'utente.
 * 
 * - Tiene GameConfig per conoscere quali navi piazzare e quante
 * - Piazza una nave alla volta con placeShip
 * - placeAllShips non serve: il controller gestisce il flusso dell'utente
 */
public class ManualShipPlacer {

    private final GameConfig config;

    public ManualShipPlacer(GameConfig config) {
        this.config = config;
    }

    /**
     * Piazza una singola nave sulla griglia del giocatore.
     * 
     * @param gameState stato corrente del gioco
     * @param player giocatore per cui piazzare la nave
     * @param ship nave da piazzare (tipo e dimensione già definite)
     * @param x coordinata X selezionata dall'utente
     * @param y coordinata Y selezionata dall'utente
     * @param horizontal orientamento scelto dall'utente
     * @return true se piazzata correttamente, false altrimenti
     */
    public boolean placeShip(GameState gameState, Player player, Ship ship, int x, int y, boolean horizontal) {
        return player.getGrid().placeShip(ship, x, y, horizontal);
    }

    /**
     * Restituisce le navi da piazzare secondo GameConfig.
     * Utile per il controller che gestisce i click dell'utente.
     */
    public List<ShipConfig> getShipsToPlace() {
        return config.getShipTypes();
    }

    /**
     * placeAllShips non ha senso per il piazzamento manuale,
     * perché l'utente decide manualmente ogni nave.
     */
}