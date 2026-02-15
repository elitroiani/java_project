package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import model.*;
import player.Player;

/**
 * Expert-level AI that uses a Probability Density Map to determine the best move.
 * It calculates the likelihood of a ship being present in each cell based on 
 * remaining ships and current grid state (Hits and Misses).
 */
public class ExpertReasoner extends AbstractReasoner {
    
    /** Matrix storing the probability score for each cell */
    private final double[][] probabilityGrid;
    private final int width;
    private final int height;
    
    public ExpertReasoner(Player player, GameConfig config) {
        super(player, config);
        this.width = config.getWidth();
        this.height = config.getHeight();
        this.probabilityGrid = new double[width][height];
    }
    
    @Override
    public Point chooseMove(GameState state) {
        Grid grid = state.getEnemyGrid(player);
        
        // Recalculate probabilities for the entire grid based on the current state
        updateProbability(state);
        
        double max = -1.0;
        List<Point> candidates = new ArrayList<>();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // MODIFICA: Utilizziamo isPotentialTarget invece di controllare solo NOTFIRED.
                // Questo esclude automaticamente le diagonali delle navi affondate 
                // e le celle dove non può fisicamente esserci una nave.
                if (!grid.isPotentialTarget(x, y)) continue;
                
                double value = probabilityGrid[x][y];
                // Identify cells with the highest probability score
                if (value > max) {
                    max = value;
                    candidates.clear();
                    candidates.add(new Point(x, y));
                } else if (value == max && max >= 0) {
                    candidates.add(new Point(x, y));
                }
            }
        }
        
        // Fallback to a random valid move if no candidates are found (edge case)
        // Nota: Assicurati che anche getRandomMove usi isPotentialTarget come filtro.
        if (candidates.isEmpty()) return getRandomMove(grid);
        
        // Pick one coordinate randomly among those with the highest probability
        return candidates.get(random.nextInt(candidates.size()));
    }
    
    /**
     * Resets and updates the probability grid by simulating all possible placements 
     * for every remaining enemy ship.
     */
    private void updateProbability(GameState state) {
        // Clear the probability grid for a fresh calculation
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) probabilityGrid[x][y] = 0;
        }
        
        Grid grid = state.getEnemyGrid(player);
        List<Ship> remainingShips = state.enemyShipsRemaining(player);
        
        for (Ship ship : remainingShips) {
            int size = ship.getSize();
            
            // Analyze all possible horizontal placements
            for (int y = 0; y < height; y++) {
                for (int x = 0; x <= width - size; x++) {
                    PlacementResult result = checkPlacement(grid, x, y, size, true);
                    if (result.canPlace) {
                        applyWeight(x, y, size, true, result.hitCount);
                    }
                }
            }
            
            // Analyze all possible vertical placements
            for (int x = 0; x < width; x++) {
                for (int y = 0; y <= height - size; y++) {
                    PlacementResult result = checkPlacement(grid, x, y, size, false);
                    if (result.canPlace) {
                        applyWeight(x, y, size, false, result.hitCount);
                    }
                }
            }
        }
    }

    /**
     * Adds weight to the probability grid for a valid potential ship placement.
     * Hits significantly increase the weight to prioritize sinking identified ships.
     */
    private void applyWeight(int x, int y, int size, boolean horizontal, int hitCount) {
        // If the placement overlaps with existing hits, increase weight exponentially
        // hitCount 0 = weight 1.0 (searching); hitCount > 0 = weight 20^hitCount (targeting)
        double weight = (hitCount == 0) ? 1.0 : Math.pow(20.0, hitCount);
        
        for (int i = 0; i < size; i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;
            probabilityGrid[cx][cy] += weight;
        }
    }

    /**
     * Checks if a ship of a given size can be placed at a specific coordinate.
     * @return A result containing if it's possible and how many existing hits it covers.
     */
    private PlacementResult checkPlacement(Grid grid, int x, int y, int size, boolean horizontal) {
        int hitCount = 0;
        
        for (int i = 0; i < size; i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;
            
            // 1. Controllo base: confini e colpi mancati
            if (!grid.isValidCoordinate(cx, cy)) return new PlacementResult(false, 0);
            CellState state = grid.getCellState(cx, cy);
            if (state == CellState.MISS) return new PlacementResult(false, 0);
            
            // 2. IL FIX PER LE DIAGONALI:
            // Se la cella che stiamo analizzando è adiacente a una nave già affondata,
            // quella posizione è illegale secondo le regole del gioco.
            if (!grid.isAreaClearOfSunkenShips(cx, cy)) {
                return new PlacementResult(false, 0);
            }

            if (state == CellState.HIT) {
                Ship s = grid.getCell(cx, cy).getShip();
                if (s != null && s.isSunk()) return new PlacementResult(false, 0);
                hitCount++;
            }
        }
        return new PlacementResult(true, hitCount);
    }

    /**
     * Simple random picker used as a safety fallback.
     */
    private Point getRandomMove(Grid grid) {
        List<Point> available = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid.getCellState(x, y) == CellState.NOTFIRED) available.add(new Point(x, y));
            }
        }
        return available.get(random.nextInt(available.size()));
    }

    /**
     * Helper class to store the results of a potential placement analysis.
     */
    private static class PlacementResult {
        final boolean canPlace;
        final int hitCount;

        PlacementResult(boolean canPlace, int hitCount) {
            this.canPlace = canPlace;
            this.hitCount = hitCount;
        }
    }


    

    /*	 Flusso Logico Dettagliato
    
    Per ogni cella (cx, cy) nella posizione candidata:

    1. Leggi CellState
       ├─ MISS? → ❌ Blocca placement (return false)
       └─ HIT? → Vai al punto 2

    2. Ottieni Cell e Ship
       cell = grid.getCell(cx, cy)
       ship = cell.getShip()
       
    3. Controlla stato nave
       ├─ ship == null? → ⚠️ Anomalia (HIT senza nave)
       │                   → Ignora e continua
       │
       ├─ ship.isSunk() == true? → ❌ Nave affondata
       │                            → Blocca placement (return false)
       │
       └─ ship.isSunk() == false? → ✅ Nave viva!
                                     → hasActiveHit = true (BOOST 10x)
    ```

    ---

    ##  Esempio di Esecuzione

    ### Griglia Nemica
    ```
        0 1 2 3 4 5
      ┌─────────────┐
    0 │ . . . . . . │
    1 │ . . ✖ . . . │  ← ✖ = Sottomarino (size=1) AFFONDATO
    2 │ . . X . . . │  ← X = Incrociatore (size=3) 1 HIT, VIVO
    3 │ . . . . . . │
      └─────────────┘
    */

}