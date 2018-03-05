package board.utils;

public class BitUtils {
	public static final long MASKS[] = new long[63];
	
	public static float probability[][] = {
			{1.0F, 0.0F},
			{0.5F, 0.5F},
			{0.0F, 1.0F}
	};
	
	static {
		for(int i=0;i<63;i++) {
			MASKS[i] = 1 << (62-i);
		}
	}
	
	public static long setVal(long current, int index) {
		return current | MASKS[index];
	}
	
	public static boolean getVal(long current, int index) {
		return (current & MASKS[index]) > 0;
	}
	
	public static long setVal(long current, int index, int value, int radix) {
		return current + ((value - getVal(current, index, radix)) * (long)Math.pow(radix, index));
	}
	
	public static int getVal(long current, int index, int radix) {
		return (int)((current / (long)Math.pow(radix, index))%radix);
	}
	
	public static float[] getPlayer1WinProbability() {
		return probability[0];
	}
	
	public static float[] getDrawnProbability() {
		return probability[1];
	}
	
	public static float[] getPlayer2WinProbability() {
		return probability[2];
	}
	
	public static String toString(long val, int size) {
		String v = "";
		for(int i=0;i<size;i++) {
			v += getVal(val, i)?"1":"0";
		}
		return v;
	}
	
	public static String toString(long val, int size, int radix) {
		String v = "";
		for(int i=0;i<size;i++) {
			v += Integer.toHexString(getVal(val, i, radix));
		}
		return v;
	}
}
