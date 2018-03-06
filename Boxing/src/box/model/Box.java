package box.model;
import static box.utils.Constants.*;

import board.utils.Players;

public class Box {

	public final int row, column;
	private Players owner = null;
	
	private Box(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	public Players getOwner() {
		return owner;
	}
	
	public void setOwner(Players p) {
		owner = p;
	}
	
	public void reset() {
		owner = null;
	}
	
	@Override
	public String toString() {
		return owner.toString();
	}
	
	public static Box getBox(int row, int column) {
		for(Box b:BOX_LIST) {
			if(b.row==row&&b.column==column) {
				return b;
			}
		}
		Box b = new Box(row, column);
		BOX_LIST.add(b);
		return b;
	}

}
