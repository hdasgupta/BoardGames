package box.model;
import board.model.Changes;
import board.utils.Moves;
import box.model.Box;
import box.model.BoxChanges;
import box.model.Line;
import box.model.StateIdentifier;
import box.threads.BoxBoard;

public class BoxMoves extends Moves<BoxBoard, BoxChanges, Line, StateIdentifier> {

	@Override
	protected BoxChanges change(Line l) {
		Box b[] = board.setLine(l);
		for(Box box:b) {
			box.setOwner(board.getCurrentTurn());
		}
		BoxChanges c = new  BoxChanges(board.getCurrentTurn(), l, b);
		board.setCurrentTurn(c.getNextTurn());
		return c;
	}

	@Override
	protected void undo(BoxChanges c) {
		c.getForMove().reset();
		for(Box box:c.occupied) {
			box.reset();
		}
		board.setCurrentTurn(c.getCurrentTurn());
	}

}
