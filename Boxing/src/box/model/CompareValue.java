package box.model;

import java.util.Comparator;

public final class CompareValue implements Comparator<long[]> {
	
	@Override
	public int compare(long[] o1, long[] o2) {
		long diff = o2[0]+o2[1]+o2[2]-o1[0]-o1[1]+o1[2];
		return diff>0?1:(diff==0?0:-1);
	}
}