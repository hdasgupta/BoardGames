package checker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import board.model.Configuration;
import board.utils.SQLFileGenerator;
import checker.model.CheckerBoard;
import checker.utils.CheckerMoves;
import checker.utils.StateId;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Configuration configuration = 
				new Configuration(CheckerBoard.class, StateId.class, CheckerMoves.class);
		
		configuration.getBean(CheckerBoard.class).simulate(); 
		
		configuration.getBean(SQLFileGenerator.class).close();
	}
}
