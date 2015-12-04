package de.unikoblenz.west.koldfish.dam.messages;

/**
 * declarative message for communication between Negotiator and Receiver.
 * 
 * @author lkastler@uni-koblenz.de
 *
 * @param <T> - type of payload
 */
public interface ReportMessage<T> extends IRIMessage {

	/**
	 * returns the payload of this message.
	 * @return the payload of this message.
	 */
	public T getPayload();
	
	public Object clone() throws CloneNotSupportedException;
}
