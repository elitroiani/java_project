package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class StartView extends JFrame {
    private JButton btnEasy, btnMedium, btnHard, btnExpert;

    public StartView() {
        setTitle("Battaglia Navale - Seleziona Difficoltà");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Pannello Titolo
        JLabel title = new JLabel("BATTAGLIA NAVALE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Pannello Pulsanti
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 50));

        btnEasy = createStyledButton("FACILE (Casuale)", new Color(46, 204, 113));
        btnMedium = createStyledButton("MEDIO (Standard)", new Color(52, 152, 219));
        btnHard = createStyledButton("DIFFICILE (Mirato)", new Color(230, 126, 34));
        btnExpert = createStyledButton("ESPERTO (Probabilità)", new Color(231, 76, 60));

        buttonPanel.add(btnEasy);
        buttonPanel.add(btnMedium);
        buttonPanel.add(btnHard);
        buttonPanel.add(btnExpert);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    public void setDifficultyListener(String difficulty, ActionListener l) {
        switch (difficulty.toLowerCase()) {
            case "easy" -> btnEasy.addActionListener(l);
            case "medium" -> btnMedium.addActionListener(l);
            case "hard" -> btnHard.addActionListener(l);
            case "expert" -> btnExpert.addActionListener(l);
        }
    }
}