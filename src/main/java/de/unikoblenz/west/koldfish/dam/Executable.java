package de.unikoblenz.west.koldfish.dam;

/**
 * provides an interface for things that can be started and terminated.
 * 
 * @author lkastler@uni-koblenz.de
 *
 * @param <T> - exceptions for flow control
 */
public interface Executable <T extends Exception> {

	/**
	 * starts the object.
	 * @throws T - thrown if something went wrong.
	 */
	public void start() throws T;
	
	/**
	 * terminates an object.
	 * @throws T - thrown if something went wrong.
	 */
	public void terminate() throws T;
}
