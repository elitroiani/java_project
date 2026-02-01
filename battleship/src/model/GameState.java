package model;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GameState {

	private Player humanPlayer;
	private Player aiPlayer;

	public GameState(Player humanPlayer, Player aiPlayer) {
		this.humanPlayer = humanPlayer;
		this.aiPlayer = aiPlayer;
	}

	public Grid getEnemyGrid(Player player) {
		if (player instanceof HumanPlayer) {
			return aiPlayer.getGrid();
		} else {
			return humanPlayer.getGrid();
		}
	}

	// elaborazione di una mossa
	public MoveResult gameMove(Player player, Point point) {
		MoveResult result = MoveResult.MISS;

		Grid eGrid = getEnemyGrid(player);

		List<Ship> ships = eGrid.getShips();
		for (Ship s : ships) {
			for (Cell c : s.getPositions()) {
				if (c.getCoordinates().equals(point)) {
					// nave colpita/affondata ?
					s.increaseHits();
					c.setState(CellState.HIT);
					eGrid.getCell(point.x, point.y).setState(CellState.HIT);
					if (s.getHits() == s.getShipConfig().getSize()) {
						return result = MoveResult.SUNK;
					} else {
						return result = MoveResult.HIT_SHIP;
					}
				}
			}

		}
		if (result == MoveResult.MISS) {
			eGrid.getCell(point.x, point.y).setState(CellState.MISS);
		}

		return result;
	}

}
