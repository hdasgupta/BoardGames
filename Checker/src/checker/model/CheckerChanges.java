package checker.model;

import java.util.List;

import board.model.Changes;
import board.utils.Players;
import checker.model.Checker.Jump;
import checker.utils.Position;

public class CheckerChanges extends Changes<Jump> {
	public final Position initialPosition;
	public final List<Checker> occupiedList;
	public final boolean king;
	
	public CheckerChanges(Players currentPlayer, 
			Players nextPlayer,
			Jump jump,
			Position initialPosition, 
			List<Checker> occupiedList) {
		super(currentPlayer, nextPlayer, jump);
		this.initialPosition = initialPosition;
		this.occupiedList = occupiedList;
		king=false;
	}
	
	public CheckerChanges(Players currentPlayer, 
			Players nextPlayer,
			Jump jump,
			Position initialPosition, 
			List<Checker> occupiedList, boolean king) {
		super(currentPlayer, nextPlayer, jump);
		this.initialPosition = initialPosition;
		this.occupiedList = occupiedList;
		this.king=king;
	}
}
