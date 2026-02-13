package model;

import java.awt.Point;
import java.util.List;
import player.Player;

/**
 * Manages the overall state of the game session.
 * Acts as a container for the players and the configuration, providing 
 * high-level methods to handle turns, victory conditions, and grid access.
 */
public class GameState {

    private final Player humanPlayer;
    private final Player aiPlayer;
    private final GameConfig config;
    
    /**
     * Initializes the game state with the participants and settings.
     * @param humanPlayer The user player.
     * @param aiPlayer The computer-controlled player.
     * @param config The rules and dimensions for this session.
     */
    public GameState(Player humanPlayer, Player aiPlayer, GameConfig config) {
        this.humanPlayer = humanPlayer;
        this.aiPlayer = aiPlayer;
        this.config = config;
    }
    
    /**
     * Identifies the opponent of a specific player.
     * @param player The current player.
     * @return The opposing Player object.
     */
    public Player getOpponent(Player player) {
        return player == this.humanPlayer ? this.aiPlayer : this.humanPlayer;
    }
    
    /**
     * Convenience method to access the grid of the current player's target.
     * @param player The player whose turn it is.
     * @return The opponent's Grid.
     */
    public Grid getEnemyGrid(Player player) {
        return this.getOpponent(player).getGrid();
    }
    
    /**
     * Executes a move on the battlefield.
     * Delegates the shot logic to the cell within the opponent's grid.
     * @param player The player firing the shot.
     * @param point The target coordinates.
     * @return The outcome of the move (MISS, HIT, SUNK, or ALREADY_FIRED).
     */
    public MoveResult gameMove(Player player, Point point) {
        Grid enemyGrid = getEnemyGrid(player);
        Cell cell = enemyGrid.getCell(point.x, point.y);

        // Directly returns the result of the shot processed by the cell
        return cell.fire();
    }
    
    /**
     * Checks if the game has concluded.
     * @return true if all ships of either the human or the AI have been sunk.
     */
    public boolean isGameOver() {
        return this.humanPlayer.getGrid().allShipsSunk()
            || aiPlayer.getGrid().allShipsSunk();
    }

    /**
     * Determines the winner of the match.
     * Note: In a theoretical tie scenario, the current logic favors the AI.
     * @return The winning Player object, or null if the game is still in progress.
     */
    public Player getWinner() {
        if (!isGameOver()) return null;
        if (humanPlayer.getGrid().allShipsSunk()) return aiPlayer;
        if (aiPlayer.getGrid().allShipsSunk()) return humanPlayer;

        return null; 
    }

    /**
     * Provides a list of opponent ships that have not been sunk yet.
     * @param player The current player.
     * @return List of remaining enemy ships.
     */
    public List<Ship> enemyShipsRemaining(Player player){
        return this.getEnemyGrid(player).shipsRemaining();
    }
    
    /**
     * Clears both players' grids to prepare for a new match.
     */
    public void reset() {
        humanPlayer.getGrid().reset();
        aiPlayer.getGrid().reset();
    }
    
    // --- GETTERS ---

    public Player getHumanPlayer() { 
        return humanPlayer; 
    }
    
    public Player getAiPlayer() { 
        return aiPlayer; 
    }
    
    public GameConfig getConfig() { 
        return config; 
    }
}
