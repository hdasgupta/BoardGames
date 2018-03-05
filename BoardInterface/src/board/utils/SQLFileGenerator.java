package board.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;

public class SQLFileGenerator {
	private File dir;
	private long count;
	private BufferedWriter br=null;
	private int queryCount =0;
	private int fileIndex = 0;
	private int saveFrom;
	
	public SQLFileGenerator() {
		
	}
	
	
	
	public long getCount() {
		return count;
	}



	public void setCount(long count) {
		this.count = count;
	}



	public void setDir(String dir) {
		this.dir = new File(dir);
		this.dir.mkdirs();	
		int saveFrom = 0;
		
		for(File f:this.dir.listFiles()) {
			try {
				int i = Integer.parseInt(f.getName().split("\\.")[0]);
				if(i>saveFrom) {
					saveFrom = i;
				}
			} catch(NumberFormatException e) {
				
			}
		}
		this.saveFrom = saveFrom==0?0:saveFrom - 1;
	}
	
	private void init() {
		if(fileIndex>=saveFrom) {
			try {
				br = new BufferedWriter(new FileWriter(new File(dir, ""+fileIndex+".sql")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fileIndex++;
	}
	
	public void close() {
		if(fileIndex>saveFrom) {
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.gc();
		}
	}
	
	public void saveQuery(String query) {
		if(br==null) {
			init();
		}
		if(fileIndex>=saveFrom) {
			try {
				br.write(query);
				br.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(queryCount>=count-1) {
			queryCount=0;
			close();			
			init();
		}
	}
}
