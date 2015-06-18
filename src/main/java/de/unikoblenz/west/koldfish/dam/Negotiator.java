package de.unikoblenz.west.koldfish.dam;

import java.util.concurrent.Future;

import de.unikoblenz.west.koldfish.dam.except.NegotiatorException;
import de.unikoblenz.west.koldfish.dam.messages.RequestMessage;

/**
 * brokers between Accessor and Controller.
 * Manages connected Accessors and their tasks. 
 * 
 * @author lkastler@uni-koblenz.de
 *
 * @param <T> - data that is returned.
 */
public interface Negotiator<T> {

	public Future<T> request(RequestMessage rm) throws NegotiatorException;
	
	/**
	 * starts this Negotiator. 
	 * 
	 * @throws NegotiatorException thrown if this Negotiator could not be started.
	 */
	public void start() throws NegotiatorException;
	
	/**
	 * shuts this Negotiator down.
	 * 
	 * @throws NegotiatorException thrown if this Negotiator could not be stopped properly.
	 */
	public void shutdown() throws NegotiatorException;
	
	/**
	 * adds a Receiver to this Negotiator to be informed about Reports.
	 * 
	 * @param r - Receiver to add.
	 */
	public void addReceiver(Receiver r);
	
	/**
	 * removes a Receiver frome this Negotiator.
	 * 
	 * @param r - Receiver to remove.
	 */
	public void removeReceiver(Receiver r);
}
