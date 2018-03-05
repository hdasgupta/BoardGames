package box.utils;

import static box.utils.Constants.RELATION;
import static box.utils.Constants.SPACE;
import static box.utils.Constants.STATE;
import static box.utils.Constants.STATE_INSERT_QUERY;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;

public final class SQLSavingUtil  {
	/*private PreparedStatement CHECK_STATE;
	private PreparedStatement STATE_INSERT;
	private Connection connection;*/
	
	
	private BufferedWriter br;
	private int index=0;
	private int count = 0;
	
	public SQLSavingUtil() {
		try {
			init();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void init() throws FileNotFoundException {
		br = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(
						new File(Constants.DIR_NAME, ""+index+".sql"))));
	}
	
	private void close() throws IOException {
		br.close();
	}
	
	public void  save(String text) {
		try {
			br.write(text);
			br.newLine();
			count++;
			if(count%10000==9999) {
				count=0;
				index++;
				close();
				System.gc();
				init();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}