/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;


/**
 * provides methods to start and stop an object.
 * 
 * @author lkastler
 *
 */
public interface LifeCycle {

	/**
	 * starts the object.
	 * @throws Exception - thrown if something during the starting process went wrong.
	 */
	public void start() throws Exception;
	
	/**
	 * returns <code>true</code> if this object has been started, otherwise <code>false</code>.
	 * @return <code>true</code> if this object has been started, otherwise <code>false</code>.
	 */
	public boolean isStarted();
	
	/**
	 * stops the object.
	 * @throws Exception - thrown if something during the starting process went wrong.
	 */
	public void stop() throws Exception;
}
