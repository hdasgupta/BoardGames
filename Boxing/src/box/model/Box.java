package box.model;
import static box.utils.Constants.*;

import box.utils.Player;

public class Box {

	public final int row, column;
	private Player owner = Player.None;
	
	private Box(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public void setOwner(Player p) {
		owner = p;
	}
	
	public void reset() {
		owner = Player.None;
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
