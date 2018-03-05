package checker.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import board.model.Board;
import board.utils.BitUtils;
import board.utils.Players;
import board.utils.StateProbability;
import checker.model.Checker.Jump;
import checker.utils.CheckerMoves;
import checker.utils.MoveType;
import checker.utils.Position;
import checker.utils.StateId;

public class CheckerBoard extends Board<StateId, CheckerMoves, CheckerChanges, Jump> {
	
	private final TreeMap<Integer, Position> positions = new TreeMap<>();
	
	public CheckerBoard() {
	}
	
	public List<Checker> checkerFor(Players player) {
		LinkedList<Checker> checkers = new LinkedList<>();
		for(Position p:positions.values()) {
			if(p.getOwner().getOwner() == player) {
				checkers.add(p.getOwner());
			}
		}
		return checkers;
	}
	
	private int getIndex(int row, int column) {
		return ((row*size)+column);
	}
	
	public Position getPosition(int row, int column) {
		return positions.get(getIndex(row, column));
	}
	
	public long[] id() {
		long val[] = new long[size];
		
		for(Position p: positions.values()) {
			val[p.row] = BitUtils.setVal(val[p.row], p.column/2, p.getOwnerSymbol(), 5);
		}

		return val;
	}
	
	private List<Checker> getCheckers(Players p) {
		List<Checker> checkers = new LinkedList<>();
		List<Position> poses = new ArrayList<>(positions.values());
		if(p==Players.Player2) {
			Collections.reverse(poses);
		}
		for(Position pos:poses) {
			if(pos.getOwner()!=null&&
					pos.getOwner().getOwner()==p) {
				checkers.add(pos.getOwner());
			}
		}
		return checkers;
	}

	private Players getWinnerPlayer() {
		Players player = null;
		for(Position p:positions.values()) {
			if(p.getOwner()!=null) {
				if(player==null) {
					player=p.getOwner().getOwner();
				}
				else if(player!=p.getOwner().getOwner()) {
					return null;
				}
			}
		}
		return player;
	}

	@Override
	public boolean isGameOver() {
		return getWinnerPlayer()!=null;
	}

	@Override
	public float[] getWinner() {
		Players p = getWinnerPlayer();
		
		return p==null?BitUtils.getDrawnProbability():
			(p==Players.Player1?
					BitUtils.getPlayer1WinProbability():
				BitUtils.getPlayer2WinProbability());
	}

	@Override
	public List<Jump> getAllPossibleMoves(Players player) {
		List<Checker> checkers = getCheckers(player);
		List<Jump> jumps = new LinkedList<>();
		boolean hasJump = false;
		for(Checker c: checkers) {
			List<Jump> jj = c.possibleNextJumps();
			if(hasJump) {
				for(Jump j : jj) {
					if(j.type()==MoveType.Normal) {
						continue;
					}
					jumps.add(j);
				}
			} else {
				List<Jump> newJumps = new LinkedList<>();
				for(Jump j : jj) {
					if(!hasJump && j.type()!=MoveType.Normal ) {
						hasJump = true;
						for(Jump jmp:jumps) {
							if(jmp.type()!=MoveType.Normal) {
								newJumps.add(jmp);
							}
						}
						jumps = newJumps;
						jumps.add(j);
					} else if((hasJump && j.type()!=MoveType.Normal) ||
							(!hasJump)) {
						jumps.add(j);
					}
				}
			}
		}
		return jumps;
	}

	@Override
	public void init() {
		List<Position> ps = new LinkedList<>();
		for(int row = 0; row<size;row++) {
			for(int column = 0; column<size;column++) {
				int index = getIndex(row, column);
				if((index+row)%2==0) {
					Position p = new Position(row, column);
					positions.put(index, p);
					ps.add(p);
				}
			}
		}
		int startingCheckerCount = (size*3)/2;
		
		for(int index = 0; index<startingCheckerCount;index++) {
			Position player1Position, player2Position;
			
			player1Position = ps.get(index);
			player2Position = ps.get(ps.size()-index-1);
			new Checker(this, Players.Player1, player1Position);
			new Checker(this, Players.Player2, player2Position);
		}
	}

}
