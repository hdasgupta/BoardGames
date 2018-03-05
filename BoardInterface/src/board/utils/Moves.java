package board.utils;

import java.util.Stack;

import board.model.Board;
import board.model.Changes;
import board.model.Move;

public abstract class Moves<B extends Board, C extends Changes<Mv>, Mv extends Move, I extends StateIdentifications> {
	
	protected final Stack<C> CHANGE_STACK = new Stack<>();
	protected B board;
	
	public void setBoard(B board) {
		this.board = board;
	}
	
	public  Players doMoves(Mv m) {
		C c = change(m);
		CHANGE_STACK.push(c);
		return c.getNextTurn();
	}
	
	protected  abstract C change(Mv m);
	protected abstract void undo(C c);
	
	public void undoMove() {
		C c = CHANGE_STACK.pop();
		undo(c);
		board.setCurrentTurn(c.getCurrentTurn());
	}
}
