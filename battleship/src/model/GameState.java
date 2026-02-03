package model;

import java.awt.Point;
//import java.util.HashSet;
//import java.util.Iterator;
import java.util.List;
//import java.util.Optional;
//import java.util.Set;

public class GameState {

	private final Player humanPlayer;
	private final Player aiPlayer;
	//private Player currentPlayer;
	
	public GameState(Player humanPlayer, Player aiPlayer) {
		this.humanPlayer = humanPlayer;
		this.aiPlayer = aiPlayer;
		//this.currentPlayer = humanPlayer; //parte prima la persona
	}
	
    /*public Player getCurrentPlayer() {
        return this.currentPlayer;
    }*/
	
    public Player getOpponent(Player player) {
        return player == this.humanPlayer ? this.aiPlayer : this.humanPlayer;
    }
    
    public Grid getEnemyGrid(Player player) {
        return this.getOpponent(player).getGrid();
    }
	
    
    // --- ELABORAZIONE MOSSA ---
    public MoveResult gameMove(Player player, Point point) {
    	try {
            Grid enemyGrid = getEnemyGrid(player);
            Cell cell = enemyGrid.getCell(point.x, point.y);

            CellState state = cell.fire(); // può lanciare eccezione

            if (state == CellState.MISS) {
                return MoveResult.MISS;
            }

            Ship ship = cell.getShip();
            if (ship.isSunk()) {
                return MoveResult.SUNK;
            }

            return MoveResult.HIT_SHIP;

        } catch (IllegalStateException e) {
            // mossa non valida (cella già colpita)
            throw e; // il Controller decide cosa fare
        }
    }

    
    public boolean isGameOver() {
        return this.humanPlayer.getGrid().allShipsSunk()
            || aiPlayer.getGrid().allShipsSunk();
    }

    public Player getWinner() {
        if (!isGameOver()) return null;
        return humanPlayer.getGrid().allShipsSunk() ? aiPlayer : humanPlayer;
    }


	
	
/*
	// elaborazione di una mossa
	public MoveResult gameMove(Player player, Point point) {
		MoveResult result = MoveResult.MISS;

		Grid eGrid = getEnemyGrid(player);

		List<Ship> ships = eGrid.getShips();
		
		/*
		Ship ship = ships.stream().filter(x -> x.getPositions().stream().anyMatch(t -> t.getCoordinates() == point)).findFirst().get();
		if(!(ship == null)) {
			Cell p = ship.getPositions().stream().filter(c -> c.getCoordinates() == point).findFirst().get();
			ship.increaseHits();
			p.setState(CellState.HIT);
			eGrid.getCell(point.x, point.y).setState(CellState.HIT);
			if (ship.getHits() == ship.getShipConfig().getSize()) {
				return result = MoveResult.SUNK;
			} else {
				return result = MoveResult.HIT_SHIP;
			}
		}else {
			eGrid.getCell(point.x, point.y).setState(CellState.MISS);
		}
		*/
		
		/*for (Ship s : ships) {
			for (Cell c : s.getPositions()) {
				if (c.getCoordinates().equals(point)) {
					// nave colpita/affondata ?
					s.hit();
					c.setState(CellState.HIT);  								// segna colpita la parte della nave
					eGrid.getCell(point.x, point.y).setState(CellState.HIT);	// segna colpito il punto sulla Grid
					if (s.getHits() == s.getConfig().getSize()) {
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
	}*/


}
