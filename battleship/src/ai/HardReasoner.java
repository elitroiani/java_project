package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import model.*;
import player.Player;

/**
 * A more aggressive AI that uses a Heat Map to target ships.
 * It prioritizes cells adjacent to successful hits and applies penalties 
 * to areas around sunken ships.
 */
public class HardReasoner extends AbstractReasoner {

    /** Matrix representing the "heat" or attractiveness of each cell */
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
     * Chooses the next move by selecting the cell with the highest heat value.
     */
    @Override
    public Point chooseMove(GameState state) {
        Grid grid = state.getEnemyGrid(player);
        
        // Refresh the heat map based on the latest grid information
        updateHeat(grid);

        int maxHeat = -1;
        List<Point> candidates = new ArrayList<>();

        // Scan only untouched cells using the inherited utility method
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

        // If no high-heat zone is found (e.g., at the start or after a ship sinks),
        // fallback to the intelligent random picker.
        if (maxHeat <= 1) {
            return randomCellPicker(state);
        }
        
        // Randomly pick one of the best candidates (those with max heat)
        return candidates.get(random.nextInt(candidates.size()));
    }

    /**
     * Updates the heat map values. 
     * Higher values are assigned to cells next to successful hits.
     */
    private void updateHeat(Grid grid) {
        // 1. Reset: initialize all cells with a base heat of 1
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                heat[x][y] = 1;
            }
        }

        // 2. Heat Calculation Logic
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Cell cell = grid.getCell(x, y);
                
                // If we found a successful hit on an enemy ship
                if (cell.getState() == CellState.HIT) {
                    Ship ship = cell.getShip();
                    
                    if (ship != null && ship.isSunk()) {
                        // If the ship is already sunk, penalize neighboring cells 
                        // as they are unlikely to contain another ship (Standard Rules)
                        applySunkenPenalty(grid, x, y);
                    } else {
                        // TARGETING MODE: Increase heat for adjacent orthogonal cells
                        // that have not been fired upon yet.
                        List<Point> adjacent = getAdjacentUntouched(grid, x, y);
                        for (Point p : adjacent) {
                            heat[p.x][p.y] += 10;
                        }
                    }
                }
                
                // If a cell has already been targeted (Hit or Miss), it can't be chosen again
                if (cell.getState() != CellState.NOTFIRED) {
                    heat[x][y] = 0;
                }
            }
        }
    }

    /**
     * Sets heat to 0 for all surrounding cells (including diagonals) 
     * once a ship is confirmed as sunk.
     */
    private void applySunkenPenalty(Grid grid, int x, int y) {
        // Iterate through the 3x3 area centered on the hit
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                
                // Ensure the penalty is only applied within grid boundaries
                if (grid.isValidCoordinate(nx, ny)) {
                    heat[nx][ny] = 0;
                }
            }
        }
    }
}
