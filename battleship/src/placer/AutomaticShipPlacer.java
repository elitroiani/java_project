package placer;

import java.util.List;

import model.GameState;
import model.Ship;
import player.Player;


/**
 * Interfaccia comune per tutte le strategie di piazzamento delle navi
 */
public interface AutomaticShipPlacer {

    /**
     * Piazza una singola nave sulla griglia del giocatore
     * @return true se piazzata con successo, false se posizione non valida
     */
    boolean placeShip(GameState gameState, Player player, Ship ship, int x, int y, boolean horizontal);

    /**
     * Piazza tutte le navi del giocatore
     * - Manual: opzionale, gestito dal controller
     * - Random/AI: itera tutte le navi automaticamente
     */
    List<Ship> placeAllShips(GameState gameState, Player player);
}