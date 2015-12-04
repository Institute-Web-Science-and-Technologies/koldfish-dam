package de.unikoblenz.west.koldfish.dam.impl.messages;

import org.apache.jena.iri.IRI;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import de.unikoblenz.west.koldfish.dam.messages.ReportMessage;

/**
 * reports a Model to registered Receiver objects.
 *  
 * @author lkastler@uni-koblenz.de
 *
 */
public class ModelReportMessage implements ReportMessage<Model> {

	private static final long serialVersionUID = 1L;
	
	private final IRI iri;
	private final Model model;
	
	/**
	 * creates a new ReportModelMessage with given Model as payload.
	 * @param iri - IRI that was called.
	 * @param m - the Payload
	 */
	public ModelReportMessage(IRI iri, Model m) {
		this.iri = iri;
		this.model = m;
	}
	
	@Override
	public IRI getResourceIRI() {
		return iri;
	}
	
	@Override
	public Model getPayload() {
		return model;
	}

	@Override
	public String toString() {
		return "ReportModelMessage [iri=" + iri +"; model=" + model + "]";
	}
	
	public Object clone() throws CloneNotSupportedException {
		Model m = ModelFactory.createDefaultModel();
		m.add(model);
		return new ModelReportMessage(iri, m);
	}
}
