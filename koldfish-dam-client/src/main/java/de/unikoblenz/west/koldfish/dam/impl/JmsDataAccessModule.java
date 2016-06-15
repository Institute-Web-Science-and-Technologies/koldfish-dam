/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.jms.JMSException;

import org.apache.jena.iri.IRI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.dam.DataAccessModule;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleException;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleListener;
import de.unikoblenz.west.koldfish.dam.DerefEncodedMessage;
import de.unikoblenz.west.koldfish.dam.DerefMessage;
import de.unikoblenz.west.koldfish.dam.DerefResponse;
import de.unikoblenz.west.koldfish.dam.ErrorResponse;
import de.unikoblenz.west.koldfish.messages.KoldfishMessage;
import de.unikoblenz.west.koldfish.messaging.ConnectionManager;
import de.unikoblenz.west.koldfish.messaging.KoldfishMessageListener;
import de.unikoblenz.west.koldfish.messaging.impl.ConnectionManagerImpl;

/**
 * DataAccessModule implementation for JMS
 * 
 * @author lkastler
 */
public class JmsDataAccessModule implements DataAccessModule {

  private static final Logger log = LogManager.getLogger(JmsDataAccessModule.class);

  private final List<DataAccessModuleListener> listeners =
      Collections.synchronizedList(new LinkedList<DataAccessModuleListener>());

  private final ConnectionManager manager;


  /**
   * creates a new JmsDataAccessModule object with the given DataAccessModuleListener
   * 
   * @throws Exception triggered if connection to JMS could not be created.
   */
  public JmsDataAccessModule() throws Exception {
    manager = new ConnectionManagerImpl();
    manager.topicReceiver("dam.errors").addListener(new KoldfishMessageListener() {
      @Override
      public void onMessage(KoldfishMessage msg) {
        log.debug(msg);

        if (msg instanceof ErrorResponse) {
          ErrorResponse errorResp = (ErrorResponse) msg;

          synchronized (listeners) {
            for (DataAccessModuleListener listener : listeners) {
              listener.onErrorResponse(errorResp);
            }
          }
        }
      }

    });
    manager.topicReceiver("dam.data").addListener(new KoldfishMessageListener() {


      @Override
      public void onMessage(KoldfishMessage msg) {
        if (msg instanceof DerefResponse) {
          DerefResponse derefResp = (DerefResponse) msg;
          log.debug("message: {}", derefResp);

          synchronized (listeners) {
            for (DataAccessModuleListener listener : listeners) {
              listener.onDerefResponse(derefResp);
            }
          }
        }
      }


    });

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.LifeCycle#start()
   */
  @Override
  public void start() throws Exception {
    manager.start();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.LifeCycle#isStarted()
   */
  @Override
  public boolean isStarted() {
    return manager.isStarted();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.LifeCycle#stop()
   */
  @Override
  public void stop() throws Exception {
    manager.stop();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.DataAccessModule#deref(org.apache.jena.iri.IRI)
   */
  @Override
  public void deref(IRI iri) throws DataAccessModuleException {
    deref(new DerefMessage(iri.toString()));
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.DataAccessModule#deref(long)
   */
  @Override
  public void deref(long compressedIri) throws DataAccessModuleException {
    deref(new DerefEncodedMessage(compressedIri));
  }

  private void deref(KoldfishMessage msg) throws DataAccessModuleException {
    try {
      manager.sentToQueue("dam.deref", msg);
    } catch (JMSException e) {
      throw new DataAccessModuleException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikoblenz.west.koldfish.dam.DataAccessModule#addListener(de.unikoblenz.west.koldfish.dam
   * .DataAccessModuleListener)
   */
  @Override
  public void addListener(DataAccessModuleListener listener) {
    synchronized (listeners) {
      listeners.add(listener);
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikoblenz.west.koldfish.dam.DataAccessModule#removeListener(de.unikoblenz.west.koldfish
   * .dam.DataAccessModuleListener)
   */
  @Override
  public void removeListener(DataAccessModuleListener listener) {
    synchronized (listeners) {
      listeners.remove(listener);
    }
  }
}
