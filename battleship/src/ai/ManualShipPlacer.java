package ai;

import java.util.List;

import model.GameConfig;
import model.Grid;
import model.Ship;

public class ManualShipPlacer extends AbstractShipPlacer {



	public ManualShipPlacer(GameConfig config) {
         super(config);
    }

    @Override
    public List<Ship> placeShips(GameConfig config) {
        return this.placeShips(config); // già valide
    }

	@Override
	protected Ship createShip(int size, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getStartX(Grid grid, Ship ship) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getStartY(Grid grid, Ship ship) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected boolean isHorizontal(Grid grid, Ship ship) {
		// TODO Auto-generated method stub
		return false;
	}
}
