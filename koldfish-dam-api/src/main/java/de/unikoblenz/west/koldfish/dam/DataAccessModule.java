/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

import org.apache.jena.iri.IRI;

/**
 * abstraction for the Koldfish Data Access Module.
 * 
 * @author lkastler
 */
public interface DataAccessModule extends LifeCycle {

	public void deref(IRI iri) throws DataAccessModuleException;
	
	public void addListener(DataAccessModuleListener listener);
}
