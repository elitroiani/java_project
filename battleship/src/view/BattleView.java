package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BattleView extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // Componenti Griglia
    private JButton[][] playerGridButtons;
    private JButton[][] enemyGridButtons;
    
    // Componenti Pannello Superiore
    private JLabel statusLabel;
    private JButton globalMenuBtn; // Tasto "Menu" sempre visibile
    
    // Componenti Setup
    private JRadioButton horizontalRadio;
    private JPanel setupPanel;
    private JButton resetPlacementBtn;
    
    // Componenti Risultati (End Game)
    private JPanel resultPanel;
    private JLabel resultLabel;
    private JButton endRestartBtn; // Tasto nel pannello finale

    public BattleView(int width, int height) {
        setTitle("Battaglia Navale - Comandante in Capo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- 1. ZONA NORD (Status + Tasto Menu) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        statusLabel = new JLabel("Fase di Setup: Piazza le tue navi", JLabel.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        globalMenuBtn = new JButton("Menu");
        globalMenuBtn.setBackground(new Color(255, 140, 0)); // Arancione
        globalMenuBtn.setForeground(Color.WHITE);
        globalMenuBtn.setFocusPainted(false);
        globalMenuBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        topPanel.add(statusLabel, BorderLayout.CENTER);
        topPanel.add(globalMenuBtn, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);

        // --- 2. ZONA CENTRALE (Griglie) ---
        JPanel gridContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        playerGridButtons = createGrid(gridContainer, width, height, "LA TUA FLOTTA");
        enemyGridButtons = createGrid(gridContainer, width, height, "RADAR NEMICO");
        add(gridContainer, BorderLayout.CENTER);

        // --- 3. ZONA SUD (Setup o Risultati) ---
        JPanel southContainer = new JPanel(new CardLayout());
        
        // Pannello A: SETUP
        setupPanel = new JPanel();
        horizontalRadio = new JRadioButton("Verticale", true);
        JRadioButton verticalRadio = new JRadioButton("Orizzontale");
        ButtonGroup group = new ButtonGroup();
        group.add(horizontalRadio); group.add(verticalRadio);
        
        resetPlacementBtn = new JButton("Reset Griglia");
        resetPlacementBtn.setBackground(new Color(255, 102, 102)); // Rosso chiaro

        setupPanel.add(new JLabel("Orientamento: "));
        setupPanel.add(horizontalRadio);
        setupPanel.add(verticalRadio);
        setupPanel.add(new JSeparator(SwingConstants.VERTICAL));
        setupPanel.add(resetPlacementBtn);
        
        // Pannello B: RISULTATI
        resultPanel = new JPanel();
        resultPanel.setBackground(new Color(230, 230, 230));
        resultLabel = new JLabel("");
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        endRestartBtn = new JButton("TORNA AL MENU");
        endRestartBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        endRestartBtn.setBackground(new Color(52, 152, 219)); // Blu
        endRestartBtn.setForeground(Color.WHITE);
        
        resultPanel.add(resultLabel);
        resultPanel.add(endRestartBtn);
        resultPanel.setVisible(false);

        southContainer.add(setupPanel, "SETUP");
        southContainer.add(resultPanel, "RESULT");
        add(southContainer, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    // --- LISTENER (Metodi per il Controller) ---

    // 1. Tasto "Menu" in alto a destra
    public void setMenuListener(ActionListener l) {
        globalMenuBtn.addActionListener(l);
    }

    // 2. Tasto "Reset Griglia" durante il setup
    public void setResetPlacementListener(ActionListener l) {
        resetPlacementBtn.addActionListener(l);
    }

    // 3. Tasto "Torna al Menu" a fine partita
    public void setRestartListener(ActionListener l) {
        endRestartBtn.addActionListener(l);
    }

    // 4. Listener per le celle
    public void setPlayerListener(int x, int y, ActionListener l) { playerGridButtons[x][y].addActionListener(l); }
    public void setEnemyListener(int x, int y, ActionListener l) { enemyGridButtons[x][y].addActionListener(l); }

    // --- GESTIONE SCHERMATE ---

    public void showResults(String winnerName) {
        setupPanel.setVisible(false);
        resultPanel.setVisible(true);
        resultLabel.setText("PARTITA FINITA! Vincitore: " + winnerName);
        statusLabel.setText("Resoconto Battaglia");
        disableEnemyGrid();
    }

    public void showSetup() {
        resultPanel.setVisible(false);
        setupPanel.setVisible(true);
        statusLabel.setText("Fase di Setup: Piazza le tue navi");
        enableAllGrids();
    }

    // --- UTILITÀ GRAFICHE ---

    public void resetGrids() {
        Color seaColor = new Color(30, 144, 255);
        for (int i = 0; i < playerGridButtons.length; i++) {
            for (int j = 0; j < playerGridButtons[i].length; j++) {
                // Resetta player
                playerGridButtons[i][j].setBackground(seaColor);
                playerGridButtons[i][j].setText("");
                playerGridButtons[i][j].setEnabled(true);
                
                // Resetta enemy
                enemyGridButtons[i][j].setBackground(seaColor);
                enemyGridButtons[i][j].setText("");
                enemyGridButtons[i][j].setEnabled(true);
            }
        }
    }

    private void enableAllGrids() {
        for (int i = 0; i < playerGridButtons.length; i++) {
            for (int j = 0; j < playerGridButtons[i].length; j++) {
                playerGridButtons[i][j].setEnabled(true);
                enemyGridButtons[i][j].setEnabled(true);
            }
        }
    }

    public void disableEnemyGrid() {
        for (JButton[] row : enemyGridButtons) {
            for (JButton btn : row) btn.setEnabled(false);
        }
    }

    public void enableEnemyGrid() {
        for (JButton[] row : enemyGridButtons) {
            for (JButton btn : row) {
                // Riabilita solo se è acqua (non ancora colpita)
                if (btn.getBackground().equals(new Color(30, 144, 255))) { 
                    btn.setEnabled(true);
                }
            }
        }
    }

    // --- HELPER DI CREAZIONE ---

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

    // Getters / Setters semplici
    public boolean isHorizontal() { return horizontalRadio.isSelected(); }
    public void setStatus(String text) { statusLabel.setText(text); }
    public void hideSetup() { setupPanel.setVisible(false); pack(); }

    public void updateCell(boolean isEnemy, int x, int y, Color c, String t) {
        JButton[][] g = isEnemy ? enemyGridButtons : playerGridButtons;
        g[x][y].setBackground(c);
        g[x][y].setText(t);
    }
}