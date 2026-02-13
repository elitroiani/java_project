package model;

/**
 * Represents the outcome of a shot: MISS (water), 
 * 									 HIT (ship segment), 
 * 									 SUNK (ship destroyed), 
 * 									 or ALREADY_FIRED (invalid move).
 */
public enum MoveResult {
    MISS, HIT, SUNK, ALREADY_FIRED
}
