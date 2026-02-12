package view;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final GridPanel playerGridPanel;
    private final GridPanel enemyGridPanel;
    private final JLabel statusLabel;

    public GameFrame(int width, int height) {

        setTitle("Battaglia Navale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        playerGridPanel = new GridPanel(width, height, true);
        enemyGridPanel = new GridPanel(width, height, false);

        statusLabel = new JLabel("Posiziona le navi", SwingConstants.CENTER);

        JPanel grids = new JPanel(new GridLayout(1, 2));
        grids.add(playerGridPanel);
        grids.add(enemyGridPanel);

        add(grids, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public GridPanel getPlayerGridPanel() {
        return playerGridPanel;
    }

    public GridPanel getEnemyGridPanel() {
        return enemyGridPanel;
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }
}
