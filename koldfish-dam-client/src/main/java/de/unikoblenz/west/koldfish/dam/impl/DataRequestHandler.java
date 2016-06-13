/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.util.List;

import javax.jms.JMSException;

import de.unikoblenz.west.koldfish.dam.DataAccessModuleListener;
import de.unikoblenz.west.koldfish.dam.DerefResponse;
import de.unikoblenz.west.koldfish.messages.KoldfishMessage;
import de.unikoblenz.west.koldfish.serverSkeletons.RequestHandler;

/**
 * @author lkastler
 *
 */
class DataRequestHandler extends RequestHandler {

  private final List<DataAccessModuleListener> listeners;

  DataRequestHandler(List<DataAccessModuleListener> listeners) throws JMSException {
    super("admin", "admin", "tcp://tcp://141.26.208.203:61616", "dam.data");
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
    if (message instanceof DerefResponse) {
      DerefResponse derefResp = (DerefResponse) message;
      log.debug("message: {}", derefResp);

      synchronized (listeners) {
        for (DataAccessModuleListener listener : listeners) {
          listener.onDerefResponse(derefResp);
        }
      }
    }
  }

}
