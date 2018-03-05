package box.model;
import static box.utils.Constants.LINE;
import static box.utils.Constants.LINE_LIST;
import static box.utils.Constants.NO_LINE;


import box.utils.LineType;

public class Line {
	
	public final int row, column;
	public final LineType type;
	private boolean hasLine = false;
	
	private Line(LineType type, int row, int column) {
		this.type =type;
		this.row = row;
		this.column = column;
	}
	
	public String toString() {
		return hasLine?LINE:NO_LINE;
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
		for(Line l:LINE_LIST) {
			if(l.type==type
					&&l.row==row
					&&l.column==column) {
				return l;
			}
		}
		Line l = new Line(type, row, column);
		LINE_LIST.add(l);
		return l;
	}
}
