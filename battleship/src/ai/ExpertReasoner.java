package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import model.*;
import player.Player;

public class ExpertReasoner extends AbstractReasoner {
    
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
        updateProbability(state);
        
        double max = -1.0;
        List<Point> candidates = new ArrayList<>();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Spara solo dove non ha mai sparato
                if (grid.getCellState(x, y) != CellState.NOTFIRED) continue;
                
                double value = probabilityGrid[x][y];
                if (value > max) {
                    max = value;
                    candidates.clear();
                    candidates.add(new Point(x, y));
                } else if (value == max && max >= 0) {
                    candidates.add(new Point(x, y));
                }
            }
        }
        
        // Se non ci sono candidati (caso limite), spara a caso tra i rimanenti
        if (candidates.isEmpty()) return getRandomMove(grid);
        
        return candidates.get(random.nextInt(candidates.size()));
    }
    
    private void updateProbability(GameState state) {
        // Reset griglia
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) probabilityGrid[x][y] = 0;
        }
        
        Grid grid = state.getEnemyGrid(player);
        List<Ship> remainingShips = state.enemyShipsRemaining(player);
        
        for (Ship ship : remainingShips) {
            int size = ship.getSize();
            
            // Analisi Orizzontale
            for (int y = 0; y < height; y++) {
                for (int x = 0; x <= width - size; x++) {
                    PlacementResult result = checkPlacement(grid, x, y, size, true);
                    if (result.canPlace) {
                        applyWeight(x, y, size, true, result.hitCount);
                    }
                }
            }
            
            // Analisi Verticale
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

    private void applyWeight(int x, int y, int size, boolean horizontal, int hitCount) {
        // Logica Direzionale: Se hitCount è 2, il peso è molto più alto che se fosse 1.
        // Se hitCount è 0, il peso è 1 (ricerca standard).
        double weight = (hitCount == 0) ? 1.0 : Math.pow(20.0, hitCount);
        
        for (int i = 0; i < size; i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;
            probabilityGrid[cx][cy] += weight;
        }
    }

    private PlacementResult checkPlacement(Grid grid, int x, int y, int size, boolean horizontal) {
        int hitCount = 0;
        
        for (int i = 0; i < size; i++) {
            int cx = horizontal ? x + i : x;
            int cy = horizontal ? y : y + i;
            
            CellState state = grid.getCellState(cx, cy);
            
            // Se c'è acqua, questa configurazione di nave è impossibile
            if (state == CellState.MISS) return new PlacementResult(false, 0);
            
            if (state == CellState.HIT) {
                Ship s = grid.getCell(cx, cy).getShip();
                // Se la nave in quella cella è già affondata, non può essere questa
                if (s != null && s.isSunk()) return new PlacementResult(false, 0);
                
                // Se è un colpo su una nave ancora viva, aumenta il valore della direzione
                hitCount++;
            }
        }
        return new PlacementResult(true, hitCount);
    }

    private Point getRandomMove(Grid grid) {
        List<Point> available = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (grid.getCellState(x, y) == CellState.NOTFIRED) available.add(new Point(x, y));
            }
        }
        return available.get(random.nextInt(available.size()));
    }

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