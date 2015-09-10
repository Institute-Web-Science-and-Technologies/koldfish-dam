package de.unikoblenz.west.koldfish.dam.impl;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import de.unikoblenz.west.koldfish.dam.Negotiator;
import de.unikoblenz.west.koldfish.dam.Receiver;
import de.unikoblenz.west.koldfish.dam.except.NegotiatorException;
import de.unikoblenz.west.koldfish.dam.messages.RequestMessage;

/**
 * dummy implementation of the Negotiator interface, no functionality. 
 * @author lkastler@uni-koblenz.de
 *
 */
public class DummyNegotiator implements Negotiator<Model> {

	private static final Logger log = LoggerFactory.getLogger(DummyNegotiator.class);

	// TODO receiver impl maybe?
	
	private final LinkedList<Receiver> receivers = new LinkedList<Receiver>();
	
	@Override
	public Future<Model> request(RequestMessage rm) throws NegotiatorException {
		log.debug("request: " + rm);
		
		return new Future<Model>(){

			private boolean cancel = false;
			
			@Override
			public boolean cancel(boolean mayInterruptIfRunning) {
				log.debug("cancel request: " + mayInterruptIfRunning);
				cancel = mayInterruptIfRunning;
				return true;
			}

			@Override
			public boolean isCancelled() {
				return cancel;
			}

			@Override
			public boolean isDone() {
				return true;
			}

			@Override
			public Model get() throws InterruptedException, ExecutionException {
				return ModelFactory.createDefaultModel();
			}

			@Override
			public Model get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException,TimeoutException {
				return get();
			}
			
		};
	}

	@Override
	public void start() throws NegotiatorException {
		log.debug("start");
	}

	@Override
	public void terminate() throws NegotiatorException {
		log.debug("shutdown");
	}

	@Override
	public void addReceiver(Receiver r) {
		log.debug("add receiver: " + r);
		receivers.add(r);
	}

	@Override
	public void removeReceiver(Receiver r) {
		log.debug("remove receiver: " + r);
		receivers.remove(r);
	}
}
