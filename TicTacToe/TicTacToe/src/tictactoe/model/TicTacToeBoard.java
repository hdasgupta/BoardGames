package tictactoe.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import board.model.Board;
import board.utils.BitUtils;
import board.utils.Players;
import tictactoe.utils.TicTacToeMoves;
import tictactoe.utils.TicTacToeStateId;
import tictactoe.utils.TicTacToeStateId.AlterType;

/**
 *
 * @author 139739
 */
public class TicTacToeBoard extends Board<TicTacToeStateId, TicTacToeMoves, TicTacToeChanges, TicTacToeMove> {
    
    
    //private final static TreeSet<Long> VALUES = new TreeSet<>();
    
    public long[] value;
    //private boolean printed = false;
    //private final LinkedList<TicTacToeBoard> similarValues;

    public TicTacToeBoard() {
    	value = new long[]{0L};
        //this.similarValues=new LinkedList<>();
        
    }
     
    public Players getWinnerPlayer() {
        long current = value[0];
        for(int rotateLoop=0;rotateLoop<3;rotateLoop++) {
            if(probability.getSid().get(current, 0, 0)!=0 && 
                    ((probability.getSid().get(current, 0, 0) == probability.getSid().get(current, 1, 0) &&
                    probability.getSid().get(current, 1, 0) == probability.getSid().get(current, 2, 0)) ||
                    probability.getSid().get(current, 0, 0) == probability.getSid().get(current, 1, 1) &&
                    probability.getSid().get(current, 1, 1) == probability.getSid().get(current, 2, 2))) {
                return probability.getSid().get(current, 0, 0)==1?Players.Player1:Players.Player2;
            }
            else if(probability.getSid().get(current, 1, 0)!=0 && 
                    (probability.getSid().get(current, 1, 0) == probability.getSid().get(current, 1, 1) &&
                    probability.getSid().get(current, 1, 1) == probability.getSid().get(current, 1, 2))) {
                return probability.getSid().get(current, 1, 0)==1?Players.Player1:Players.Player2;
            }
            current = probability.getSid().alter(current, AlterType.RotateClockwise);
        }
        return null;
    }
    
    private boolean isDrawn() {
		for(int row = 0; row<3;row++) {
			for(int col=0;col<3;col++) {
				if(probability.getSid().get(value[0], row, col)==0) {
					return false;
				}
			}
			
		}
		return true;

	}

	@Override
	public long[] id() {
		return value;
	}

	@Override
	public boolean isGameOver() {
		return getWinnerPlayer()!=null||isDrawn();
	}

	@Override
	public float[] getWinner() {
		if(isDrawn()) {
			return BitUtils.getDrawnProbability();
		} else if(getWinnerPlayer()==Players.Player1) {
			return BitUtils.getPlayer1WinProbability();
		} else {
			return BitUtils.getPlayer2WinProbability();
		}
	}

	@Override
	public List<TicTacToeMove> getAllPossibleMoves(Players player) {
		List<TicTacToeMove> moves = new LinkedList();
		for(int row = 0; row<3;row++) {
			for(int col=0;col<3;col++) {
				if(probability.getSid().get(value[0], row, col)==0) {
					moves.add(new TicTacToeMove(
							probability.getSid().positionToIndex(row, col)));
				}
			}
			
		}
		return moves;
	}

	@Override
	public void init() {
		
	}
}

