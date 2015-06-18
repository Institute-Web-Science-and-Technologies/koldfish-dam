package de.unikoblenz.west.koldfish.dam.messages;

import org.apache.jena.iri.IRI;

/**
 * provides interface for communication to an IRI.
 * 
 * @author lkastler@uni-koblenz.de
 */
public interface IRIMessage extends Message {
	
	/**
	 * returns the IRI of resource.
	 * @return the IRI of resource.
	 */
	public IRI getResourceIRI();
}
