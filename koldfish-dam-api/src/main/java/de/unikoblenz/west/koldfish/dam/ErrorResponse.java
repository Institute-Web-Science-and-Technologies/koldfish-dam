/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;

/**
 * response when the DAM backend unsuccessfully tried to dereference an IRI.
 * 
 * @author lkastler
 */
public interface ErrorResponse extends KoldfishMessage {

  /**
   * returns encoded IRI as long that has been dereferenced.
   * 
   * @return encoded IRI as long that has been dereferenced.
   */
  public long getEncodedDerefIri();

  /**
   * returns the thrown Exception causing the IRI deref to fail.
   * 
   * @return the thrown Exception causing the IRI deref to fail.
   */
  public Exception getException();

}
