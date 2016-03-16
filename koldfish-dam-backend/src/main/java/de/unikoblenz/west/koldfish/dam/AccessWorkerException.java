/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

/**
 * @author lkastler
 *
 */
public class AccessWorkerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AccessWorkerException(Exception e) {
		super(e);
	}
}
