package tictactoe.utils;

import java.util.LinkedList;
import java.util.List;

import board.utils.BitUtils;
import board.utils.Players;
import board.utils.StateIdentifications;

public class TicTacToeStateId extends StateIdentifications {

	public static enum AlterType {
        RotateClockwise,
        FlipVertically
    }
    
    
	@Override
	public String toStringId(long[] id) {
		return BitUtils.toString(id[0], 9, 3);
	}

    public long set(long value, int row, int column, Players currentPlayer) {
	    return BitUtils.setVal(value, 
	    		positionToIndex(row, column), 
	    		currentPlayer==Players.Player1?1:2, 3);
    }
    
    public int positionToIndex(int row, int column) {
        return (row*3+column);
    }
	
    public int get(long value, int row, int column) {
        int v = BitUtils.getVal(value, positionToIndex(row, column), 3);
        return v;
    }
    
    
	@Override
	public List<long[]> generateSimilar(long[] id) {
		LinkedList<long[]> similar = new LinkedList<>();
        Long current = id[0];
        for(int flipLoop=0;flipLoop<2;flipLoop++) {
            for(int rotateLoop=0;rotateLoop<4;rotateLoop++) {
                if(!exists(similar, current) && current!=id[0]) {
                    similar.add(new long[] {current});
                }
                current=alter(current, AlterType.RotateClockwise);
            }
            current=alter(current, AlterType.FlipVertically);
        }
        return similar;
	}
    public long alter(long value, AlterType alterType) {
        long val = 0;
        for(int row = 0;row < 3; row++) {
            for(int column = 0; column < 3; column++) {
                int newRow, newColumn;
                if(alterType==AlterType.RotateClockwise) {
                    newRow = (2-column);
                    newColumn = row;
                }
                else {
                    newRow = row;
                    newColumn = (2-column);
                }
                int newValue = get(row, column);
                val = BitUtils.setVal(value, positionToIndex(newRow, newColumn), newValue, 3);
            }
        }
        return val;
    }

    private boolean exists(LinkedList<long[]> list, long value) {
        for(long[] val:list) {
            if(val[0] == value) {
                return true;
            }
        }
        return false;
    }
    

}
