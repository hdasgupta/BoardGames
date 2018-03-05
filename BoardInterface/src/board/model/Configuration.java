package board.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Supplier;

import board.utils.Moves;
import board.utils.Players;
import board.utils.SQLFileGenerator;
import board.utils.StateIdentifications;
import board.utils.StateProbability;

public class Configuration<B extends Board, 
I extends StateIdentifications,
M extends Moves> {
	protected HashMap<Class, Object> beans;
	
	public Configuration() {
		B board = board();
		StateProbability<I> probability = probabilities();
		I id = id();
		SQLFileGenerator sql = sql();
		M moves = moves();
		
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(new File("common.properties")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		board.setMoves(moves);
		board.setProbability(probability);
		board.setCurrentTurn(Players.Player1);
		board.setSize(Integer.parseInt(properties.getProperty("board.size")));
		
		probability.setBoard(board);
		probability.setDBDetails(properties.getProperty("sql.db"), 
				properties.getProperty("sql.table"), 
				properties.getProperty("sql.columns").split("\\,"));
		probability.setSid(id);
		probability.setSql(sql);
		
		id.setBoard(board);
		
		sql.setCount(Integer.parseInt(properties.getProperty("sql.query_per_file")));
		sql.setDir(properties.getProperty("sql.dir"));
		
		moves.setBoard(board);
	}

	protected <T> T getBean(Class<T> c) {
		if(beans.containsKey(c)) {
			return (T)beans.get(c);
		} else {
			
			T t;
			try {
				t = c.newInstance();
				beans.put(c, t);
				return t;
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public B board() {
		return (B) getBean(Board.class);
	}
	
	public StateProbability<I> probabilities() {
		return  getBean(StateProbability.class);
	}
	
	public I id() {
		return (I) getBean(StateIdentifications.class);
	}
	
	public M moves() {
		return (M) getBean(Moves.class);
	}
	
	public SQLFileGenerator sql() {
		return getBean(SQLFileGenerator.class);
	}
}
