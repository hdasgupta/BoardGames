package box.threads;

import static box.utils.Constants.ACTIVE_MQ_URL;
import static box.utils.Constants.QUEUE_NAME;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;

import box.utils.SQLSavingUtil;

public final class StateConsumer implements Runnable {
	private static int i = 0;
	private static ActiveMQConnectionFactory connectionFactory;
	private static javax.jms.Connection qConnection;
	private static Session session ;
	private static Destination destination;
	private static MessageConsumer consumer = null;
	private static ExecutorService singleThreadService = Executors.newSingleThreadExecutor();
	
	private static final SQLSavingUtil saveSQL = new SQLSavingUtil();
	
	static {
		BasicConfigurator.configure();

		try {
			connectionFactory = new ActiveMQConnectionFactory(ACTIVE_MQ_URL);
			qConnection = connectionFactory.createQueueConnection();
			qConnection.start();
			qConnection.setExceptionListener(new ExceptionListener() {

				public void onException(JMSException exception) {
					System.exit(0);
				}
			});
			session = qConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createQueue(QUEUE_NAME);
			consumer = session.createConsumer(destination);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}


	public void run() {
		Message message = null;
		do {
			try {
				
				message = consumer.receive(1000);
				if (message instanceof TextMessage) {
					TextMessage tm = (TextMessage) message;
					final String text = tm.getText();
					
					saveSQL.save(text);
					
				} 
			} catch (JMSException e) {
				e.printStackTrace();
				break;
			}

		} while(true);

	}
}