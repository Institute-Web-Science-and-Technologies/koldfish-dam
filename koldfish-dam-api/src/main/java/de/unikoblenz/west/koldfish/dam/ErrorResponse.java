/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

import de.uni_koblenz.west.koldfish.messaging.KoldfishMessage;

/**
 * response when the DAM backend unsuccessfully tried to dereference an IRI.
 * 
 * @author lkastler
 */
public interface ErrorResponse extends KoldfishMessage {

	/**
	 * returns the thrown Exception causing the IRI deref to fail. 
	 * @return the thrown Exception causing the IRI deref to fail. 
	 */
	public Exception getException();
	
}
