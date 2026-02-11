package view;

import javax.swing.*;
import java.awt.*;

public class GridPanel extends JPanel {

    private final CellButton[][] buttons;

    public GridPanel(int width, int height, boolean isPlayerGrid) {

        setLayout(new GridLayout(height, width));
        buttons = new CellButton[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                CellButton btn = new CellButton(x, y);
                buttons[x][y] = btn;
                add(btn);
            }
        }

        setBorder(BorderFactory.createTitledBorder(
                isPlayerGrid ? "Tua Griglia" : "Griglia Nemica"
        ));
    }

    public CellButton getButton(int x, int y) {
        return buttons[x][y];
    }
}