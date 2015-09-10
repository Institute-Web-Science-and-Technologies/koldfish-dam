package de.unikoblenz.west.koldfish.dam.impl.messages;

import org.apache.jena.iri.IRI;

import de.unikoblenz.west.koldfish.dam.messages.IRIMessage;
import de.unikoblenz.west.koldfish.dam.messages.RequestMessage;

/**
 * issues an IRI derefencing request.
 * 
 * @author lkastler@uni-koblenz.de
 *
 */
public class DereferenceRequestMessage implements IRIMessage, RequestMessage {

	private final IRI resourceIRI;
	private final IRI endpoint;
	
	public DereferenceRequestMessage(IRI resourceIRI, IRI endpoint) {
		this.resourceIRI = resourceIRI;
		this.endpoint = endpoint;
	}
	
	public DereferenceRequestMessage(IRI resourceIRI) {
		this(resourceIRI, resourceIRI);
	}

	/**
	 * returns the IRI of the endpoint.
	 * @return the IRI of the endpoint.
	 */

	public IRI getEndpoint() {
		return endpoint;
	}

	public IRI getResourceIRI() {
		return resourceIRI;
	}

	@Override
	public String toString() {
		return "DereferenceRequestMessage [resourceIRI=" + resourceIRI
				+ ", endpoint=" + endpoint + "]";
	}
}
