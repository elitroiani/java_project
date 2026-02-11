package view;

import javax.swing.*;
import java.awt.*;

public class CellButton extends JButton {

    private final int x;
    private final int y;

    public CellButton(int x, int y) {
        this.x = x;
        this.y = y;

        setPreferredSize(new Dimension(40, 40));
    }

    public int getGridX() {
        return x;
    }

    public int getGridY() {
        return y;
    }
}
