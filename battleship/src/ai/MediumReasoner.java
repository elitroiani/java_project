package ai;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import model.CellState;
import model.GameConfig;
import model.GameState;
import model.Grid;
import player.Player;

public class MediumReasoner extends AbstractReasoner {
    
    // Usa Set per evitare duplicati
    private Set<Point> candidates = new HashSet<>();
    private Point lastHit = null;
    private Direction currentDirection = null;
    
    public enum Direction { UP, DOWN, LEFT, RIGHT }
    
    public MediumReasoner(Player player, GameConfig config) {
        super(player, config);
    }
    
    @Override
    public Point chooseMove(GameState state) {
        Grid grid = state.getEnemyGrid(player);
        
        // 1. Pulisci candidati invalidi (già colpiti)
        candidates.removeIf(p -> grid.getCellState(p.x, p.y) != CellState.NOTFIRED);
        
        // 2. Aggiorna candidati solo se non abbiamo una direzione attiva
        if (currentDirection == null) {
            updateCandidatesFromHits(grid);
        }
        
        // 3. Se abbiamo una direzione, continua in quella direzione
        if (currentDirection != null && lastHit != null) {
            Point next = nextInDirection(grid, lastHit, currentDirection);
            
            if (next != null) {
                candidates.remove(next); // rimuovi dai candidati se presente
                return next;
            } else {
                // Direzione bloccata, resetta
                currentDirection = null;
                lastHit = null;
            }
        }
        
        // 4. Scegli tra i candidati disponibili
        if (!candidates.isEmpty()) {
            Point next = chooseFromCandidates();
            
            // Aggiorna direzione se abbiamo un lastHit valido
            if (lastHit != null) {
                updateDirection(lastHit, next);
            }
            
            lastHit = next; // aggiorna ultimo colpo
            return next;
        }
        
        // 5. Fallback: scelta casuale (modalità "hunt")
        Point randomMove = randomCellPicker(state);
        lastHit = null; // reset stato
        currentDirection = null;
        return randomMove;
    }
    
    /**
     * Aggiorna i candidati aggiungendo celle adiacenti a tutti gli HIT
     */
    private void updateCandidatesFromHits(Grid grid) {
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                if (grid.getCellState(x, y) == CellState.HIT) {
                    // Aggiungi adiacenti non ancora sparati
                    candidates.addAll(getAdjacentUntouched(grid, x, y));
                    
                    // Aggiorna lastHit all'ultimo trovato
                    // (potrebbe non essere cronologicamente l'ultimo, 
                    // ma è accettabile per Medium)
                    lastHit = new Point(x, y);
                }
            }
        }
    }
    
    /**
     * Sceglie casualmente tra i candidati, con preferenza per quelli 
     * allineati con lastHit se disponibili
     */
    private Point chooseFromCandidates() {
        if (lastHit == null || candidates.size() == 1) {
            // Scelta casuale semplice
            return candidates.stream()
                            .skip(random.nextInt(candidates.size()))
                            .findFirst()
                            .orElseThrow();
        }
        
        // Preferisci candidati allineati con lastHit (stessa riga o colonna)
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
        
        // Altrimenti casuale
        Point chosen = candidates.stream()
                                 .skip(random.nextInt(candidates.size()))
                                 .findFirst()
                                 .orElseThrow();
        candidates.remove(chosen);
        return chosen;
    }
    
    /**
     * Calcola la prossima cella nella direzione specificata
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
     * Deduce la direzione tra due punti consecutivi
     */
    private void updateDirection(Point last, Point next) {
        if (last.x == next.x) {
            // Movimento verticale
            currentDirection = (next.y > last.y) ? Direction.DOWN : Direction.UP;
        } else if (last.y == next.y) {
            // Movimento orizzontale
            currentDirection = (next.x > last.x) ? Direction.RIGHT : Direction.LEFT;
        } else {
            // Movimento diagonale (non dovrebbe accadere con getAdjacentUntouched)
            currentDirection = null;
        }
    }
}