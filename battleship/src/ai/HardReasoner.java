package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.CellState;
import model.GameConfig;
import model.GameState;
import model.Grid;
import player.Player;

public class HardReasoner extends AbstractReasoner {

    private int[][] heat;
    private int width, height;
    //private Random random = new Random();  RIMUOVO PERCHè è EREDITATO DA ABSTRACTREASONER

    public HardReasoner(Player player, GameConfig config) {
        super(player, config);
        this.width = config.getWidth();
        this.height = config.getHeight();
        this.heat = new int[width][height];
        initializeHeat();
    }

    @Override
    public Point chooseMove(GameState state) {
        Grid grid = state.getEnemyGrid(player);

        // Aggiorna heat map prima di scegliere
        updateHeat(grid);

        // Trova celle NOTFIRED con heat massimo
        int maxHeat = Integer.MIN_VALUE;
        List<Point> candidates = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid.getCellState(x, y) == CellState.NOTFIRED) {
                    if (heat[x][y] > maxHeat) {
                        maxHeat = heat[x][y];
                        candidates.clear();
                        candidates.add(new Point(x, y));
                    } else if (heat[x][y] == maxHeat) {
                        candidates.add(new Point(x, y));
                    }
                }
            }
        }

        // Scegli casualmente tra le celle con heat massimo
        return candidates.get(random.nextInt(candidates.size()));
    }

    // -------------------------------
    // Inizializza la griglia di calore
    // -------------------------------
    private void initializeHeat() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                heat[x][y] = 1; // valore base uguale per tutte le celle
            }
        }
    }

    // -------------------------------
    // Aggiorna il calore in base a HIT/MISS
    // -------------------------------
    private void updateHeat(Grid grid) {
        // Reset parziale (puoi anche sommare valori per accumulare memoria)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid.getCellState(x, y) != CellState.NOTFIRED) {
                    heat[x][y] = 0; // non più sparabile
                }
            }
        }

        // Aumenta heat vicino a HIT
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid.getCellState(x, y) == CellState.HIT) {
                    increaseAdjacentHeat(x, y);
                }
            }
        }
    }

    private void increaseAdjacentHeat(int x, int y) {
        int[][] deltas = {{-1,0},{1,0},{0,-1},{0,1}}; // solo ortogonali
        for (int[] d : deltas) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                heat[nx][ny] += 5; // incremento arbitrario, maggiore = più appetibile
            }
        }
    }
}
