package de.unikoblenz.west.koldfish.dam;

/**
 * current constants file, should be removed eventually.
 * 
 * @author lkastler
 */
public interface Constants {

	/**
	 * topic for control flow to all Data Access Masters
	 */
	public static final String DAM_CONTROL = "dam.ctrl";
	/**
	 * queue for IRI dereferences
	 */
	public static final String DAM_DEREF = "dam.deref";
	/**
	 * topic for retrieved data
	 */
	public static final String DAM_DATA = "dam.data";
	/**
	 * topic for errors
	 */
	public static final String DAM_ERRORS = "dam.errors";
	// FIXME cheat, this is the standard content when using ActiveMQ sent message...
	public static final String POISON = "Enter some text here for the message body...";
}
