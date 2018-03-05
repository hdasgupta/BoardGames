package tictactoe.model;

import board.model.Move;

public class TicTacToeMove implements Move {
	public final int index;
	
	public TicTacToeMove(int index) {
		this.index = index;
	}
}
