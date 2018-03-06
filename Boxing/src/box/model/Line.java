package box.model;

import java.util.LinkedList;

import board.model.Move;
import box.utils.LineType;

public class Line implements Move {
	
	private static final LinkedList<Line> CACHE = new LinkedList<Line>();
	
		public final int row, column;
	public final LineType type;
	private boolean hasLine = false;
	
	private Line(LineType type, int row, int column) {
		this.type =type;
		this.row = row;
		this.column = column;
	}
	
	public String toString() {
		return hasLine?"0":"1";
	}
	
	public boolean hasLine() {
		return hasLine;
	}
	
	public void setLine() {
		hasLine = true;
	}
	
	public void reset() {
		hasLine=false;
	}
	public static Line getLine(LineType type, int row, int column) {
		for(Line l:CACHE) {
			if(l.type==type
					&&l.row==row
					&&l.column==column) {
				return l;
			}
		}
		Line l = new Line(type, row, column);
		CACHE.add(l);
		return l;
	}
}
