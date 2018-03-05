package board.model;

import java.util.List;

import board.utils.Moves;
import board.utils.Players;
import board.utils.SQLFileGenerator;
import board.utils.StateIdentifications;
import board.utils.StateProbability;

public abstract class Board<
			I extends StateIdentifications, 
			M extends Moves,
			C extends Changes<Mv>,
			Mv extends Move> {
	
	protected StateProbability<I> probability;
	protected M moves;
	protected Players currentTurn;
	protected int size;
	
	public Board() {
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public StateProbability<I> getProbability() {
		return probability;
	}
	
	public void setProbability(StateProbability<I> probability) {
		this.probability = probability;
	}
	
	public M getMoves() {
		return moves;
	}
	
	public void setMoves(M moves) {
		this.moves = moves;
	}
	
	public abstract long[] id();
	
	public Players getCurrentTurn() {
		return currentTurn;
	}
	
	public void setCurrentTurn(Players currentTurn) {
		this.currentTurn = currentTurn;
	}
	
	public void simulate() {
		currentTurn=Players.Player1;
		init();
		calculate(currentTurn);
	}
	
	public abstract void init();
	
	public abstract boolean isGameOver();
	
	public abstract float[] getWinner();
	
	public abstract List<Mv> getAllPossibleMoves(Players player);
	
	private float[] calculate(Players p) {
		long[] id = id();
		String idStr = probability.getSid().toStringId(id);
		if(isGameOver()) {
			return probability.get(id, getWinner());
		} else if(probability.exists(id)) {
			return probability.get(id);
		}
		else  {
			List<Mv> possibleMoves = getAllPossibleMoves(p);
			if(possibleMoves.isEmpty()) {
				return probability.get(id, getWinner());
			}
			float probs[] = {0.0f, 0.0f};
			for(Mv m:possibleMoves) {
				Players next=moves.doMoves(m);
				float current[] = calculate(next);
				probs[0]+=current[0];
				probs[1]=current[1];
				moves.undoMove();
			}
			int size = possibleMoves.size();
			probs[0]/=size;
			probs[1]/=size;
			probability.get(id, probs);
			return probs;
		}
	}
}
