package board.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Properties;
import java.util.function.Supplier;

import board.utils.Moves;
import board.utils.Players;
import board.utils.SQLFileGenerator;
import board.utils.StateIdentifications;
import board.utils.StateProbability;

public class Configuration {
	protected HashMap<Class, Object> beans= new HashMap<>();
	
	public <B extends Board, I extends StateIdentifications, M extends Moves> Configuration(
			Class<B> clsB, Class<I> clsI, Class<M> clsM) {
		B board = getBean(clsB);
		StateProbability<I> probability = probabilities(clsI);
		I id = getBean(clsI);
		SQLFileGenerator sql = sql();
		M moves = getBean(clsM);
		
		Properties properties = new Properties();
		try {
			properties.load(Configuration.class.getClassLoader().getResourceAsStream("common.properties"));
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

	public <T> T getBean(Class<T> c) {
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
	
	public <T> Class<T> getClass(int index) {
		ParameterizedType pt = (ParameterizedType)getClass().getGenericSuperclass();
		Class<T> clazz = (Class)pt.getActualTypeArguments()[index];
		return clazz;
	}
	
	private <I extends StateIdentifications> StateProbability<I> probabilities(Class<I> clsI) {
		return  getBean(StateProbability.class);
	}
	
	private SQLFileGenerator sql() {
		return getBean(SQLFileGenerator.class);
	}
}
