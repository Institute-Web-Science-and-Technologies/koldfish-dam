/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

/**
 * exception for EncodingParser malfunctions.
 * 
 * @author lkastler
 */
public class ParserException extends Exception {
  private static final long serialVersionUID = -5142174048358348377L;

  public ParserException(Throwable cause) {
    super(cause);
  }

  public ParserException(String message) {
    super(message);
  }

}
