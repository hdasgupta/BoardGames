package box.utils;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import box.model.Box;
import box.model.Line;

public class Constants {
	public static final LinkedList<Box> BOX_LIST = new LinkedList<Box>();
	
	public static final String NO_LINE, LINE;
	public static final String DIR_NAME;
	
	public static final int DIMENSION, LINE_COUNT, BOX_COUNT;
	
	public static final LinkedList<Line> LINE_LIST = new LinkedList<Line>();
	
	public static final String ACTIVE_MQ_URL, /*MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD,*/
		QUEUE_NAME;
	public static final String SPACE = " "/*,*/
			/*SELECT_ALL_STATE_QUERY = "SELECT * FROM `box`.`states`",
					RELATION_CHECKING_QUERY = "SELECT `state`, `parentState`" + 
							" FROM relations WHERE state=? and parentState=?",*/
			/*STATE_CHECKING_QUERY = "SELECT `state`, `player1WiningProbability`, `player2WiningProbability`"
					+ " FROM states WHERE state=?"*/,
	STATE_INSERT_QUERY/*,
	RELATION_INSERT_QUERY=
			"INSERT INTO `box`.`relations` (`state`, `parentState`) "
			+ "values (?, ?);"*/;
	static {
		Properties p = new Properties();
		try {
			p.load(Constants.class.getClassLoader().getResourceAsStream("common.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		DIMENSION= Integer.parseInt(p.getProperty("box.dimension"));
		LINE_COUNT = DIMENSION * (DIMENSION - 1);
		BOX_COUNT = (DIMENSION - 1)*(DIMENSION - 1);
		
		LINE = p.getProperty("char.line");
		NO_LINE=p.getProperty("char.noline");
		ACTIVE_MQ_URL=p.getProperty("activemq.url");
		/*MYSQL_URL=p.getProperty("mysql.connectionstring");
		MYSQL_USER=p.getProperty("mysql.user");
		MYSQL_PASSWORD=p.getProperty("mysql.password");*/
		QUEUE_NAME=p.getProperty("activemq.queue");
		DIR_NAME=p.getProperty("dir.save");
		STATE_INSERT_QUERY=p.getProperty("insert.query");
	}

	public static final String BLANK = "",
			STATE = "STATE", 
			RELATION = "RELATION"; 


}
