package box;

import java.io.IOException;
import java.sql.SQLException;

import javax.jms.JMSException;

import box.threads.StateProducer;

public class Producer {

	
	
	public static void main(String[] args) throws IOException, InterruptedException, SQLException, JMSException {
			new Thread(new StateProducer()).start();
	}

}
