package board.model;

import board.utils.Players;

public abstract class Changes<M extends Move> {
	protected final Players nextTurn, currentTurn;
	protected final M forMove;
	
	public Changes(Players currentTurn, Players nextTurn, M forMove) {
		this.currentTurn = currentTurn;
		this.nextTurn = nextTurn;
		this.forMove = forMove;
	}
	
	public Players getCurrentTurn() {
		return currentTurn;
	}
	
	public Players getNextTurn() {
		return nextTurn;
	}
	
	public M getForMove() {
		return forMove;
	}
}
