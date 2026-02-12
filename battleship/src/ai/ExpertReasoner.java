package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import model.Cell;
import model.CellState;
import model.GameConfig;
import model.GameState;
import model.Grid;
import model.Ship;
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
        
        double max = Double.NEGATIVE_INFINITY;
        List<Point> candidates = new ArrayList<>();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid.getCellState(x, y) != CellState.NOTFIRED)
                    continue;
                
                double value = probabilityGrid[x][y];
                if (value > max) {
                    max = value;
                    candidates.clear();
                    candidates.add(new Point(x, y));
                } else if (value == max) {
                    candidates.add(new Point(x, y));
                }
            }
        }
        
        return candidates.get(random.nextInt(candidates.size()));
    }
    
    private void updateProbability(GameState state) {
        // Reset griglia probabilità
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                probabilityGrid[x][y] = 0;
            }
        }
        
        Grid grid = state.getEnemyGrid(player);
        List<Ship> remainingShips = state.enemyShipsRemaining(player);
        
        for (Ship ship : remainingShips) {
            int size = ship.getSize();
            
            // ORIZZONTALE
            for (int y = 0; y < height; y++) {
                for (int x = 0; x <= width - size; x++) {
                    PlacementResult result = canPlaceHorizontal(grid, x, y, size);
                    
                    if (result.canPlace) {
                        // Se attraversa un HIT di nave viva, peso 10x
                        double weight = result.hasActiveHit ? 10.0 : 1.0;
                        
                        for (int i = 0; i < size; i++) {
                            probabilityGrid[x + i][y] += weight;
                        }
                    }
                }
            }
            
            // VERTICALE
            for (int x = 0; x < width; x++) {
                for (int y = 0; y <= height - size; y++) {
                    PlacementResult result = canPlaceVertical(grid, x, y, size);
                    
                    if (result.canPlace) {
                        double weight = result.hasActiveHit ? 10.0 : 1.0;
                        
                        for (int i = 0; i < size; i++) {
                            probabilityGrid[x][y + i] += weight;
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Verifica se una nave può essere piazzata orizzontalmente.
     * Controlla anche se gli HIT appartengono a navi già affondate.
     */
    private PlacementResult canPlaceHorizontal(Grid grid, int x, int y, int size) {
        boolean hasActiveHit = false;
        
        for (int i = 0; i < size; i++) {
            int cx = x + i;
            int cy = y;
            
            CellState state = grid.getCellState(cx, cy);
            
            // Bloccato da MISS (acqua)
            if (state == CellState.MISS) {
                return new PlacementResult(false, false);
            }
            
            // Se è HIT, verifica se appartiene a nave affondata
            if (state == CellState.HIT) {
                Cell cell = grid.getCell(cx, cy);
                Ship ship = cell.getShip();
                
                // Se c'è una nave e questa è affondata, blocca
                if (ship != null && ship.isSunk()) {
                    return new PlacementResult(false, false);
                }
                
                // Altrimenti è un HIT di nave viva = BOOST!
                if (ship != null && !ship.isSunk()) {
                    hasActiveHit = true;
                }
            }
        }
        
        return new PlacementResult(true, hasActiveHit);
    }
    
    /**
     * Verifica se una nave può essere piazzata verticalmente.
     * Controlla anche se gli HIT appartengono a navi già affondate.
     */
    private PlacementResult canPlaceVertical(Grid grid, int x, int y, int size) {
        boolean hasActiveHit = false;
        
        for (int i = 0; i < size; i++) {
            int cx = x;
            int cy = y + i;
            
            CellState state = grid.getCellState(cx, cy);
            
            // Bloccato da MISS (acqua)
            if (state == CellState.MISS) {
                return new PlacementResult(false, false);
            }
            
            // Se è HIT, verifica se appartiene a nave affondata
            if (state == CellState.HIT) {
                Cell cell = grid.getCell(cx, cy);
                Ship ship = cell.getShip();
                
                // Se c'è una nave e questa è affondata, blocca
                if (ship != null && ship.isSunk()) {
                    return new PlacementResult(false, false);
                }
                
                // Altrimenti è un HIT di nave viva = BOOST!
                if (ship != null && !ship.isSunk()) {
                    hasActiveHit = true;
                }
            }
        }
        
        return new PlacementResult(true, hasActiveHit);
    }
    
    /**
     * Classe helper per ritornare risultato del placement check.
     */
    private static class PlacementResult {
        final boolean canPlace;      // Può piazzare la nave?
        final boolean hasActiveHit;  // Attraversa un HIT di nave viva?
        
        PlacementResult(boolean canPlace, boolean hasActiveHit) {
            this.canPlace = canPlace;
            this.hasActiveHit = hasActiveHit;
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