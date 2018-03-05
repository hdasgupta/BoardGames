package box.threads;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import board.model.Board;
import board.model.Changes;
import board.utils.BitUtils;
import board.utils.Players;
import box.model.Box;
import box.model.BoxChanges;
import box.model.BoxMoves;
import box.model.Line;
import box.model.StateIdentifier;
import box.utils.Constants;
import box.utils.LineType;

public class BoxBoard extends Board<StateIdentifier, BoxMoves, BoxChanges, Line> {

	private int dots_per_row_column;
	private int TOTAL_DOTS;
	
	private int NUMBER_OF_ROWS;
	private int NUMBER_OF_COLUMNS;
	private int NUMBER_OF_VERTICAL_LINE_ROWS;
	private int NUMBER_OF_HORIZONTAL_LINES_PER_ROW;
	private int NUMBER_OF_VERTICAL_LINES_PER_COLUMN;
	private int NUMBER_OF_HORIZNTAL_LINES;
	private int NUMBER_OF_VERTICAL_LINES;
	private int TOTAL_LINES;


	private Players current = Players.Player1;

	private final LinkedList<Line> ALL_LINES = new LinkedList<Line>();
	private final LinkedList<Box> ALL_BOXES = new LinkedList<Box>();

	private int NUMBER_OF_BOX_PER_ROW;
	private int NUMBER_OF_BOX_PER_COLUMN;
	private int TOTAL_BOX;

	/*
	 * public int getLineIndex(int line) { LineType type = Line.type(line);
	 * if(type==LineType.Horizontal) { return
	 * Line.row(line)*NUMBER_OF_HORIZONTAL_LINES_PER_ROW +Line.column(line); } else
	 * { return NUMBER_OF_HORIZNTAL_LINES+ Line.row(line)*NUMBER_OF_COLUMNS+
	 * Line.column(line); } }
	 */

	public Box[] adjucentBoxes(Line l) {
		if (l.type == LineType.Horizontal) {
			if (l.row > 0 && l.row < (NUMBER_OF_HORIZONTAL_LINES_PER_ROW - 1)) {
				return new Box[] { Box.getBox(l.row - 1, l.column), Box.getBox(l.row, l.column) };
			} else if (l.row == 0) {
				return new Box[] { Box.getBox(0, l.column) };
			} else {
				return new Box[] { Box.getBox(l.row - 1, l.column) };
			}
		} else {
			if (l.column > 0 && l.column < (NUMBER_OF_COLUMNS - 1)) {
				return new Box[] { Box.getBox(l.row, l.column - 1), Box.getBox(l.row, l.column) };
			} else if (l.column == 0) {
				return new Box[] { Box.getBox(l.row, 0) };
			} else {
				return new Box[] { Box.getBox(l.row, l.column - 1) };
			}
		}
	}

	public Line[] adjucentLines(Box b) {
		Line[] ls = new Line[] { Line.getLine(LineType.Horizontal, b.row, b.column),
				Line.getLine(LineType.Horizontal, b.row + 1, b.column),
				Line.getLine(LineType.Vertical, b.row, b.column),
				Line.getLine(LineType.Vertical, b.row, b.column + 1), };
		// System.out.println(Arrays.toString(ls));
		return ls;
	}

	public boolean isBoxOccupied(Box b) {
		for (Line l : adjucentLines(b)) {
			if (!l.hasLine()) {
				return false;
			}
		}
		return true;
	}

	public Box[] setLine(Line l) {
		LinkedList<Box> notPreOccupied = new LinkedList<Box>();
		LinkedList<Box> newlyOccupied = new LinkedList<Box>();

		for (Box b : adjucentBoxes(l)) {
			if (!isBoxOccupied(b)) {
				notPreOccupied.add(b);
			}
		}
		// System.out.println("notPreoccupied: "+notPreOccupied.size());
		l.setLine();

		for (Box b : notPreOccupied) {
			if (isBoxOccupied(b)) {
				newlyOccupied.add(b);
			}
		}

		return newlyOccupied.toArray(new Box[newlyOccupied.size()]);
	}

	public long[] id() {
		long val[] = new long[] {0l,0l,0l};
		int index =0;
		for(Line l:ALL_LINES) {
			if(l.hasLine()) {
				if(index<NUMBER_OF_HORIZNTAL_LINES) {
					val[0] = BitUtils.setVal(val[0], index);
				} else {
					val[1] = BitUtils.setVal(val[1], index);
				}
			}
			index++;
		}
		index=0;
		for(Box b:ALL_BOXES) {
			if(b.getOwner()==Players.Player2) {
				val[2] = BitUtils.setVal(val[2], index);
			}
			index++;
		}
		return val;
	}


	@Override
	public void init() {
		TOTAL_DOTS = dots_per_row_column;
		NUMBER_OF_ROWS = dots_per_row_column;
		NUMBER_OF_COLUMNS = dots_per_row_column;
		NUMBER_OF_VERTICAL_LINE_ROWS=NUMBER_OF_ROWS-1;
		NUMBER_OF_HORIZONTAL_LINES_PER_ROW = NUMBER_OF_ROWS - 1;
		NUMBER_OF_VERTICAL_LINES_PER_COLUMN = NUMBER_OF_COLUMNS - 1;
		NUMBER_OF_HORIZNTAL_LINES = NUMBER_OF_HORIZONTAL_LINES_PER_ROW * NUMBER_OF_ROWS;
		NUMBER_OF_VERTICAL_LINES = NUMBER_OF_VERTICAL_LINES_PER_COLUMN * NUMBER_OF_COLUMNS;
		TOTAL_LINES = NUMBER_OF_HORIZNTAL_LINES + NUMBER_OF_VERTICAL_LINES;

		int i = 0;

		for (int row = 0; row < NUMBER_OF_ROWS; row++) {
			for (int column = 0; column < NUMBER_OF_HORIZONTAL_LINES_PER_ROW; column++) {
				ALL_LINES.add(Line.getLine(LineType.Horizontal, row, column));
			}
		}
		for (int row = 0; row < NUMBER_OF_VERTICAL_LINE_ROWS; row++) {
			for (int column = 0; column < NUMBER_OF_COLUMNS; column++) {
				ALL_LINES.add(Line.getLine(LineType.Vertical, row, column));
			}
		}
		NUMBER_OF_BOX_PER_ROW = NUMBER_OF_HORIZONTAL_LINES_PER_ROW;
		NUMBER_OF_BOX_PER_COLUMN = NUMBER_OF_VERTICAL_LINES_PER_COLUMN;
		TOTAL_BOX = NUMBER_OF_BOX_PER_ROW * NUMBER_OF_BOX_PER_COLUMN;

		for (int row = 0; row < NUMBER_OF_BOX_PER_ROW; row++) {
			for (int column = 0; column < NUMBER_OF_BOX_PER_COLUMN; column++) {
				ALL_BOXES.add(Box.getBox(row, column));
			}
		}

	}

	@Override
	public boolean isGameOver() {
		for(Box b:ALL_BOXES) {
			if(b.getOwner() == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public float[] getWinner() {
		if(isGameOver()) {
			float count1 = 0, count2 = 0;
			for(Box b:ALL_BOXES) {
				if(b.getOwner() == Players.Player1) {
					count1++;
				} else {
					count2++;
				}
			}
			return new float[]{count1/TOTAL_BOX, count2/TOTAL_BOX};
		}
		return BitUtils.getDrawnProbability();
	}

	@Override
	public List<Line> getAllPossibleMoves(Players player) {
		LinkedList<Line> lines = new LinkedList<Line>();
		for(Line l:ALL_LINES) {
			if(!l.hasLine()) {
				lines.add(l);
			}
		}
		return lines;
	}

}
