package box.model;

import static box.utils.Constants.STATE_INSERT_QUERY;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;

import box.utils.Constants;
import box.utils.LineType;
import box.utils.TransformType;

public class StateProbability {

	//public final StateIdentifier id;
	public final float probabilityPlayer1;
	public final float probabilityPlayer2;

	// private static ExecutorService threadExecutor =
	// Executors.newFixedThreadPool(50);
	private static final SortedMap<StateIdentifier, StateProbability> CACHE = new TreeMap<StateIdentifier, StateProbability>();
	private static long count = 0;
	private static int lacs = 0;

	private static javax.jms.Connection qConnection;
	private static Session session;
	private static MessageProducer producer;
	private static long lastInitialized = 0;
	private static boolean closed = true;

	static {
		BasicConfigurator.configure();
		
		init();

	}

	private static void init() {
		try {
			if (closed) {
				final ActiveMQConnectionFactory connectionFactory
					= new ActiveMQConnectionFactory(Constants.ACTIVE_MQ_URL);
				final Destination destination;
				qConnection = connectionFactory.createConnection();
				session = qConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				destination = session.createQueue(Constants.QUEUE_NAME);

				producer = session.createProducer(destination);

				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				closed = false;
			}
		} catch (JMSException e) {
			System.out.println("Unable to establish MQ connection.");
			System.exit(0);
		}

	}

	private StateProbability(/*S id,*/ float probabilityPlayer1, float probabilityPlayer2) {
		//this.id = id;
		this.probabilityPlayer1 = probabilityPlayer1;
		this.probabilityPlayer2 = probabilityPlayer2;

		count++;
		if (count % 100000 == 99999) {
			count = 0;
			if(lacs%5==4) {
				System.gc();
			}
			lacs++;
			System.out.println(lacs);
		}

	}
	
	
	private static StateProbability setProbability(StateIdentifier id, float probabilityPlayer1, float probabilityPlayer2) {
		StateProbability sp = new StateProbability( probabilityPlayer1, probabilityPlayer2);
		CACHE.put(id, sp);
		return sp;
	}

	public static boolean exists(long[] id) throws InterruptedException, SQLException {
		
		if(StateIdentifier.exists(id)) {
			return true;
		} else {
			StateIdentifier.get(id);
			return false;
		}
	}

	/*@Override
	public String toString() {
		return Arrays.toString(new double[] { probabilityPlayer1, probabilityPlayer2 });
	}*/

	public static StateProbability getProbability(final long[] value) {
		return CACHE.get(StateIdentifier.get(value));
	}

	public static StateProbability getProbability(final long[] id, final float probabilityPlayer1,
			final float probabilityPlayer2, final List<StateProbability> children)
			throws IOException, InterruptedException, SQLException, JMSException {
		//TextMessage message;
		/*if (!exists(id)) {*/
			StateIdentifier idf = StateIdentifier.get(id);
			
			String values = "";
			String probability ="', " +probabilityPlayer1+", "+probabilityPlayer2+")";
			
			StateProbability sp = setProbability(idf, probabilityPlayer1, probabilityPlayer2);
			for(StateIdentifier similarId: idf.similars) {

				/*if(similarId!=id) {
					CACHE.put(similarId, sp);
				}*/
				values += " ('" + similarId.toString() + probability;
			}
			String text = MessageFormat.format(STATE_INSERT_QUERY, values);
			
			try {
					sendToQueue(text);
			} catch (JMSException e1) {
				close();
				init();
				try {
					sendToQueue(text);
				} catch (JMSException e) {
					System.out.println("Failed to reconnect MQ. Stopping application");
					System.exit(0);
				}
			}
				
			
			return sp;
		/*} else {
			return getProbability(id);
		}*/

	}
	
	private static void sendToQueue(String text) throws JMSException {
		producer.send(session.createTextMessage(text));
	}

	private static void close() {
		long now = new Date().getTime();
		if ((now - lastInitialized) > 1000) {
			lastInitialized = now;
			try {
				producer.close();
			} catch (JMSException e1) {
				
			};
			try {
				session.close();;
			} catch (JMSException e) {
				
			}
			try {
				qConnection.close();
			} catch (JMSException e) {
				
			}
			closed = true;
		}
	}

}
