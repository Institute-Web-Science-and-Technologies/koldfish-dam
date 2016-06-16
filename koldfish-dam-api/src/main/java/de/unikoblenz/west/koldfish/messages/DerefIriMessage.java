/**
 * 
 */
package de.unikoblenz.west.koldfish.messages;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;

/**
 * message for dereferencing an IRI.
 * 
 * @author lkastler
 */
public class DerefIriMessage implements KoldfishMessage {

  private static final long serialVersionUID = 1L;

  private final String iri;

  /**
   * creates a dereferencing message for given IRI.
   * 
   * @param iri - IRI to dereference
   */
  public DerefIriMessage(String iri) {
    this.iri = iri;
  }

  /**
   * returns IRI to dereference.
   * 
   * @return IRI to dereference.
   */
  public String getIRI() {
    return iri;
  }

  @Override
  public String toString() {
    return "DerefMessage [" + iri + "]";
  }


}
