package de.unikoblenz.west.koldfish.dam.impl;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import de.unikoblenz.west.koldfish.dam.DataAccessMaster;
import de.unikoblenz.west.koldfish.dam.Constants;
import de.unikoblenz.west.koldfish.dam.messages.DerefMessage;

/**
 * multi-threaded master implementation
 * @author lkastler
 */
public class DataAccessMasterImpl implements DataAccessMaster {

	private static final Logger log = LogManager.getLogger(DataAccessMasterImpl.class);

	private final ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

	private volatile boolean running = true;
	
	private final ConnectionFactory connectionFactory;
	
	/**
	 * creates a new DataAccessMasterImpl object, using given ConnectivityFactory for creating JMS connection, sessions, etc.
	 * @param connectionFactory - JMS ConnectivityFactory to connect to a broker.
	 */
	public DataAccessMasterImpl(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try (
				// JMS preparations
				Connection connection = connectionFactory.createConnection();
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				// control topic
				MessageConsumer ctrl = session.createConsumer(session.createTopic(Constants.DAM_CONTROL));
				// deref queue
				MessageConsumer deref = session.createConsumer(session.createQueue(Constants.DAM_DEREF));
				// data output
				MessageProducer data = session.createProducer(session.createTopic(Constants.DAM_DATA));
				// error output
				MessageProducer errors = session.createProducer(session.createTopic(Constants.DAM_ERRORS));
			){
			
			// configuring control topic
			ctrl.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message msg) {
					if (msg instanceof TextMessage) {
						try {
							// take the poison - shutdown reaction to text message with the POISON string
							if (Constants.POISON.equalsIgnoreCase(((TextMessage) msg).getText())) {
								log.debug("kill command!");
								close();
							}
						} catch (JMSException e) {
							log.error(e.toString(),e);
						}
					}
				}
			});

			// configuring deref topic
			deref.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message msg) {
					try {
						log.debug(msg);
						
						// is ObjectMessage?
						if (msg instanceof ObjectMessage) {
							Serializable content = ((ObjectMessage)msg).getObject();
							
							log.debug("received: {}", content);
							
							// is deref command?
							if(content instanceof DerefMessage) {
								String iri = ((DerefMessage)content).getIRI();
								log.debug("retrieving: {}", iri);
								
								ListenableFuture<String> future = service.submit(new HttpAccessWorker(iri));
								Futures.addCallback(future, new FutureCallback<String>() {

									@Override
									public void onSuccess(String result) {
										log.debug("successful: {}", iri);
										
										try {
											data.send(session.createTextMessage(result));
										} catch (JMSException e) {
											log.error(e.toString(),e);
										}
									}

									@Override
									public void onFailure(Throwable t) {
										log.debug("failed: {} ", t);
										
										try {
											data.send(session.createObjectMessage(t));
										} catch (JMSException e) {
											log.error(e.toString(),e);
										}
									}
									
								});
							}
						}
					} catch (Exception e) {
						log.error(e);
					}
				}
				
			});
			connection.start();
			
			log.debug("running");

			// wait until end of time... or interrupt
			while(!Thread.currentThread().isInterrupted() && running) {}
				
			log.debug("finished");
		} catch (JMSException e) {
			log.error(e.toString(),e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() {
		try {
			service.shutdown();
			if(!service.awaitTermination(30, TimeUnit.SECONDS)) {
				service.shutdownNow();
			}
		} catch(InterruptedException e) {
			log.error(e);
		}
		
		running = false;
	}
}
