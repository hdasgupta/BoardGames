package tictactoe.utils;

import board.utils.BitUtils;
import board.utils.Moves;
import board.utils.Players;
import tictactoe.model.TicTacToeBoard;
import tictactoe.model.TicTacToeChanges;
import tictactoe.model.TicTacToeMove;

public class TicTacToeMoves extends Moves<TicTacToeBoard, TicTacToeChanges, TicTacToeMove, TicTacToeStateId> {

	@Override
	protected TicTacToeChanges change(TicTacToeMove m) {
		long oldValue = board.id()[0];
		long newValue = BitUtils.setVal(oldValue, m.index, 
				board.getCurrentTurn()==Players.Player1?1:2, 3); 
		Players turn = board.getCurrentTurn()==Players.Player1?Players.Player2:Players.Player1;
		board.value[0] = newValue;
		board.setCurrentTurn(turn);
		return new TicTacToeChanges(board.getCurrentTurn(),
				turn, m, oldValue, newValue);
	}

	@Override
	protected void undo(TicTacToeChanges c) {
		board.value[0] = c.oldValue;
		board.setCurrentTurn(c.getCurrentTurn());
		
	}

}
