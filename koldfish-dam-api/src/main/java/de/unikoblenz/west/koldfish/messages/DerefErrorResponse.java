/**
 * 
 */
package de.unikoblenz.west.koldfish.messages;

import de.unikoblenz.west.koldfish.dam.ErrorResponse;

/**
 * implementation of the ErrorResponse interface as Exception
 * 
 * @author lkastler
 *
 */
public class DerefErrorResponse extends Exception implements ErrorResponse {

  private static final long serialVersionUID = -1383761794860945562L;

  private final long encodedIri;

  public DerefErrorResponse(long encodedIri, String message, Throwable cause) {
    super(message, cause);
    this.encodedIri = encodedIri;
  }

  public DerefErrorResponse(long encodedIri, String message) {
    super(message);
    this.encodedIri = encodedIri;
  }

  public DerefErrorResponse(long encodedIri, Throwable cause) {
    super(cause);
    this.encodedIri = encodedIri;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.ErrorResponse#getException()
   */
  @Override
  public Exception getException() {
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.ErrorResponse#getDerefIri()
   */
  @Override
  public long getEncodedDerefIri() {
    return encodedIri;
  }

  @Override
  public String toString() {
    return "ErrorResponse [compressedIri=" + encodedIri + ", message()=" + getMessage() + "]";
  }


}
