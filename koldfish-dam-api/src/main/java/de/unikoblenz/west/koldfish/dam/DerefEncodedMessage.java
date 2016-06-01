/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;

/**
 * @author lkastler
 *
 */
public class DerefEncodedMessage implements KoldfishMessage {

  private static final long serialVersionUID = 1L;

  private final long encodedIri;

  public DerefEncodedMessage(long encodedIri) {
    this.encodedIri = encodedIri;
  }

  public long getEncodedIri() {
    return encodedIri;
  }

  @Override
  public String toString() {
    return "DerefEncodedMessage [encodedIri=" + encodedIri + "]";
  }
}
