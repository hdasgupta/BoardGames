package box;

import board.model.Configuration;
import board.utils.SQLFileGenerator;
import box.model.BoxMoves;
import box.model.StateIdentifier;
import box.threads.BoxBoard;

public class Main {

	public static void main(String[] args) {
		Configuration configuration =
				new Configuration(BoxBoard.class, StateIdentifier.class, BoxMoves.class);
		
		configuration.getBean(BoxBoard.class).simulate();
		
		configuration.getBean(SQLFileGenerator.class).close();
	}

}
