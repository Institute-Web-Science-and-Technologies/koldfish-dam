package de.unikoblenz.west.koldfish.dam.impl.messages;

import org.apache.jena.iri.IRI;
import org.apache.jena.rdf.model.Model;

import de.unikoblenz.west.koldfish.dam.messages.ReportMessage;

/**
 * reports a Model to registered Receiver objects.
 *  
 * @author lkastler@uni-koblenz.de
 *
 */
public class ModelReportMessage implements ReportMessage<Model> {

	private final IRI iri;
	private final Model m;
	
	/**
	 * creates a new ReportModelMessage with given Model as payload.
	 * @param m - the Payload
	 */
	public ModelReportMessage(IRI iri, Model m) {
		this.iri = iri;
		this.m = m;
	}
	
	@Override
	public IRI getResourceIRI() {
		return iri;
	}
	
	@Override
	public Model getPayload() {
		return m;
	}

	@Override
	public String toString() {
		return "ReportModelMessage [iri=" + iri +"; model=" + m + "]";
	}
}
