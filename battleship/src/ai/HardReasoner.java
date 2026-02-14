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

        // Se non c'è calore (fase di caccia), usiamo una strategia a scacchiera
        if (maxHeat <= 1) {
            return checkerboardPicker(state);
        }
        
        return candidates.get(random.nextInt(candidates.size()));
    }

    private void updateHeat(Grid grid) {
        // 1. Reset base
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                heat[x][y] = 1;
            }
        }

        // 2. Calcolo del Calore Strategico
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = grid.getCell(x, y);
                
                if (cell.getState() == CellState.HIT) {
                    Ship ship = cell.getShip();
                    
                    if (ship != null && ship.isSunk()) {
                        applySunkenPenalty(grid, x, y);
                    } else {
                        // --- LOGICA DIREZIONALE ---
                        // Calore base per le celle adiacenti
                        applyDirectionalHeat(grid, x, y);
                    }
                }
            }
        }

        // 3. Azzeramento celle già colpite (sempre per ultime)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid.getCell(x, y).getState() != CellState.NOTFIRED) {
                    heat[x][y] = 0;
                }
            }
        }
    }

    private void applyDirectionalHeat(Grid grid, int x, int y) {
        // Direzioni: Destra, Sinistra, Giù, Su
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];

            if (grid.isValidCoordinate(nx, ny) && grid.getCell(nx, ny).getState() == CellState.NOTFIRED) {
                // Calore base per adiacenza
                heat[nx][ny] += 10;

                // CONTROLLO ALLINEAMENTO:
                // Se nella direzione OPPOSTA a quella in cui sto guardando c'è un altro HIT,
                // significa che ho trovato la direzione della nave! Aumento molto il calore.
                int ox = x - d[0];
                int oy = y - d[1];
                if (grid.isValidCoordinate(ox, oy) && grid.getCell(ox, oy).getState() == CellState.HIT) {
                    heat[nx][ny] += 25; // Bonus direzione: la nave "prosegue" qui
                }
            }
        }
    }

    /**
     * Strategia a scacchiera per la fase di caccia: 
     * spara solo dove (x + y) è pari. Dimezza i turni di ricerca.
     */
    private Point checkerboardPicker(GameState state) {
        Grid grid = state.getEnemyGrid(player);
        List<Point> available = getUntouchedCells(grid);
        List<Point> checkerboard = new ArrayList<>();
        
        for (Point p : available) {
            if ((p.x + p.y) % 2 == 0) {
                checkerboard.add(p);
            }
        }
        
        if (checkerboard.isEmpty()) return randomCellPicker(state);
        return checkerboard.get(random.nextInt(checkerboard.size()));
    }

    private void applySunkenPenalty(Grid grid, int x, int y) {
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