package de.unikoblenz.west.koldfish.dam.messages;

import java.io.Serializable;

/**
 * message for dereferencing an IRI.
 * 
 * @author lkastler
 */
public class DerefMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String iri;
	
	/**
	 * creates a dereferencing message for given IRI.
	 * @param iri - IRI to dereference
	 */
	public DerefMessage(String iri) {
		this.iri = iri;
	}
	
	/**
	 * returns IRI to dereference.
	 * @return IRI to dereference.
	 */
	public String getIRI() {
		return iri;
	}

	@Override
	public String toString() {
		return "DerefMessage [" + iri + "]";
	}
	
	
}
