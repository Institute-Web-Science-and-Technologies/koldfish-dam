/**
 * 
 */
package de.unikoblenz.west.koldfish.messages;

import de.unikoblenz.west.koldfish.messages.KoldfishMessage;

/**
 * @author lkastler
 *
 */
public class DerefEncodedIriMessage implements KoldfishMessage {

  private static final long serialVersionUID = 1L;

  private final long encodedIri;

  public DerefEncodedIriMessage(long encodedIri) {
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
