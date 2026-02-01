package model;

import java.awt.Point;

public class GameState {
	
	public Player humanPlayer;
	public Player aiPlayer;
	
	public GameState(Player humanPlayer, Player aiPlayer) {
		this.humanPlayer = humanPlayer;
		this.aiPlayer = aiPlayer;
	}


	public Grid getEnemyGrid(Player player) {
		if(player instanceof HumanPlayer) {
			return aiPlayer.getGrid();
		}else {
			return humanPlayer.getGrid();
		}
	}

	
	//elaborazione di una mossa
	public MoveResult gameMove(Player player, Point point) {
		Grid eGrid = getEnemyGrid(player);
		
		
		
		return null;
		
	}
	
}
