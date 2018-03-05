package tictactoe.model;

import board.model.Changes;
import board.utils.Players;

public class TicTacToeChanges extends Changes<TicTacToeMove> {
	public final long oldValue, newValue;
	
	public TicTacToeChanges(Players currentTurn, Players nextTurn, 
			TicTacToeMove forMove, long oldValue, long newValue) {
		super(currentTurn, nextTurn, forMove);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
}
