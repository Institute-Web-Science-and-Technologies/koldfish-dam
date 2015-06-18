package de.unikoblenz.west.koldfish.dam;

import java.util.concurrent.Future;

import org.apache.jena.iri.IRI;

import de.unikoblenz.west.koldfish.dam.except.ControllerException;

/**
 * requests a Negotiator to initiate an RDF data access.
 * 
 * @author lkastler@uni-koblenz.de
 *
 * @param <T> - type of result
 */
public interface Controller<T> {

	/**
	 * dereferences a given resource, defined by resourceIRI, in a synchronous way.
	 * @param resourceIRI - IRI to dereference.
	 * @return dereferenced IRI in an expected result type.
	 * @throws ControllerException thrown if this Controller could not initialize the RDF data access.
	 */
	public T deref(IRI resourceIRI) throws ControllerException;
	
	/**
	 * dereferences a given resource, defined by resourceIRI, in an asynchronous way.
	 * @param resourceIRI - IRI to dereference.
	 * @return dereferenced IRI in an expected result type.
	 * @throws ControllerException thrown if this Controller could not initialize the RDF data access.
	 */
	public Future<T> derefAsync(IRI resourceIRI) throws ControllerException;
}
