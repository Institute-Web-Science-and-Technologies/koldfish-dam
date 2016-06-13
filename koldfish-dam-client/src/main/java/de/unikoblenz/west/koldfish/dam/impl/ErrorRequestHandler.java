/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.util.List;

import javax.jms.JMSException;

import de.unikoblenz.west.koldfish.dam.DataAccessModuleListener;
import de.unikoblenz.west.koldfish.dam.ErrorResponse;
import de.unikoblenz.west.koldfish.messages.KoldfishMessage;
import de.unikoblenz.west.koldfish.serverSkeletons.RequestHandler;

/**
 * @author lkastler
 *
 */
class ErrorRequestHandler extends RequestHandler {

  private final List<DataAccessModuleListener> listeners;

  ErrorRequestHandler(List<DataAccessModuleListener> listeners) throws JMSException {
    super("admin", "admin", "tcp://tcp://141.26.208.203:61616", "dam.errors");

    this.listeners = listeners;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikoblenz.west.koldfish.serverSkeletons.RequestHandler#processMessage(de.unikoblenz.west.
   * koldfish.messages.KoldfishMessage)
   */
  @Override
  protected void processMessage(KoldfishMessage message) {
    log.warn(message);

    if (message instanceof ErrorResponse) {
      ErrorResponse errorResp = (ErrorResponse) message;

      synchronized (listeners) {
        for (DataAccessModuleListener listener : listeners) {
          listener.onErrorResponse(errorResp);
        }
      }
    }
  }

}
