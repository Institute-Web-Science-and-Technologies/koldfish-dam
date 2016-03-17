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

	/**
	 * orders the DAM backend to dereference the given IRI.
	 * @param iri - IRI to dereference.
	 * @throws DataAccessModuleException thrown if this DataAccessModule was unable to notify the DAM backend.
	 */
	public void deref(IRI iri) throws DataAccessModuleException;
	
	/**
	 * orders the DAM backend to dereference the given compressed IRI representation.
	 * @param iri - compressed IRI representation to dereference.
	 * @throws DataAccessModuleException thrown if this DataAccessModule was unable to notify the DAM backend.
	 */
	public void deref(long compressedIri) throws DataAccessModuleException;
	
	/**
	 * sets given DataAccessModuleListener to listen on this DataAccessModule.
	 * @param listener - DataAccessModuleListener to listen on this DataAccessModule.
	 */
	public void setListener(DataAccessModuleListener listener);
}
