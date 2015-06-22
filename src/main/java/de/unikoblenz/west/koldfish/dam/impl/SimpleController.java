package de.unikoblenz.west.koldfish.dam.impl;

import java.util.concurrent.Future;

import org.apache.jena.iri.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;

import de.unikoblenz.west.koldfish.dam.Controller;
import de.unikoblenz.west.koldfish.dam.Negotiator;
import de.unikoblenz.west.koldfish.dam.except.ControllerException;
import de.unikoblenz.west.koldfish.dam.except.NegotiatorException;
import de.unikoblenz.west.koldfish.dam.impl.messages.DereferenceRequestMessage;

/**
 * example implementation of the Controller interface.
 * @author lkastler@uni-koblenz.de
 */
public class SimpleController implements Controller<Model> {

	private static final Logger log = LoggerFactory.getLogger(SimpleController.class);
	
	private final Negotiator<Model> negotiator;
	
	/**
	 * constructor for a SimpleController with given Negotiator.
	 * @param negotiator
	 */
	public SimpleController(Negotiator<Model> negotiator) {
		this.negotiator = negotiator;
		log.debug("created");
	}

	@Override
	public Model deref(IRI resourceIRI) throws ControllerException {
		try {
			Future<Model> future = derefAsync(resourceIRI);
			return future.get();
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ControllerException(e);
		}
	}

	@Override
	public Future<Model> derefAsync(IRI resourceIRI) throws ControllerException {
		try {
			return negotiator.request(new DereferenceRequestMessage(resourceIRI));
		} catch (NegotiatorException e) {
			log.error(e.toString(), e);
			throw new ControllerException(e);
		}
	}

	@Override
	public String toString() {
		return "SimpleController [negotiator=" + negotiator + "]";
	}
}
