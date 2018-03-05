package checker.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import board.model.Move;
import board.utils.Players;
import checker.utils.MoveType;
import checker.utils.Position;

public class Checker {
	private Position position;
	private Players owner;
	private boolean isKing = false;
	private final CheckerBoard b;
	
	
	public Checker(CheckerBoard b, Players owner, Position position) {
		this.owner = owner;
		this.position = position;
		this.b = b;
		position.setOwner(this);
	}
	
	public Position getPosition() {
		return position;
	}
	
	public void setPosition(Position position) {
		this.position.setOwner(null);
		this.position = position;
		position.setOwner(this);
	}
	
	public Players getOwner() {
		return owner;
	}
	
	public void setOwner(Players owner) {
		this.owner = owner;
	}
	
	public void captureBy(Players player) {
		owner = player;
	}
	
	public boolean isNextMoveOrJumpAvailable() {
		return true;
	}
	
	public int symbol() {
		return isKing?(owner==Players.Player1?3:4):(owner==Players.Player1?1:2);
	}
	
	public List<Jump> possibleNextJumps() {
		LinkedList<Jump> jumps = new LinkedList<>();
		int dRowForward = owner==Players.Player1?+1:-1;

		jumps.addAll(predictMoves(position, dRowForward, -1, new LinkedList<>()));
		jumps.addAll(predictMoves(position, dRowForward, +1, new LinkedList<>()));
		if(isKing) {
			jumps.addAll(predictMoves(position, -dRowForward, -1, new LinkedList<>()));
			jumps.addAll(predictMoves(position, -dRowForward, +1, new LinkedList<>()));
		}
		return jumps;
	}
	
	private List<Jump> predictMoves(Position position, int dRow, int dColumn, LinkedList<Position> poses) {
		LinkedList<Jump> jumps = new LinkedList<>();
		int newRow = position.row+dRow, newColumn = position.column+dColumn;
		if(newRow >=0 && newRow<b.getSize()) {
			if(newColumn>=0 && newColumn<b.getSize()) {
				Position newPosition=b.getPosition(newRow, newColumn);
				Position intermidiate = b.getPosition(newPosition.row-dRow/2, 
						newPosition.column-dColumn/2);
				if(newPosition.getOwner()==null) {
					if(poses.isEmpty()) {
						jumps.add(new Jump(this,MoveType.Normal, newPosition));
					} else if(Math.abs(dRow)==2 && !poses.contains(newPosition)) {
						if(intermidiate.getOwner()!=null&&
								intermidiate.getOwner().getOwner()==owner) {
							poses.add(newPosition);
							jumps.addAll(predictMoves(newPosition, dRow>0?2:-2, 2, poses));
							jumps.addAll(predictMoves(newPosition, dRow>0?2:-2, -2, poses));
							if(isKing) {
								jumps.addAll(predictMoves(newPosition, dRow>0?-2:2, 2, poses));
								jumps.addAll(predictMoves(newPosition, dRow>0?-2:2, -2, poses));
							}
							poses.remove(newPosition);
						} else {
							jumps.add(new Jump(this, MoveType.Jump, poses.toArray(new Position[poses.size()])));
							return jumps;
						}
						
					}
				} else if(Math.abs(dRow)==2 &&
						(intermidiate.getOwner()==null||
						intermidiate.getOwner().getOwner() == owner) &&
						!poses.isEmpty()) {
					jumps.add(new Jump(this, MoveType.Jump, poses.toArray(new Position[poses.size()])));
					return jumps;
				}else if(newPosition.getOwner().owner!=owner) {
					int c = 1;
					for(int row = newPosition.row+dRow, column = newPosition.column+dColumn;
							((dRow<0&&row>0)||(dRow>0&&row<b.getSize()-1))&&
							((dColumn<0&&column>0)||(dColumn>0&&column<b.getSize()-1));row+=dRow,column+=dColumn) {
						Position current = b.getPosition(row, column);
						if(current.getOwner()==null) {
							if(c>1) {
								if(poses.isEmpty()) {
									jumps.add(new Jump(this, MoveType.MultiJump, current));
									return jumps;
								}
							} else {
								if(!poses.contains(current)) {
									poses.add(current);
									jumps.addAll(predictMoves(current, dRow>0?2:-2, 2, poses));
									jumps.addAll(predictMoves(current, dRow>0?2:-2, -2, poses));
									if(isKing) {
										jumps.addAll(predictMoves(current, dRow>0?-2:2, 2, poses));
										jumps.addAll(predictMoves(current, dRow>0?-2:2, -2, poses));
									}
									poses.remove(current);
								}
								return jumps;

							}
						} else if(current.getOwner().owner!=owner) {
							c++;
						} else if(current.getOwner().owner==owner) {
							return jumps;
						}
					}
				}
			}
		}
		return jumps;
	}
	
	private CheckerChanges moveTo(Jump j) {
		Position newPosition, oldPosition = position;
		List<Checker> occupied = new LinkedList<>();
		
		
		switch (j.type) {
		case Jump:
			newPosition = position;
			for(Position p:j.newPositions) {
				if(p!=position) {
					Position intermidiate = b.getPosition((newPosition.row+p.row)/2,
							(newPosition.column+p.column)/2);
					Checker occupy = intermidiate.getOwner();
					occupied.add(occupy);
					occupy.owner = this.owner;
					//setPosition(p);
					newPosition = p;
				}
			}
			break;
		case MultiJump:
			int dRow = j.newPositions[0].row-position.row, 
				dColumn = j.newPositions[0].column-position.column;
			
			int dr = dRow<0?-1:1, dc = dColumn<0?-1:1;
			
			for(int ddr = dr, ddc = dc;
					Math.abs(dRow- ddr)>0;ddr+=dr, ddc+=dc) {
				newPosition = b.getPosition(position.row+ddr, 
						position.column+ddc);
				if(newPosition!=position) {
					Checker occupy = newPosition.getOwner();
					occupied.add(occupy);
					occupy.owner = this.owner;
				}
					
			}
			break;
		default:
			break;
		}
		setPosition(j.newPositions[j.newPositions.length-1]);
		Players turn = owner==Players.Player1?Players.Player2:Players.Player1;
		
		if(((owner==Players.Player1 && position.row==b.getSize()-1) ||
				(owner==Players.Player2 && position.row == 0)) && !isKing) {
			isKing=true;
			return new CheckerChanges(owner, turn, j, oldPosition, occupied, true);
		} else {
			return new CheckerChanges(owner, turn, j, oldPosition, occupied);
		}
		
		
	}
	
	private void undoMove(CheckerChanges change) {
		this.setPosition(change.initialPosition);
		for(Checker c:change.occupiedList) {
			c.owner = change.getNextTurn();
		}
		if(change.king) {
			isKing = false;
		}
	}
	
	public class Jump implements Move {
		private final Checker forChecker;
		private final MoveType type;
		private final Position[] newPositions;
		
		public Jump( Checker forChecker, MoveType type, Position... newPosition) {
			this.forChecker = forChecker;
			this.type = type;
			this.newPositions = newPosition;
		}

		public CheckerChanges doJump() {
			return forChecker.moveTo(this);
		}
		
		public  void undo(CheckerChanges c) {
			forChecker.undoMove(c);
		}
		
		public MoveType type() {
			return type;
		}
	}
}
