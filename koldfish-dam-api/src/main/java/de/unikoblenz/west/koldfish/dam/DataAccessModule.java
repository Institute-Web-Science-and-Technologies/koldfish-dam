/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

import org.apache.jena.iri.IRI;

import de.unikoblenz.west.koldfish.LifeCycle;
import de.unikoblenz.west.koldfish.services.KoldfishServiceAPI;

/**
 * abstraction for the Koldfish Data Access Module.
 * 
 * @author lkastler
 */
public interface DataAccessModule extends LifeCycle, KoldfishServiceAPI {

  /**
   * orders the DAM backend to dereference the given IRI.
   * 
   * @param iri - IRI to dereference.
   * @throws DataAccessModuleException thrown if this DataAccessModule was unable to notify the DAM
   *         back-end.
   */
  public void deref(IRI iri) throws DataAccessModuleException;

  /**
   * orders the DAM back-end to dereference the given encoded IRI representation.
   * 
   * @param encodedIri - compressed IRI representation to dereference.
   * @throws DataAccessModuleException thrown if this DataAccessModule was unable to notify the DAM
   *         back-end.
   */
  public void deref(long encodedIri) throws DataAccessModuleException;

  /**
   * adds given DataAccessModuleListener to the active listeners of this DataAccessModule.
   * 
   * @param listener - listener to add.
   */
  public void addListener(DataAccessModuleListener listener);

  /**
   * removes given DataAccessModuleListener from the active listeners of this DataAccessModule.
   * 
   * @param listener - listener to remove.
   */
  public void removeListener(DataAccessModuleListener listener);
}
