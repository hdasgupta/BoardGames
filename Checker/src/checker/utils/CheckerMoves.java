package checker.utils;

import board.model.Move;
import board.utils.Moves;
import checker.model.CheckerBoard;
import checker.model.CheckerChanges;
import checker.model.Checker.Jump;

public class CheckerMoves extends Moves<CheckerBoard, CheckerChanges, Jump, StateId> {

	
	@Override
	protected CheckerChanges change(Jump j) {
		return j.doJump();
	}

	@Override
	protected void undo(CheckerChanges c) {
		c.getForMove().undo(c);
	}


}
