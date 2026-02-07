package ai;

import java.util.List;

import model.GameConfig;
import model.Ship;

public class ManualShipPlacer extends AbstractShipPlacer {



	public ManualShipPlacer(GameConfig config) {
         super(config);
    }

    @Override
    public List<Ship> placeShips(GameConfig config) {
        return this.ships; // già valide
    }

	@Override
	protected Ship createShip(int size, String type) {
		// TODO Auto-generated method stub
		return null;
	}
}
