package de.unikoblenz.west.koldfish.dam.except;

/**
 * defines exceptions resulting from Accessor malfunctions.
 * 
 * @author lkastler@uni-koblenz.de
 *
 */
public class AccessorException extends Exception {

	private static final long serialVersionUID = 1L;
	
	// TODO add doc
	public AccessorException(String message, Throwable cause) {
		super(message, cause);
	}

}
