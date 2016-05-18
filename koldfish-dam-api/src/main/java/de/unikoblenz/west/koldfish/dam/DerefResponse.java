/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;

/**
 * response from the DAM backend when successfully dereferenced a given IRI.
 * 
 * @author lkastler
 *
 */
public interface DerefResponse extends KoldfishMessage, Iterable<long[]> {

  /**
   * returns IRI that has been dereferenced.
   * 
   * @return IRI that has been dereferenced.
   */
  public long getDerefIri();
}
