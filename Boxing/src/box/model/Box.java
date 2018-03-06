package box.model;

import java.util.LinkedList;

import board.utils.Players;

public class Box {

	private static final LinkedList<Box> CACHE = new LinkedList<Box>();
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
		for(Box b:CACHE) {
			if(b.row==row&&b.column==column) {
				return b;
			}
		}
		Box b = new Box(row, column);
		CACHE.add(b);
		return b;
	}

}
