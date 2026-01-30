package ai;

import model.GameState;

import java.awt.Point;


public interface Reasoner {
	Point chooseMove(GameState state);
}
