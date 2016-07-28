/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.dam.DataAccessWorker;
import de.unikoblenz.west.koldfish.messages.KoldfishMessage;
import de.unikoblenz.west.koldfish.messaging.ConnectionManager;

/**
 * Runnable implementation for handling the complete deref process, including exception handling.
 * 
 * @author lkastler
 */
public class DerefResponseWorker implements Runnable {

  private static final Logger log = LogManager.getLogger(DerefResponseWorker.class);

  private final ConnectionManager manager;
  private final ExecutorService service;

  private final DataAccessWorker<? extends KoldfishMessage> response;
  private final ErrorWorkerFactory errorFactory;

  /**
   * creates new DerefResponseWorker object.
   * 
   * @param manager - manager for sending messages to JMS.
   * @param service - ExecutorService for handling concurrent execution.
   * @param dataAccess - Worker to access the data.
   * @param errorFactory - factory to create an ErrorWorker for exception handling.
   */
  public <T extends KoldfishMessage> DerefResponseWorker(ConnectionManager manager,
      ExecutorService service, DataAccessWorker<T> dataAccess, ErrorWorkerFactory errorFactory) {
    this.manager = manager;
    this.service = service;
    this.response = dataAccess;
    this.errorFactory = errorFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    if (!service.isShutdown()) {
      try {
        manager.sentToTopic("dam.data", service.submit(response).get());
        log.debug("response sent");
      } catch (Throwable e) {
        log.error(e);
        service.execute(errorFactory.build(e));
      }
    }
  }
}
