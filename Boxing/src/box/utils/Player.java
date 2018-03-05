package box.utils;

public enum Player {
	None("0"), Player1("0"), Player2("1");
	private String s;
	private Player(String s) {
		this.s = s;
	}
	
	@Override
	public String toString() {
		return s;
	}
}
