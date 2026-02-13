package view;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;

public class CellView extends JButton {
    // Rimosso il riferimento al Model per puro MVC
    
    public CellView() {
        this.setPreferredSize(new Dimension(50, 50));
        this.setBackground(Color.BLUE); // Stato iniziale
    }

    /**
     * Il Controller chiamerà questo metodo passando i dati necessari
     */
    public void displayState(Color color, String text, boolean active) {
        this.setBackground(color);
        this.setText(text);
        this.setEnabled(active);
    }
}
