package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BattleView extends JFrame {
    private JButton[][] playerGridButtons;
    private JButton[][] enemyGridButtons;
    private JLabel statusLabel;
    private JRadioButton horizontalRadio;
    private JPanel setupPanel;

    public BattleView(int width, int height) {
        setTitle("Battaglia Navale - Comandante in Capo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Status bar
        statusLabel = new JLabel("Fase di Setup: Piazza le tue navi", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(statusLabel, BorderLayout.NORTH);

        // Griglie
        JPanel gridContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        playerGridButtons = createGrid(gridContainer, width, height, "LA TUA FLOTTA");
        enemyGridButtons = createGrid(gridContainer, width, height, "RADAR NEMICO");
        add(gridContainer, BorderLayout.CENTER);

        // Controlli Setup
        setupPanel = new JPanel();
        horizontalRadio = new JRadioButton("Verticale", true);
        JRadioButton verticalRadio = new JRadioButton("Orizzontale");
        ButtonGroup group = new ButtonGroup();
        group.add(horizontalRadio); group.add(verticalRadio);
        setupPanel.add(new JLabel("Orientamento: "));
        setupPanel.add(horizontalRadio);
        setupPanel.add(verticalRadio);
        add(setupPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JButton[][] createGrid(JPanel parent, int w, int h, String title) {
        JPanel c = new JPanel(new BorderLayout());
        c.add(new JLabel(title, JLabel.CENTER), BorderLayout.NORTH);
        JPanel g = new JPanel(new GridLayout(w, h));
        JButton[][] btns = new JButton[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                btns[i][j] = new JButton();
                btns[i][j].setPreferredSize(new Dimension(35, 35));
                btns[i][j].setBackground(new Color(30, 144, 255));
                g.add(btns[i][j]);
            }
        }
        c.add(g, BorderLayout.CENTER);
        parent.add(c);
        return btns;
    }
    
    public void disableEnemyGrid() {
        for (JButton[] row : enemyGridButtons) {
            for (JButton btn : row) {
                btn.setEnabled(false);
            }
        }
    }

    public void enableEnemyGrid() {
        for (JButton[] row : enemyGridButtons) {
            for (JButton btn : row) {
                // Riabilita solo se la cella non è già stata colpita (opzionale ma consigliato)
                if (btn.getBackground().equals(new Color(30, 144, 255))) { 
                    btn.setEnabled(true);
                }
            }
        }
    }

    public void setPlayerListener(int x, int y, ActionListener l) { playerGridButtons[x][y].addActionListener(l); }
    public void setEnemyListener(int x, int y, ActionListener l) { enemyGridButtons[x][y].addActionListener(l); }
    public boolean isHorizontal() { return horizontalRadio.isSelected(); }
    public void setStatus(String text) { statusLabel.setText(text); }
    
    public void hideSetup() { setupPanel.setVisible(false); pack(); }

    public void updateCell(boolean isEnemy, int x, int y, Color c, String t) {
        JButton[][] g = isEnemy ? enemyGridButtons : playerGridButtons;
        g[x][y].setBackground(c);
        g[x][y].setText(t);
    }
}