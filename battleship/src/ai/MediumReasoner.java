package ai;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import model.CellState;
import model.GameConfig;
import model.GameState;
import model.Grid;
import player.Player;

/**
 * AI implementation that simulates a human-like "Targeting" strategy.
 * Once it hits a ship, it explores adjacent cells and tries to deduce the 
 * ship's orientation to finish it off.
 */
public class MediumReasoner extends AbstractReasoner {
    
    /** Set of potential target coordinates adjacent to successful hits */
    private Set<Point> candidates = new HashSet<>();
    
    /** Tracks the last successful hit to determine direction */
    private Point lastHit = null;
    
    /** Current orientation of the target ship being attacked */
    private Direction currentDirection = null;
    
    /** Possible directions for ship orientation */
    public enum Direction { UP, DOWN, LEFT, RIGHT }
    
    public MediumReasoner(Player player, GameConfig config) {
        super(player, config);
    }
    
    @Override
    public Point chooseMove(GameState state) {
        Grid grid = state.getEnemyGrid(player);
        
        // 1. Clean up: Remove candidates that have already been fired upon
        candidates.removeIf(p -> grid.getCellState(p.x, p.y) != CellState.NOTFIRED);
        
        // 2. Target Mode Initiation: If no specific direction is set, find hits to follow
        if (currentDirection == null) {
            updateCandidatesFromHits(grid);
        }
        
        // 3. Execution: If we have an active direction, continue striking along that line
        if (currentDirection != null && lastHit != null) {
            Point next = nextInDirection(grid, lastHit, currentDirection);
            
            if (next != null) {
                candidates.remove(next); // Ensure we don't pick this point again from candidates
                return next;
            } else {
                // Direction blocked (edge or miss), reset state to re-evaluate
                currentDirection = null;
                lastHit = null;
            }
        }
        
        // 4. Exploration: Pick from known potential targets (adjacent to previous hits)
        if (!candidates.isEmpty()) {
            Point next = chooseFromCandidates();
            
            // If we have a previous hit, try to determine the direction based on the new move
            if (lastHit != null) {
                updateDirection(lastHit, next);
            }
            
            lastHit = next; 
            return next;
        }
        
        // 5. Hunt Mode Fallback: No active targets, perform a random search
        Point randomMove = randomCellPicker(state);
        lastHit = null; 
        currentDirection = null;
        return randomMove;
    }
    
    /**
     * Scans the grid for successful hits and adds untouched adjacent cells to candidates.
     */
    private void updateCandidatesFromHits(Grid grid) {
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                if (grid.getCellState(x, y) == CellState.HIT) {
                    // Add orthogonal neighbors using the inherited utility
                    candidates.addAll(getAdjacentUntouched(grid, x, y));
                    
                    // Set as reference point for the next move
                    lastHit = new Point(x, y);
                }
            }
        }
    }
    
    /**
     * Selects a coordinate from candidates, preferring those aligned with the last hit.
     */
    private Point chooseFromCandidates() {
        if (lastHit == null || candidates.size() == 1) {
            return candidates.stream()
                            .skip(random.nextInt(candidates.size()))
                            .findFirst()
                            .orElseThrow();
        }
        
        // Heuristic: Prefer candidates in the same row or column as the last hit
        Set<Point> aligned = new HashSet<>();
        for (Point p : candidates) {
            if (p.x == lastHit.x || p.y == lastHit.y) {
                aligned.add(p);
            }
        }
        
        if (!aligned.isEmpty()) {
            Point chosen = aligned.stream()
                                  .skip(random.nextInt(aligned.size()))
                                  .findFirst()
                                  .orElseThrow();
            candidates.remove(chosen);
            return chosen;
        }
        
        // Fallback to random candidate selection
        Point chosen = candidates.stream()
                                 .skip(random.nextInt(candidates.size()))
                                 .findFirst()
                                 .orElseThrow();
        candidates.remove(chosen);
        return chosen;
    }
    
    /**
     * Calculates the next point in a specific direction.
     * @return Point if valid and untouched, null otherwise.
     */
    private Point nextInDirection(Grid grid, Point from, Direction dir) {
        int nx = from.x;
        int ny = from.y;
        
        switch (dir) {
            case UP -> ny--;
            case DOWN -> ny++;
            case LEFT -> nx--;
            case RIGHT -> nx++;
        }
        
        if (grid.isValidCoordinate(nx, ny) && 
            grid.getCellState(nx, ny) == CellState.NOTFIRED) {
            return new Point(nx, ny);
        }
        
        return null;
    }
    
    /**
     * Deduces the ship orientation (Direction) based on the movement between two points.
     */
    private void updateDirection(Point last, Point next) {
        if (last.x == next.x) {
            // Vertical movement identified
            currentDirection = (next.y > last.y) ? Direction.DOWN : Direction.UP;
        } else if (last.y == next.y) {
            // Horizontal movement identified
            currentDirection = (next.x > last.x) ? Direction.RIGHT : Direction.LEFT;
        } else {
            // Diagonal movement (safety reset, shouldn't occur in standard play)
            currentDirection = null;
        }
    }
}