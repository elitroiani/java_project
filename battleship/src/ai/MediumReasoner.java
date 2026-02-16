package ai;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.CellState;
import model.GameConfig;
import model.GameState;
import model.Grid;
import model.Cell;
import player.Player;

/**
 * REFINED MEDIUM AI REASONER
 * Strategy: "Hunt and Target" with proximity filtering.
 * This AI tracks hits to sink ships efficiently and uses grid rules to skip 
 * impossible cell locations (buffer zones).
 */
public class MediumReasoner extends AbstractReasoner {
    
    // Set of potential target coordinates adjacent to a hit
    private Set<Point> candidates = new HashSet<>();
    
    // The very first hit of the ship we are currently attacking
    private Point firstHitOfCurrentShip = null;
    
    // The most recent successful hit (used to determine direction)
    private Point lastHit = null;
    
    // Current firing axis (UP, DOWN, LEFT, RIGHT) once a direction is found
    private Direction currentDirection = null;
    
    public enum Direction { UP, DOWN, LEFT, RIGHT }
    
    public MediumReasoner(Player player, GameConfig config) {
        super(player, config);
    }
    
    @Override
    public Point chooseMove(GameState state) {
        Grid grid = state.getEnemyGrid(player);
        
        // --- STEP 1: SMART CLEANUP ---
        // Remove any candidates that are no longer valid targets.
        // This includes cells that were recently fired upon or that became 
        // "buffer zones" because a nearby ship was sunk.
        candidates.removeIf(p -> !grid.isPotentialTarget(p.x, p.y));
        
        // --- STEP 2: SUNK CHECK ---
        // Check if the ship we were tracking has been sunk.
        // If so, we reset targeting data to stop wasting shots around it.
        if (firstHitOfCurrentShip != null) {
            Cell firstCell = grid.getCell(firstHitOfCurrentShip.x, firstHitOfCurrentShip.y);
            if (firstCell.hasShip() && firstCell.getShip().get().isSunk()) {
                resetTargeting();
            }
        }

        // --- STEP 3: DIRECTIONAL MODE (LINEAR ATTACK) ---
        // If we know the ship's orientation (currentDirection), keep firing along that line.
        if (currentDirection != null && lastHit != null) {
            Point next = nextInDirection(grid, lastHit, currentDirection);
            if (next != null) {
                return next; // Valid next shot in the same direction
            } else {
                // We reached an edge, water, or a forbidden zone. 
                // Flip the direction and start again from the first hit to find the other end.
                currentDirection = reverseDirection(currentDirection);
                lastHit = firstHitOfCurrentShip;
                Point reverseNext = nextInDirection(grid, lastHit, currentDirection);
                
                if (reverseNext != null) return reverseNext;
                
                // If both ends are blocked, the ship is likely done. Reset direction.
                currentDirection = null; 
            }
        }
        
        // --- STEP 4: CANDIDATE EXPLORATION (SKEW ATTACK) ---
        // If we have hit a ship once but don't know the direction yet, 
        // try one of the adjacent candidate cells.
        if (!candidates.isEmpty()) {
            Point next = chooseFromCandidates();
            
            // If the last shot was a hit and this next one is adjacent, 
            // we can establish a firing axis (direction).
            if (lastHit != null && grid.getCellState(lastHit.x, lastHit.y) == CellState.HIT) {
                if (isAdjacent(lastHit, next)) {
                    updateDirection(lastHit, next);
                }
            }
            return next;
        }
        
        // --- STEP 5: RE-ENGAGEMENT (CLEANUP SCATTERED HITS) ---
        // Scan the grid for any successful hits that belong to ships not yet sunk.
        // This happens if we hit a ship but got distracted by another one.
        Point activeHit = findAnyActiveHit(grid);
        if (activeHit != null) {
            // Clear old ship data to prevent logical "jumping" between distant ships
            resetTargeting(); 
            
            firstHitOfCurrentShip = activeHit;
            lastHit = activeHit;
            // Generate new smart candidates around this existing hit
            addSmartNeighbors(grid, activeHit);
            
            if (!candidates.isEmpty()) {
                return chooseFromCandidates();
            }
        }
        
        // --- STEP 6: SMART HUNT MODE (RANDOM SEARCH) ---
        // No active targets left. Pick a random cell from the "Smart List".
        // This list excludes all cells where a ship cannot possibly exist.
        resetTargeting();
        List<Cell> smartCells = grid.getSmartUntouchedCells();
        if (!smartCells.isEmpty()) {
            Point p = smartCells.get(random.nextInt(smartCells.size())).getCoordinates();
            lastHit = p; // Seed lastHit for potential candidate logic next turn
            return p;
        }
        
        // Final fallback: standard random picker
        return randomCellPicker(state);
    }

    /**
     * Clears all internal states and candidate sets.
     */
    private void resetTargeting() {
        candidates.clear();
        firstHitOfCurrentShip = null;
        lastHit = null;
        currentDirection = null;
    }

    /**
     * Adds North, South, East, and West neighbors of a point to candidates,
     * provided they are within bounds and logically targetable.
     */
    private void addSmartNeighbors(Grid grid, Point p) {
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] d : dirs) {
            int nx = p.x + d[0];
            int ny = p.y + d[1];
            if (grid.isValidCoordinate(nx, ny) && grid.isPotentialTarget(nx, ny)) {
                candidates.add(new Point(nx, ny));
            }
        }
    }

    /**
     * Iterates through the grid to find a HIT cell that isn't part of a sunk ship.
     */
    private Point findAnyActiveHit(Grid grid) {
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                Cell cell = grid.getCell(x, y);
                // Cell must be HIT, have a ship, and that ship must be afloat
                if (cell.getState() == CellState.HIT && cell.hasShip() && !cell.getShip().get().isSunk()) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    /**
     * Picks one point from the candidate set and removes it to prevent duplicate shots.
     */
    private Point chooseFromCandidates() {
        Point chosen = candidates.iterator().next();
        candidates.remove(chosen);
        return chosen;
    }

    /**
     * Simple utility to flip the search direction 180 degrees.
     */
    private Direction reverseDirection(Direction dir) {
        return switch (dir) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }

    /**
     * Calculates the next point in a line and ensures it's a "Smart" target.
     */
    private Point nextInDirection(Grid grid, Point from, Direction dir) {
        int nx = from.x, ny = from.y;
        switch (dir) {
            case UP -> ny--; case DOWN -> ny++;
            case LEFT -> nx--; case RIGHT -> nx++;
        }
        if (grid.isValidCoordinate(nx, ny) && grid.isPotentialTarget(nx, ny)) {
            return new Point(nx, ny);
        }
        return null;
    }

    /**
     * Determines the vertical or horizontal axis based on the last two hits.
     */
    private void updateDirection(Point last, Point next) {
        if (last.x == next.x) {
            currentDirection = (next.y > last.y) ? Direction.DOWN : Direction.UP;
        } else if (last.y == next.y) {
            currentDirection = (next.x > last.x) ? Direction.RIGHT : Direction.LEFT;
        }
    }

    /**
     * Checks if two points are exactly one cell apart (Manhattan distance of 1).
     */
    private boolean isAdjacent(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) == 1;
    }
}