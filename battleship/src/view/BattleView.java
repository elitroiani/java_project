package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * BattleView handles the Graphical User Interface for the game.
 * It manages the display of grids, ship placement assets, and battle status.
 */
public class BattleView extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JButton[][] playerGridButtons;
    private JButton[][] enemyGridButtons;
    
    private final Color SEA_COLOR = new Color(30, 144, 255);
    private final Color SUNK_COLOR = new Color(44, 62, 80); 
    
    private JLabel statusLabel;
    private JButton globalMenuBtn; 
    private JPanel southContainer;
    private CardLayout southLayout;
    private JPanel setupPanel;
    private JRadioButton horizontalRadio;
    private JButton resetPlacementBtn;
    private JPanel resultPanel;
    private JLabel resultLabel;
    private JButton endRestartBtn;

    /**
     * Initializes the view with the specified grid dimensions.
     */
    public BattleView(int width, int height) {
        // Keeping original Italian titles and labels
        setTitle("Battaglia Navale - Comandante in Capo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- TOP PANEL Setup ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusLabel = new JLabel("Fase di Setup: Piazza le tue navi", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        globalMenuBtn = new JButton("Menu");
        styleButton(globalMenuBtn, new Color(255, 140, 0));
        topPanel.add(statusLabel, BorderLayout.CENTER);
        topPanel.add(globalMenuBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- GRID CONTAINER Setup ---
        JPanel gridContainer = new JPanel(new GridLayout(1, 2, 30, 0));
        gridContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        playerGridButtons = createGrid(gridContainer, width, height, "LA TUA FLOTTA");
        enemyGridButtons = createGrid(gridContainer, width, height, "RADAR NEMICO");
        add(gridContainer, BorderLayout.CENTER);

        // --- SOUTH CONTAINER Setup (CardLayout) ---
        southLayout = new CardLayout();
        southContainer = new JPanel(southLayout);
        
        setupPanel = new JPanel();
        horizontalRadio = new JRadioButton("Verticale", true);
        JRadioButton verticalRadio = new JRadioButton("Orizzontale");
        ButtonGroup group = new ButtonGroup();
        group.add(horizontalRadio); group.add(verticalRadio);
        resetPlacementBtn = new JButton("Reset Griglia");
        styleButton(resetPlacementBtn, new Color(231, 76, 60));
        setupPanel.add(new JLabel("Orientamento: "));
        setupPanel.add(horizontalRadio);
        setupPanel.add(verticalRadio);
        setupPanel.add(Box.createHorizontalStrut(20));
        setupPanel.add(resetPlacementBtn);
        
        resultPanel = new JPanel();
        resultLabel = new JLabel("");
        endRestartBtn = new JButton("TORNA AL MENU");
        styleButton(endRestartBtn, new Color(52, 152, 219));
        resultPanel.add(resultLabel);
        resultPanel.add(endRestartBtn);

        southContainer.add(setupPanel, "SETUP");
        southContainer.add(resultPanel, "RESULT");
        add(southContainer, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }
    
    /**
     * Prevents clicks on the enemy grid during AI turns.
     */
    public void disableInteraction() {
        for (JButton[] row : enemyGridButtons) {
            for (JButton btn : row) {
                btn.setEnabled(false);
            }
        }
    }

    /**
     * Enables clickable buttons (water cells with no icons).
     */
    public void enableInteraction() {
        for (int i = 0; i < enemyGridButtons.length; i++) {
            for (int j = 0; j < enemyGridButtons[i].length; j++) {
                if (enemyGridButtons[i][j].getBackground().equals(SEA_COLOR) && 
                    enemyGridButtons[i][j].getIcon() == null) {
                    enemyGridButtons[i][j].setEnabled(true);
                }
            }
        }
    }

    /**
     * Loads and scales image assets from the resources folder.
     */
    private ImageIcon getShipIcon(String filename) {
        try {
            java.net.URL imgURL = getClass().getResource("/" + filename);
            if (imgURL == null) {
                imgURL = getClass().getResource("/resources/" + filename);
            }

            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Renders ship images across multiple grid cells based on orientation.
     */
    public void renderSunkenShip(boolean isEnemy, List<Point> positions) {
        if (positions == null || positions.isEmpty()) return;

        java.util.List<Point> modifiablePositions = new java.util.ArrayList<>(positions);
        modifiablePositions.sort((p1, p2) -> (p1.x != p2.x) ? p1.x - p2.x : p1.y - p2.y);
        
        int size = modifiablePositions.size();
        boolean isVertical = (size > 1 && modifiablePositions.get(0).x != modifiablePositions.get(size - 1).x);

        for (int i = 0; i < size; i++) {
            Point p = modifiablePositions.get(i);
            String imgName;

            if (size == 1) {
                imgName = "ship_single.png";
            } else if (isVertical) {
                if (i == 0) imgName = "ship_top.png";
                else if (i == size - 1) imgName = "ship_bot.png";
                else imgName = "ship_mid.png";
            } else {
                if (i == 0) imgName = "ship_left.png";
                else if (i == size - 1) imgName = "ship_right.png";
                else imgName = "ship_mid_hor.png";
            }

            JButton[][] grid = isEnemy ? enemyGridButtons : playerGridButtons;
            ImageIcon icon = getShipIcon(imgName);
            
            grid[p.x][p.y].setIcon(icon);
            grid[p.x][p.y].setDisabledIcon(icon); 
            
            grid[p.x][p.y].setContentAreaFilled(false);
            grid[p.x][p.y].setBorderPainted(false); 
            
            grid[p.x][p.y].setEnabled(false);
        }
        refreshView();
    }

    /**
     * Updates a cell visually for hit/miss results.
     */
    public void updateCell(boolean isEnemy, int x, int y, Color c, String t) {
        JButton[][] g = isEnemy ? enemyGridButtons : playerGridButtons;
        g[x][y].setBackground(c);
        g[x][y].setIcon(null);
        g[x][y].setText(""); 
        
        g[x][y].setContentAreaFilled(true);

        if (!t.isEmpty()) g[x][y].setEnabled(false);
    }

    /**
     * Marks a cell as a non-targetable buffer zone.
     */
    public void disableSmartCell(boolean isEnemy, int x, int y) {
        JButton[][] grid = isEnemy ? enemyGridButtons : playerGridButtons;
        grid[x][y].setBackground(new Color(200, 200, 200)); 
        grid[x][y].setIcon(null);
        grid[x][y].setEnabled(false); 
    }

    public void refreshView() { revalidate(); repaint(); }
    
    /**
     * Transitions the UI to the battle phase.
     */
    public void switchToPlayMode() {
        southLayout.show(southContainer, "RESULT");
        resultPanel.setVisible(false);
        statusLabel.setText("BATTAGLIA! Fuoco al nemico.");
        refreshView();
    }

    public void setGridListener(boolean isEnemy, int x, int y, ActionListener l) {
        if (isEnemy) enemyGridButtons[x][y].addActionListener(l);
        else playerGridButtons[x][y].addActionListener(l);
    }

    /**
     * Resets both grids to their default state.
     */
    public void resetGrids() {
        for (int i = 0; i < playerGridButtons.length; i++) {
            for (int j = 0; j < playerGridButtons[i].length; j++) {
                resetButton(playerGridButtons[i][j]);
                resetButton(enemyGridButtons[i][j]);
            }
        }
        refreshView();
    }

    /**
     * Reverts a single button to sea state.
     */
    private void resetButton(JButton btn) {
        btn.setBackground(SEA_COLOR);
        btn.setText("");
        btn.setIcon(null);
        btn.setDisabledIcon(null);
        btn.setEnabled(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(true);
        btn.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,50)));
    }

    /**
     * Creates a titled grid panel.
     */
    private JButton[][] createGrid(JPanel parent, int w, int h, String title) {
        JPanel container = new JPanel(new BorderLayout());
        JLabel l = new JLabel(title, JLabel.CENTER);
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        container.add(l, BorderLayout.NORTH);
        JPanel gridBody = new JPanel(new GridLayout(w, h));
        JButton[][] btns = new JButton[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                btns[i][j] = new JButton();
                btns[i][j].setPreferredSize(new Dimension(40, 40));
                btns[i][j].setBackground(SEA_COLOR);
                btns[i][j].setBorder(BorderFactory.createLineBorder(new Color(255,255,255,50)));
                gridBody.add(btns[i][j]);
            }
        }
        container.add(gridBody, BorderLayout.CENTER);
        parent.add(container);
        return btns;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    /**
     * Displays the game winner.
     */
    public void showResults(String winnerName) {
        resultPanel.setVisible(true);
        resultLabel.setText("PARTITA FINITA! Vincitore: " + winnerName);
        statusLabel.setText("Resoconto Battaglia");
        refreshView();
    }

    public boolean isHorizontal() { return horizontalRadio.isSelected(); }
    public void setStatus(String text) { statusLabel.setText(text); }
    public void setMenuListener(ActionListener l) { globalMenuBtn.addActionListener(l); }
    public void setResetPlacementListener(ActionListener l) { resetPlacementBtn.addActionListener(l); }
    public void setRestartListener(ActionListener l) { endRestartBtn.addActionListener(l); }
}