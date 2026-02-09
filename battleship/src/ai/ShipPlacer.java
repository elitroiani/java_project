package ai;

import java.util.List;

import model.GameState;
import model.Ship;
import player.Player;


/**
 * Interfaccia per piazzare le navi di un giocatore sulla sua griglia
 * Fornisce sia il metodo principale per piazzare tutte le navi,
 * sia i metodi per decidere posizione e orientamento delle singole navi.
 */
public interface ShipPlacer {

    /**
     * Piazza tutte le navi definite nel GameConfig per un giocatore
     * @param gameState stato corrente della partita
     * @param player giocatore su cui piazzare le navi
     * @return lista di navi piazzate
     */
    List<Ship> placeShips(GameState gameState, Player player);

    /**
     * Restituisce la coordinata X di partenza per la nave
     */
    int getStartX(GameState gameState, Player player, Ship ship);

    /**
     * Restituisce la coordinata Y di partenza per la nave
     */
    int getStartY(GameState gameState, Player player, Ship ship);

    /**
     * Decide se la nave sarà orizzontale o verticale
     */
    boolean isHorizontal(GameState gameState, Player player, Ship ship);
}