/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

/**
 * 
 * @author lkastler
 */
public class DataAccessModuleException extends Exception {

	private static final long serialVersionUID = 1L;

	public DataAccessModuleException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataAccessModuleException(String message) {
		super(message);
	}

	public DataAccessModuleException(Throwable cause) {
		super(cause);
	}

	
}
