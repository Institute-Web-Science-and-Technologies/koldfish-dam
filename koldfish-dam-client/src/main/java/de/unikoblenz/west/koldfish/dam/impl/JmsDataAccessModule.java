/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import javax.jms.JMSException;

import org.apache.jena.iri.IRI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.dam.DataAccessModule;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleException;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleListener;
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

  /**
   * creates a new JmsDataAccessModule object with the given DataAccessModuleListener
   * @param listener - listener for this JmsDataAccessModule object.
   * @throws Exception triggered if connection to JMS could not be created.
   */
  public JmsDataAccessModule(DataAccessModuleListener listener) throws Exception {
    ConnectionManager.get().createTopic("dam.data", msg -> {
      if (msg instanceof DerefResponse) {
        DerefResponse derefResp = (DerefResponse) msg;
        log.debug("message: {}", derefResp);

        listener.onDerefResponse(derefResp);
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
      log.error(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.DataAccessModule#deref(long)
   */
  @Override
  public void deref(long compressedIri) throws DataAccessModuleException {
    throw new UnsupportedOperationException("implement deref");
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
}
