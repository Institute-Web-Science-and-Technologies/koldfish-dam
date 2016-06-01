/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

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
import de.unikoblenz.west.koldfish.fluid.ConnectionManager;

/**
 * DataAccessModule implementation for JMS
 * 
 * @author lkastler
 */
public class JmsDataAccessModule implements DataAccessModule {

  private static final Logger log = LogManager.getLogger(JmsDataAccessModule.class);

  private final List<DataAccessModuleListener> listeners =
      new LinkedList<DataAccessModuleListener>();

  /**
   * creates a new JmsDataAccessModule object with the given DataAccessModuleListener
   * 
   * @throws Exception triggered if connection to JMS could not be created.
   */
  public JmsDataAccessModule() throws Exception {
    ConnectionManager.get().createTopic("dam.data", msg -> {
      if (msg instanceof DerefResponse) {
        DerefResponse derefResp = (DerefResponse) msg;
        log.debug("message: {}", derefResp);

        for (DataAccessModuleListener listener : listeners) {
          listener.onDerefResponse(derefResp);
        }

      }
    }).createTopic("dam.errors", msg -> {
      log.warn(msg);
    });
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.LifeCycle#start()
   */
  @Override
  public void start() throws Exception {
    ConnectionManager.get().start();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.LifeCycle#isStarted()
   */
  @Override
  public boolean isStarted() {
    return ConnectionManager.get().isConnectionActive();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.DataAccessModule#deref(org.apache.jena.iri.IRI)
   */
  @Override
  public void deref(IRI iri) throws DataAccessModuleException {
    try {
      ConnectionManager.get().sendToQueue("dam.deref", new DerefMessage(iri.toString()));
    } catch (JMSException e) {
      throw new DataAccessModuleException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.DataAccessModule#deref(long)
   */
  @Override
  public void deref(long compressedIri) throws DataAccessModuleException {
    try {
      ConnectionManager.get().sendToQueue("dam.deref", new DerefEncodedMessage(compressedIri));
    } catch (JMSException e) {
      throw new DataAccessModuleException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.LifeCycle#stop()
   */
  @Override
  public void stop() throws Exception {
    ConnectionManager.get().close();
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
    throw new UnsupportedOperationException("implement addListener");
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
    throw new UnsupportedOperationException("implement removeListener");
  }
}
