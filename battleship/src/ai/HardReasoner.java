package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import model.*;
import player.Player;

public class HardReasoner extends AbstractReasoner {

    private int[][] heat;
    private final int width;
    private final int height;

    public HardReasoner(Player player, GameConfig config) {
        super(player, config);
        this.width = config.getWidth();
        this.height = config.getHeight();
        this.heat = new int[width][height];
    }

    @Override
    public Point chooseMove(GameState state) {
        Grid grid = state.getEnemyGrid(player);
        updateHeat(grid);

        int maxHeat = -1;
        List<Point> candidates = new ArrayList<>();

        // Usiamo il tuo metodo ereditato per scansionare solo le celle "vergini"
        List<Point> available = getUntouchedCells(grid);
        
        for (Point p : available) {
            int h = heat[p.x][p.y];
            if (h > maxHeat) {
                maxHeat = h;
                candidates.clear();
                candidates.add(p);
            } else if (h == maxHeat) {
                candidates.add(p);
            }
        }

        // Se non c'è una zona calda (es. all'inizio o dopo un affondamento)
        // usiamo il tuo selezionatore casuale intelligente
        if (maxHeat <= 1) {
            return randomCellPicker(state);
        }
        
        return candidates.get(random.nextInt(candidates.size()));
    }

    private void updateHeat(Grid grid) {
        // 1. Reset
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                heat[x][y] = 1;
            }
        }

        // 2. Calcolo Calore
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = grid.getCell(x, y);
                
                if (cell.getState() == CellState.HIT) {
                    Ship ship = cell.getShip();
                    
                    if (ship != null && ship.isSunk()) {
                        // Se la nave è affondata, applichiamo la penale di vicinato
                        applySunkenPenalty(grid, x, y);
                    } else {
                        // USIAMO IL TUO METODO: getAdjacentUntouched
                        // Alza il calore solo delle celle ortogonali libere
                        List<Point> adjacent = getAdjacentUntouched(grid, x, y);
                        for (Point p : adjacent) {
                            heat[p.x][p.y] += 10;
                        }
                    }
                }
                
                // Se la cella non è più sparabile, il calore è 0
                if (cell.getState() != CellState.NOTFIRED) {
                    heat[x][y] = 0;
                }
            }
        }
    }

    private void applySunkenPenalty(Grid grid, int x, int y) {
        // Usiamo grid.isValidCoordinate che avevamo scritto nella classe Grid
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                
                if (grid.isValidCoordinate(nx, ny)) {
                    heat[nx][ny] = 0;
                }
            }
        }
    }
}
