package de.unikoblenz.west.koldfish.dam.impl.messages;

import org.apache.jena.iri.IRI;

import de.unikoblenz.west.koldfish.dam.messages.ActivationMessage;

public class DereferenceActivationMessage implements ActivationMessage {

	private final IRI iri; 
	
	public DereferenceActivationMessage(IRI iri) {
		this.iri = iri;
	}
	
	@Override
	public IRI getResourceIRI() {
		return iri;
	}

	@Override
	public String toString() {
		return "DereferenceActivationMessage [iri=" + iri + "]";
	}
}
