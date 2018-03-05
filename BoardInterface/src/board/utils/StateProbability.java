package board.utils;

import java.util.ArrayList;
import java.util.List;

import board.model.Board;

public abstract class StateProbability<I extends StateIdentifications> {
	
	protected I sid; 
	protected SQLFileGenerator sql;
	protected String INSERT_QUERY;
	protected final List<float[]> CACHE = new ArrayList<float[]>();
	protected Board board;
	
	
	public StateProbability() {
		
	}
	
	public void setDBDetails(String db, String table, String... columns) {
		INSERT_QUERY = "INSERT INTO `"+db+"`.`"+table+"` "
				+ "(`"+String.join("`,`", columns)+"`) values (";
	}
	
	public boolean exists(long... id) {
		if(sid.exists(id)) {
			return true;
		} else {
			float[] f = {0.0f, 0.0f};
			CACHE.add(sid.get(id), f);
			return false;
		}
	}
	
	public float[] get(long... id) {
		return CACHE.get(sid.get(id));
	}
	
	public float[] get(long[] id, float... probabilities) {
		
		String valueStr = "', "+probabilities[0]+", "+probabilities[1]+");";
		for(long[] similar:sid.generateSimilar(id)) {
			float[] probs =  get(similar);
			probs[0] = probabilities[0];
			probs[1] = probabilities[1];
			
			sql.saveQuery(INSERT_QUERY
					+"'"+sid.toStringId(similar)+valueStr);
		}
		return probabilities;
	}



	public I getSid() {
		return sid;
	}



	public void setSid(I sid) {
		this.sid = sid;
	}



	public SQLFileGenerator getSql() {
		return sql;
	}



	public void setSql(SQLFileGenerator sql) {
		this.sql = sql;
	}



	public Board getBoard() {
		return board;
	}



	public void setBoard(Board board) {
		this.board = board;
	}
	
	
}
