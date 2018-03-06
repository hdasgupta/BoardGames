package box.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import board.utils.BitUtils;
import board.utils.StateIdentifications;
import box.threads.BoxBoard;
import box.utils.LineType;
import box.utils.TransformType;

public class StateIdentifier extends StateIdentifications {
	/*private final long[] value;*/
	private static final int HORIZONTAL_LINES = 0, 
			VERTICAL_LINES = 1, BOXES = 2;
	/*private static final CompareValue COMPARE_VALUE = new CompareValue();
	private static final long MASKS[] = new long[63];
	public final LinkedList<StateIdentifier> similars;
	private static LinkedList<StateIdentifier> CACHE = new LinkedList<StateIdentifier>();
	*/
	
	/*static {
		for(int index = 55; index>=0;index--) {
			MASKS[55-index] = (long)1<<index;
		}
	}*/
	
	/*private StateIdentifier(long... value) {
		this(true, value);
		generateSimilarIds();
	}
	
	private StateIdentifier(boolean withoutSimilar, long... value) {
		this.value = value;
		this.similars = new LinkedList<StateIdentifier>();
		
	}*/
	
	private long[] change(final long val[], TransformType type) {
		BoxBoard b = (BoxBoard) board;
		int dim = b.getSize();
		int row, column, newIndex;
		final long newVal[] = {0, 0, 0};
		for(int otype = 0; otype < 3;otype++) {
		for(int index = 0; index < (otype==BOXES?
				b.getTOTAL_BOX():b.getNUMBER_OF_HORIZNTAL_LINES());index++) {
			int oldIndex = otype*b.getNUMBER_OF_HORIZNTAL_LINES()+index;
			if(otype==HORIZONTAL_LINES) {
				
				row = oldIndex/(dim-1);
				column = oldIndex%(dim-1);
				
				if(type==TransformType.Rotate90d) {
					newIndex = dim*(dim + column)-row - 1;
				} else if(type==TransformType.FlipVertical) {
					newIndex = (dim-1)*(dim-1-row)+column;
				} else {
					newIndex =(dim-1)*(row+1)-column-1;
				}
			} else if(otype==VERTICAL_LINES) {
				int vIndex = oldIndex-b.getNUMBER_OF_HORIZNTAL_LINES();
				row = vIndex/dim;
				column = vIndex%dim;
				
				if(type==TransformType.Rotate90d) {
					newIndex = (dim-1)*(column+1)-row-1;
				} else if(type==TransformType.FlipVertical) {
					newIndex = dim*(2*dim-row-3)+column;
				} else {
					newIndex =dim*(dim+row)-column-1;
				}
				
			} else {
				int bxIndex = oldIndex-2*b.getNUMBER_OF_HORIZNTAL_LINES();
				row = bxIndex/(dim-1);
				column = bxIndex%(dim-1);
				
				if(type==TransformType.Rotate90d) {
					newIndex = (dim-1)*(2*dim+column+1)-row-1;
				} else if(type==TransformType.FlipVertical) {
					newIndex = (dim-1)*(3*dim-row-2)+column;
				} else {
					newIndex =(dim-1)*(2*dim+row+1)-column-1;
				}
			}
			boolean oldVal = BitUtils.getVal(val[otype], index);
			if(oldVal) {
				int newType = newIndex/b.getNUMBER_OF_HORIZNTAL_LINES();
				int oIndex = newIndex % b.getNUMBER_OF_HORIZNTAL_LINES();
				newVal[newType] = BitUtils.setVal(newVal[newType], oIndex);
			}
		}
		}
		return newVal;
	}/*
	private static String fillWithZerros(long val, int size) {
		String value = Long.toBinaryString(val);
		int count = 56  - value.length();
		char c[] = new char[count];
		Arrays.fill(c,  Constants.NO_LINE.charAt(0));
		return (String.valueOf(c)+value).substring(0, size);
	}
	private static String fillWithZerros(long val, int size) {
		String value = Long.toBinaryString(val);
		int count = 56  - value.length();
		char c[] = new char[count];
		Arrays.fill(c,  Constants.NO_LINE.charAt(0));
		return (String.valueOf(c)+value).substring(0, size);
	}
	
	private static String id(long value[]) {
		String id=fillWithZerros(value[HORIZONTAL_LINES], Constants.LINE_COUNT)
				+ fillWithZerros(value[VERTICAL_LINES],  Constants.LINE_COUNT)
				+ fillWithZerros(value[BOXES],  Constants.BOX_COUNT);
		
		return id;
	}
	
	private String id() {
		return id(value);
	}
	
	private void generateSimilarIds() {
		final long[] mainId = value;
		long[] id = mainId;
		
		LinkedList<StateIdentifier> list = new LinkedList<StateIdentifier>();		
		
		TreeSet<long[]> tree = new TreeSet<long[]>(COMPARE_VALUE);		
		
		tree.add(mainId);
		tree.add(change(id, TransformType.FlipHorazontal));
		tree.add(change(id, TransformType.FlipVertical));
		for(int i=0;i<3;i++) {
			tree.add(id = change(id, TransformType.Rotate90d));
			tree.add(change(id, TransformType.FlipHorazontal));
			tree.add(change(id, TransformType.FlipVertical));
		}
		//tree.remove(mainId);
		
		for(long[] item: tree) {
			if(Arrays.equals(item, mainId)) {
				list.add(this);
			} else {
				list.add(new StateIdentifier(true, item));
			}			
		}
		for(StateIdentifier sid:list) {
			sid.similars.addAll(list);
		}
	}
	
	public static long[] value(LinkedList<Line> lines, LinkedList<Box> box) {
		long value[] = new long[3];;
		int index = 0;
		for(Line l:lines) {
			int type = index/Constants.LINE_COUNT;
			int idx = index%Constants.LINE_COUNT;
			
			if(l.hasLine()) {
				value[type] |= MASKS[idx];
			}
			index++;
		}
		for(Box b:box) {

			if(b.getOwner()==Player.Player2) {
				value[BOXES] += MASKS[index%Constants.LINE_COUNT];
			}
			index++;
		}
		return value;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		long[] value = {};
		if(obj instanceof StateIdentifier) {
			StateIdentifier o = (StateIdentifier) obj;
			value = o.value;
		} else if(obj instanceof long[]) {
			value = (long[]) obj;
		}

		for(StateIdentifier id:similars) {
			if(Arrays.equals(id.value, value)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return id();
	}

	@Override
	public int compareTo(StateIdentifier o) {
		
		return COMPARE_VALUE.compare(this.value, o.value);
	}
	
	private static final StateIdentifier TEMP = new StateIdentifier(true, 0l, 0l, 0l);
	
	public static StateIdentifier get(long...value) {
		
		
		TEMP.value[HORIZONTAL_LINES] = value[HORIZONTAL_LINES];
		TEMP.value[VERTICAL_LINES] = value[VERTICAL_LINES];
		TEMP.value[BOXES] = value[BOXES];
		
		int index = Collections.binarySearch(CACHE, TEMP);
		
		if(index>=0) {
			return CACHE.get(index);
		
		}
		StateIdentifier sid = new StateIdentifier(value[HORIZONTAL_LINES],
				value[VERTICAL_LINES], value[BOXES]);
		
		CACHE.add(-index-1, sid);
		
		return sid;
	}
	
	public static boolean exists(long[] value) {
		
		TEMP.value[HORIZONTAL_LINES] = value[HORIZONTAL_LINES];
		TEMP.value[VERTICAL_LINES] = value[VERTICAL_LINES];
		TEMP.value[BOXES] = value[BOXES];
		
		int index = Collections.binarySearch(CACHE, TEMP);
		
		return index >= 0;
	}*/

	@Override
	public String toStringId(long[] value) {
		BoxBoard b = (BoxBoard) board;
		String id= BitUtils.toString(value[HORIZONTAL_LINES], b.getNUMBER_OF_HORIZNTAL_LINES())
				+ BitUtils.toString(value[VERTICAL_LINES],  b.getNUMBER_OF_VERTICAL_LINES())
				+ BitUtils.toString(value[BOXES],  b.getTOTAL_BOX());
		return id;
	}

	@Override
	public List<long[]> generateSimilar(long[] value) {
		final long[] mainId = value;
		long[] id = mainId;
		
		TreeSet<long[]> list = new TreeSet<long[]>(COMPARATOR);		
		
		
		list.add(mainId);
		list.add(change(id, TransformType.FlipHorazontal));
		list.add(change(id, TransformType.FlipVertical));
		for(int i=0;i<3;i++) {
			list.add(id = change(id, TransformType.Rotate90d));
			list.add(change(id, TransformType.FlipHorazontal));
			list.add(change(id, TransformType.FlipVertical));
		}
		//tree.remove(mainId);
		
		
		return new LinkedList<long[]>(list);
	}
}
