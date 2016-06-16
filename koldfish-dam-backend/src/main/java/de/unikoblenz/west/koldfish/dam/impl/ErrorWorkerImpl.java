/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import de.unikoblenz.west.koldfish.dam.ErrorWorker;
import de.unikoblenz.west.koldfish.dictionary.Dictionary;
import de.unikoblenz.west.koldfish.messages.DerefErrorResponse;
import de.unikoblenz.west.koldfish.messaging.ConnectionManager;

/**
 * @author lkastler
 *
 */
public class ErrorWorkerImpl implements ErrorWorker {

  private static final Logger log = LogManager.getLogger(ErrorWorkerImpl.class);

  private final Dictionary dictionary;
  private final ConnectionManager manager;
  private final String iri;
  private final Throwable cause;

  public ErrorWorkerImpl(Dictionary dictionary, ConnectionManager manager, String iri,
      Throwable cause) {
    this.dictionary = dictionary;
    this.manager = manager;
    this.iri = iri;
    this.cause = cause;
  }



  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    log.debug("exception occured: ", cause.getLocalizedMessage());
    try {
      manager.sentToTopic("dam.errors", createErrorResponseImpl(iri, cause));
    } catch (JMSException e) {
      log.error(e);
    }
  }

  // creates ErrorResponseImpl for throwing
  private DerefErrorResponse createErrorResponseImpl(String iri, Throwable e) {
    try {
      return new DerefErrorResponse(dictionary.convertIris(Lists.newArrayList(iri)).get(0), e);
    } catch (JMSException e1) {
      return new DerefErrorResponse(0, e1);
    }
  }
}
