package box;

import board.model.Configuration;
import box.model.BoxMoves;
import box.model.StateIdentifier;
import box.threads.BoxBoard;

public class Main {

	public static void main(String[] args) {
		Configuration<BoxBoard, StateIdentifier, BoxMoves> configuration =
				new Configuration<>();
		
		configuration.board().simulate();

	}

}
