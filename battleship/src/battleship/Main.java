package battleship;

import controller.BattleController;


/**
 * Entry point of the Battleship application.
 * This class handles the initial application bootstrapping, the transition between 
 * the main menu and the game session, and the dynamic injection of AI strategies.
 */
public class Main {

    public static void main(String[] args) {
    	BattleController b = new BattleController();
    	b.launchGame();
    }
}