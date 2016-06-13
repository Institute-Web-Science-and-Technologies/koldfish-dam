/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.jms.JMSException;

import org.apache.jena.iri.IRI;

import de.unikoblenz.west.koldfish.dam.DataAccessModule;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleException;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleListener;
import de.unikoblenz.west.koldfish.dam.DerefEncodedMessage;
import de.unikoblenz.west.koldfish.dam.DerefMessage;
import de.unikoblenz.west.koldfish.messaging.QueueSender;
import de.unikoblenz.west.koldfish.serverSkeletons.RequestHandler;

/**
 * DataAccessModule implementation for JMS
 * 
 * @author lkastler
 */
public class JmsDataAccessModule implements DataAccessModule {

  private final List<DataAccessModuleListener> listeners =
      Collections.synchronizedList(new LinkedList<DataAccessModuleListener>());

  private final QueueSender deref;
  private final RequestHandler errors;
  private final RequestHandler damOutput;


  /**
   * creates a new JmsDataAccessModule object with the given DataAccessModuleListener
   * 
   * @throws Exception triggered if connection to JMS could not be created.
   */
  public JmsDataAccessModule() throws Exception {
    deref = new QueueSender("admin", "admin", "tcp://141.26.208.203:61616", "dam.deref");
    errors = new ErrorRequestHandler(listeners);

    damOutput = new DataRequestHandler(listeners);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.LifeCycle#start()
   */
  @Override
  public void start() throws Exception {}

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.LifeCycle#isStarted()
   */
  @Override
  public boolean isStarted() {
    return errors.isAlive() && damOutput.isAlive();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.DataAccessModule#deref(org.apache.jena.iri.IRI)
   */
  @Override
  public void deref(IRI iri) throws DataAccessModuleException {
    try {
      deref.sendMessage(new DerefMessage(iri.toString()));
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
      deref.sendMessage(new DerefEncodedMessage(compressedIri));
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
    deref.close();
    errors.close();
    damOutput.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikoblenz.west.koldfish.dam.DataAccessModule#addListener(de.unikoblenz.west.koldfish.dam
   * .DataAccessModuleListener)
   */
  @Override
  public synchronized void addListener(DataAccessModuleListener listener) {
    listeners.add(listener);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikoblenz.west.koldfish.dam.DataAccessModule#removeListener(de.unikoblenz.west.koldfish
   * .dam.DataAccessModuleListener)
   */
  @Override
  public synchronized void removeListener(DataAccessModuleListener listener) {
    listeners.remove(listener);
  }
}
