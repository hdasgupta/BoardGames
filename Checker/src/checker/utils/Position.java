package checker.utils;

import board.utils.Players;
import checker.model.Checker;

public class Position {
	public final int row, column;
	private Checker owner = null;
	public Position(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	@Override
	public String toString() {
		return owner == null? "0" : (owner.getOwner() == Players.Player1?"1":"2");
	}
	
	public Checker getOwner() {
		return owner;
	}
	
	public int getOwnerSymbol() {
		return owner==null?0:owner.symbol();
	}
	
	public void setOwner(Checker owner) {
		this.owner = owner;
	}
}
