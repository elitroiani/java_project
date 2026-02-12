package battleship;

import controller.GameControllerGUI;
import model.GameConfig;
import model.GameState;
import model.Grid;
import player.AIPlayer;
import player.HumanPlayer;
import player.Player;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            GameConfig config = new GameConfig();

            Grid humanGrid = new Grid(config.getWidth(), config.getHeight());
            Grid aiGrid = new Grid(config.getWidth(), config.getHeight());

            HumanPlayer human = new HumanPlayer("Giocatore", humanGrid);
            AIPlayer ai = new AIPlayer("Computer", aiGrid, null);

            GameState state = new GameState(human, ai, config);

            GameControllerGUI controller = new GameControllerGUI(state);

            controller.startGame("medium");

        });
    }
}
 