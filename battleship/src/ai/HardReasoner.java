package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import model.*;
import player.Player;

/**
 * An advanced AI implementation that uses a dynamic Heat Map to target ships.
 * It prioritizes cells based on proximity to hits, ship alignment (direction),
 * and uses a checkerboard pattern for efficient exploration.
 */
public class HardReasoner extends AbstractReasoner {

    /** Matrix representing the probability/attractiveness of each cell */
    private int[][] heat;
    private final int width;
    private final int height;

    public HardReasoner(Player player, GameConfig config) {
        super(player, config);
        this.width = config.getWidth();
        this.height = config.getHeight();
        this.heat = new int[width][height];
    }

    /**
     * Selects the next move by finding the cell(s) with the highest heat value.
     * If multiple cells have the same maximum heat, one is picked at random.
     */
    @Override
    public Point chooseMove(GameState state) {
        Grid grid = state.getEnemyGrid(player);
        
        // Recalculate the heat map based on the current grid state
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

        // If no tactical heat is found (e.g., Hunt Mode), use checkerboard strategy
        if (maxHeat <= 1) {
            return checkerboardPicker(state);
        }
        
        // Randomly pick from the most promising target cells
        return candidates.get(random.nextInt(candidates.size()));
    }

    /**
     * Refreshes the heat map. Logic flows from: 
     * 1. Reset -> 2. Tactical Analysis -> 3. Cleaning already fired cells.
     */
    private void updateHeat(Grid grid) {
        // 1. Initialization: Reset all cells to a base value of 1
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                heat[x][y] = 1;
            }
        }

        // 2. Strategic Calculation
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = grid.getCell(x, y);
                
                if (cell.getState() == CellState.HIT) {
                    Ship ship = cell.getShip();
                    
                    // If the ship is sunk, neutralize surrounding area (Standard Rule: ships can't touch)
                    if (ship != null && ship.isSunk()) {
                        applySunkenPenalty(grid, x, y);
                    } else {
                        // TARGETING MODE: Identify ship orientation and boost heat along that axis
                        applyDirectionalHeat(grid, x, y);
                    }
                }
            }
        }

        // 3. Cleanup: Set heat to 0 for any cell that has already been fired upon
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid.getCell(x, y).getState() != CellState.NOTFIRED) {
                    heat[x][y] = 0;
                }
            }
        }
    }

    /**
     * Applies heat to adjacent cells. If two hits are aligned, it heavily 
     * boosts the heat of the next cell in that line.
     */
    private void applyDirectionalHeat(Grid grid, int x, int y) {
        // Orthogonal directions: Right, Left, Down, Up
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];

            if (grid.isValidCoordinate(nx, ny) && grid.getCell(nx, ny).getState() == CellState.NOTFIRED) {
                // Standard proximity bonus
                heat[nx][ny] += 10;

                // ALIGNMENT CHECK:
                // If there is another HIT in the OPPOSITE direction, it confirms the ship's axis.
                // We add a significant bonus to "continue" the line.
                int ox = x - d[0];
                int oy = y - d[1];
                if (grid.isValidCoordinate(ox, oy) && grid.getCell(ox, oy).getState() == CellState.HIT) {
                    heat[nx][ny] += 25; // Massive boost for maintaining the direction
                }
            }
        }
    }

    /**
     * Hunt Mode Strategy: Checkerboard pattern.
     * Filters available cells to only those where (x + y) is even.
     * This ensures the AI finds any ship (minimum size 2) in half the turns.
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
        
        // If no checkerboard cells are left, fallback to standard random selection
        if (checkerboard.isEmpty()) return randomCellPicker(state);
        return checkerboard.get(random.nextInt(checkerboard.size()));
    }

    /**
     * Nullifies the heat in a 3x3 area around a sunk ship's cell.
     * Used because ships usually cannot be placed immediately adjacent to each other.
     */
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