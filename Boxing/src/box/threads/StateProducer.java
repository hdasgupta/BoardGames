package box.threads;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.jms.JMSException;

import box.model.Box;
import box.model.Line;
import box.model.StateIdentifier;
import box.model.StateProbability;
import box.utils.Constants;
import box.utils.LineType;
import box.utils.Player;

public class StateProducer implements Runnable {

	public final int dots_per_row_column;
	public final int TOTAL_DOTS;
	
	public final int NUMBER_OF_ROWS;
	public final int NUMBER_OF_COLUMNS;
	public final int NUMBER_OF_VERTICAL_LINE_ROWS;
	public final int NUMBER_OF_HORIZONTAL_LINES_PER_ROW;
	public final int NUMBER_OF_VERTICAL_LINES_PER_COLUMN;
	public final int NUMBER_OF_HORIZNTAL_LINES;
	public final int NUMBER_OF_VERTICAL_LINES;
	public final int TOTAL_LINES;


	private final BigInteger linesMask[];

	private Player current = Player.Player1;

	private final LinkedList<Line> ALL_LINES = new LinkedList<Line>();
	private final LinkedList<Box> ALL_BOXES = new LinkedList<Box>();

	public final int NUMBER_OF_BOX_PER_ROW;
	public final int NUMBER_OF_BOX_PER_COLUMN;
	public final int TOTAL_BOX;
	
	public StateProducer() {
		this.dots_per_row_column = Constants.DIMENSION;

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

		linesMask = new BigInteger[TOTAL_LINES];
		// ALL_LINES = new Integer[TOTAL_LINES];

		for (int row = 0; row < NUMBER_OF_ROWS; row++) {
			for (int column = 0; column < NUMBER_OF_HORIZONTAL_LINES_PER_ROW; column++) {
				ALL_LINES.add(Line.getLine(LineType.Horizontal, row, column));
				linesMask[i] = BigInteger.ONE.shiftLeft(i);
				i++;
			}
		}
		for (int row = 0; row < NUMBER_OF_VERTICAL_LINE_ROWS; row++) {
			for (int column = 0; column < NUMBER_OF_COLUMNS; column++) {
				ALL_LINES.add(Line.getLine(LineType.Vertical, row, column));
				linesMask[i] = BigInteger.ONE.shiftLeft(i);
				i++;
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

	public Player getBoxOwner(Box b) {
		return b.getOwner();
	}

	public void setBoxOwner(Box b) {
		b.setOwner(current);
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
		return StateIdentifier.value(ALL_LINES, ALL_BOXES);
	}

	/*private void createNewFiles() {
		try {
			if (statesFile != null) {
				statesFile.close();
			}

			System.out.println(fileIndex);
			File dir = new File(BASE_DIR + dots_per_row_column + BY + dots_per_row_column),
					stateDir = new File(dir, STATES), relationDir = new File(dir, RELATIONS);
			if (!stateDir.exists()) {
				stateDir.mkdirs();
			}
			if (!relationDir.exists()) {
				relationDir.mkdirs();
			}
			statesFile = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(stateDir, fileIndex + SQL_EXT), false)));

			if (relationsFile != null) {
				relationsFile.close();
			}
			relationsFile = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(relationDir, fileIndex + SQL_EXT), false)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		fileIndex++;
	}*/

	public void run() {
		
		try {
			startCalc(new LinkedList<Line>(ALL_LINES), current);
		} catch (IOException e) {
			e.printStackTrace();
		} catch(JMSException e) {
			e.printStackTrace();
		} catch(SQLException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();
		} finally {
			/*StateProbability.waitToEnqueue();*/
		}
		
	}

	public boolean gameOver() {
		for(Box b:ALL_BOXES) {
			if(b.getOwner() == Player.None) {
				return false;
			}
		}
		return true;
	}

	public long player1OwnedBoxes() {
		int count = 0;
		for(Box b:ALL_BOXES) {
			if(b.getOwner() == Player.Player1) {
				count++;
			}
		}
		return count;
	}

	public long player2OwnedBoxes() {
		int count = 0;
		for(Box b:ALL_BOXES) {
			if(b.getOwner() == Player.Player2) {
				count++;
			}
		}
		return count;
	}

	public StateProbability startCalc(LinkedList<Line> pendingLines, Player current) throws IOException, InterruptedException, SQLException, JMSException {
		long id[] = id();
		if(StateProbability.exists(id)) {
			return StateProbability.getProbability(id);
		} else if (pendingLines.isEmpty()) {
			// System.out.println("Gameover");
			float prob1 = (float) player1OwnedBoxes() / TOTAL_BOX;
			float prob2 = (float) player2OwnedBoxes() / TOTAL_BOX;
			StateProbability sp = StateProbability.getProbability(id, prob1, prob2, new LinkedList<StateProbability>());
			return sp;
		} else {

			List<StateProbability> probs = new LinkedList<StateProbability>();
			LinkedList<Line> newPendingList = new LinkedList<Line>(pendingLines);
			float prob1 = 0;
			float prob2 = 0;
			
			for (int i = 0; i < pendingLines.size(); i++) {
				Line l = pendingLines.get(i);
				Box b[] = setLine(l);
				for (Box bb : b) {
					bb.setOwner(current);
				}
				// System.out.println(id()+", boxes: "+b.length+" player: "+current);
				newPendingList.remove(i);
				StateProbability p = startCalc(newPendingList,
						b.length == 0 ? (current == Player.Player1 ? Player.Player2 : Player.Player1) : current);
				
				prob1+=p.probabilityPlayer1;
				prob2+=p.probabilityPlayer2;
				
				probs.add(p);
				newPendingList.add(i, l);
				l.reset();
				for (Box bb : b) {
					bb.reset();
				}
			}

			
			prob1/=probs.size();
			prob2/=probs.size();
			StateProbability sp = StateProbability.getProbability(id, prob1, prob2, probs);
			return sp;
		}
	}

}
