package view;

import controller.GameControllerGUI;
import model.*;

import javax.swing.*;
import java.awt.*;
	
public class GameView extends JFrame {

    private final JButton[][] playerButtons;
    private final JButton[][] enemyButtons;

    private final GameControllerGUI controller;
    private final GameState state;

    private final JLabel infoLabel;

    public GameView(GameControllerGUI controller, GameState state) {

        this.controller = controller;
        this.state = state;

        int size = state.getConfig().getGridSize();

        playerButtons = new JButton[size][size];
        enemyButtons = new JButton[size][size];

        setTitle("Battaglia Navale");
        setLayout(new BorderLayout());

        JPanel gridsPanel = new JPanel(new GridLayout(1, 2));

        gridsPanel.add(createGridPanel(playerButtons, true));
        gridsPanel.add(createGridPanel(enemyButtons, false));

        infoLabel = new JLabel("Piazza le tue navi", SwingConstants.CENTER);

        add(infoLabel, BorderLayout.NORTH);
        add(gridsPanel, BorderLayout.CENTER);

        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JPanel createGridPanel(JButton[][] buttons, boolean playerGrid) {

        int size = state.getConfig().getGridSize();
        JPanel panel = new JPanel(new GridLayout(size, size));

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {

                JButton btn = new JButton();
                int finalX = x;
                int finalY = y;

                btn.addActionListener(e -> {
                    if (playerGrid) {
                        controller.handlePlayerGridClick(finalX, finalY);
                    } else {
                        controller.handleEnemyGridClick(finalX, finalY);
                    }
                });

                buttons[x][y] = btn;
                panel.add(btn);
            }
        }

        return panel;
    }

    public void setInfo(String text) {
        infoLabel.setText(text);
    }

    public void refresh() {

        refreshGrid(playerButtons, state.getHumanPlayer().getGrid(), true);
        refreshGrid(enemyButtons,
                state.getEnemyGrid(state.getHumanPlayer()), false);
    }

    private void refreshGrid(JButton[][] buttons, Grid grid, boolean showShips) {

        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {

                Cell cell = grid.getCell(x, y);

                JButton btn = buttons[x][y];

                btn.setBackground(null);
                btn.setText("");

                switch (cell.getState()) {
                    case HIT -> btn.setBackground(Color.RED);
                    case MISS -> btn.setBackground(Color.CYAN);
                }

                if (showShips && cell.hasShip() && cell.getState() != CellState.HIT) {
                    btn.setBackground(Color.GRAY);
                }
            }
        }
    }
}


