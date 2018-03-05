package box.model;

import board.model.Changes;
import board.utils.Players;

public class BoxChanges extends Changes<Line> {
	
	public final Box occupied[];

	public BoxChanges(Players currentTurn, Line forMove, Box[] occupied) {
		super(currentTurn, occupied.length>0?currentTurn:
			(currentTurn==Players.Player1?Players.Player2:Players.Player1), 
			forMove);
		this.occupied = occupied;
	}

}
