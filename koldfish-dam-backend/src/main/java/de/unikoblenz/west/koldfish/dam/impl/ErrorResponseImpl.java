/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import de.unikoblenz.west.koldfish.dam.ErrorResponse;

/**
 * implementation of the ErrorResponse interface as Exception
 * 
 * @author lkastler
 *
 */
public class ErrorResponseImpl extends Exception implements ErrorResponse {

  private static final long serialVersionUID = 1L;

  private final long encodedIri;

  public ErrorResponseImpl(long encodedIri, String message, Throwable cause) {
    super(message, cause);
    this.encodedIri = encodedIri;
  }

  public ErrorResponseImpl(long encodedIri, String message) {
    super(message);
    this.encodedIri = encodedIri;
  }

  public ErrorResponseImpl(long encodedIri, Throwable cause) {
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
    return "ErrorResponseImpl [compressedIri=" + encodedIri + ", message()=" + getMessage() + "]";
  }


}
