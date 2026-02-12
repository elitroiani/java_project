package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Configurazione di gioco: dimensioni griglia e tipi di nave
 */
public class GameConfig {

    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;

    private final List<ShipConfig> shipTypes;

    public GameConfig() {
        // Configurazione standard navi
        shipTypes = new ArrayList<>();

        // esempio classico di battaglia navale
        shipTypes.add(new ShipConfig("Portaerei", 5, 1));
        shipTypes.add(new ShipConfig("Corazzata", 4, 1));
        shipTypes.add(new ShipConfig("Incrociatore", 3, 1));
        shipTypes.add(new ShipConfig("Sottomarino", 3, 1));
        shipTypes.add(new ShipConfig("Cacciatorpediniere", 2, 1));
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    /**
     * Restituisce la lista dei tipi di nave con size e count
     */
    public List<ShipConfig> getShipTypes() {
        return new ArrayList<>(shipTypes); // restituisce copia per sicurezza
    }

	public int getGridSize() {
		return this.WIDTH;
	}
}
