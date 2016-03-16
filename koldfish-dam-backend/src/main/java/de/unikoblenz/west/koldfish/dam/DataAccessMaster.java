package de.unikoblenz.west.koldfish.dam;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import de.unikoblenz.west.koldfish.dam.impl.HttpAccessWorker;
import de.unikoblenz.west.koldfish.dam.messages.DerefMessage;

/**
 * responsible for JMS connections and controls data access workers.
 * 
 * @author lkastler
 */
public class DataAccessMaster extends Thread implements AutoCloseable {

	private static final Logger log = LogManager
			.getLogger(DataAccessMaster.class);

	private static final Lock lock = new ReentrantLock();
	private static final Condition running = lock.newCondition();

	private Connection connection;
	private Session session;
	private MessageConsumer deref;
	private MessageProducer data;
	private MessageProducer errors;

	private final ExecutorService service = Executors.newCachedThreadPool();

	/**
	 * creates a new DataAccessMasterImpl object, using given
	 * ConnectivityFactory for creating JMS connection, sessions, etc.
	 * 
	 * @param connectionFactory
	 *            - JMS ConnectivityFactory to connect to a broker.
	 * @throws JMSException
	 */
	public DataAccessMaster(ConnectionFactory connectionFactory)
			throws JMSException {
		connection = connectionFactory.createConnection();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// deref queue
		deref = session
				.createConsumer(session.createQueue(Constants.DAM_DEREF));
		// data output
		data = session.createProducer(session.createTopic(Constants.DAM_DATA));
		// error output
		errors = session.createProducer(session.createTopic(Constants.DAM_ERRORS));
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				close();
				try {
					DataAccessMaster.this.join();
				} catch (InterruptedException e) {
					log.error(e);
				}
				if( LogManager.getContext() instanceof LoggerContext ) {
	                Configurator.shutdown((LoggerContext)LogManager.getContext());
	            } else {
	                log.warn("Unable to shutdown log4j2");
	            }
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			deref.setMessageListener(setDerefListener(session, data, errors));

			connection.start();

			log.debug("running");

			lock.lock();
			try {
				running.await();
			} catch (InterruptedException e) {
				log.error(e);
			} finally {
				lock.unlock();
			}
		} catch (JMSException e) {
			log.catching(e);
		}
	}

	private MessageListener setDerefListener(Session session,
			MessageProducer data, MessageProducer error) {
		return new MessageListener() {
			@Override
			public void onMessage(Message msg) {
				try {
					// is ObjectMessage?
					if (msg instanceof ObjectMessage) {
						Serializable content = ((ObjectMessage) msg)
								.getObject();

						log.debug("received: {}", content);

						// is deref command?
						if (content instanceof DerefMessage) {
							String iri = ((DerefMessage) content).getIRI();
							log.debug("retrieving: {}", iri);

							if (!service.isShutdown()) {
								CompletableFuture
										.supplyAsync(new HttpAccessWorker(iri),
												service)
										.whenComplete(
												(result, ex) -> {
													try {
														if (ex != null) {
															error.send(session
																	.createObjectMessage(ex));
														} else {
															data.send(session
																	.createObjectMessage(result));
														}
													} catch (Exception e) {
														log.error(e);
													}
												});
							} else {
								log.debug("executor already shut down");
							}
						}
					}
				} catch (Exception e) {
					log.error(e);
				}
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() {
		log.debug("closing");

		try {
			service.shutdown();
			if (!service.awaitTermination(30, TimeUnit.SECONDS)) {
				service.shutdownNow();
			}
		} catch (InterruptedException e) {
			log.error(e);
		}

		try {
			deref.close();
			data.close();
			errors.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			log.error(e);
		}

		lock.lock();
		try {
			running.signalAll();
		} finally {
			lock.unlock();
		}

		log.debug("closed");

	}

	/**
	 * starts the DataAccessManager
	 * 
	 * @param args
	 *            - no arguments.
	 */
	public static void main(String[] args) {
		log.info("accessing {}",
				ActiveMQConnectionFactory.DEFAULT_BROKER_BIND_URL);

		ActiveMQConnectionFactory fac = new ActiveMQConnectionFactory(
				ActiveMQConnectionFactory.DEFAULT_USER,
				ActiveMQConnectionFactory.DEFAULT_PASSWORD,
				ActiveMQConnectionFactory.DEFAULT_BROKER_BIND_URL);

		// FIXME hardcore hack: http://activemq.apache.org/objectmessage.html
		fac.setTrustAllPackages(true);

		DataAccessMaster master;
		try {
			
			master = new DataAccessMaster(fac);
			master.start();
		}
		catch (JMSException e1) {log.error(e1);}
		log.debug("done");
	}
}
