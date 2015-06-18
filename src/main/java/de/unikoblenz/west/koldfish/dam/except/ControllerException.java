package de.unikoblenz.west.koldfish.dam.except;

/**
 * defines exceptions resulting from Controller malfunctions.
 * 
 * @author lkastler@uni-koblenz.de
 */
public class ControllerException extends Exception {

	private static final long serialVersionUID = 4978893487613966484L;

	// TODO add doc
	public ControllerException(Throwable e) {
		super(e);
	}
}
