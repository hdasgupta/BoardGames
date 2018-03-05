package checker.utils;

import java.util.LinkedList;
import java.util.List;

import board.utils.BitUtils;
import board.utils.StateIdentifications;

public class StateId extends StateIdentifications {

	
	@Override
	public String toStringId(long[] id) {
		String val = "";
		for(long l:id) {
			val += BitUtils.toString(l, board.getSize(), 5);
		}
		return val;
	}

	@Override
	public List<long[]> generateSimilar(long[] id) {
		return new LinkedList<long[]>() {{add(id);}};
	}

}
