package board.utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;

import board.model.Board;

public abstract class StateIdentifications {
	private List<long[]> CACHE = new ArrayList<long[]>();
	
	private Function<long[], List<long[]>> func;
	protected Board board;
	protected final Comparator<long[]> COMPARATOR = new Comparator<long[]>() {
		
		@Override
		public int compare(long[] o1, long[] o2) {
			int lenDiff = o2.length-o1.length;
			if(lenDiff!=0) {
				return lenDiff;
			}
			for(int index=0;index<o1.length;index++) {
				long diff = o2[index]-o1[index];
				if(diff==0) {
					continue;
				}
				return diff>0?1:-1;
			}
			return 0;
		}
	};
	
	public Board getBoard() {
		return board;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}
	
	public abstract String toStringId(long[] id);
	
	public abstract List<long[]> generateSimilar(long[] id);
	
	public boolean exists(long[] id) {
		List<long[]> list = generateSimilar(id);
		for(long[] val:list) {
			int index = Collections.binarySearch(CACHE, val, COMPARATOR);
			if(index>=0) {
				return true;
			}else {
				index = -index -1;
				CACHE.add(index, val);
			}
		}
		return false;
	}
	
	public int get(long... value) {
		return Collections.binarySearch(CACHE, value, COMPARATOR);
	}
	
}
