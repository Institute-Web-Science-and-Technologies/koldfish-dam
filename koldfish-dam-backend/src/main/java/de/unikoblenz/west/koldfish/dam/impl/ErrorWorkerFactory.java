/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import de.unikoblenz.west.koldfish.dam.ErrorWorker;
import de.unikoblenz.west.koldfish.dictionary.Dictionary;
import de.unikoblenz.west.koldfish.messaging.ConnectionManager;

/**
 * @author lkastler
 *
 */
public class ErrorWorkerFactory {

  private final Dictionary dictionary;
  private final ConnectionManager manager;
  private final String iri;

  public ErrorWorkerFactory(Dictionary dictionary, ConnectionManager manager, String iri) {
    this.dictionary = dictionary;
    this.manager = manager;
    this.iri = iri;
  }

  public ErrorWorker build(Throwable e) {
    return new ErrorWorkerImpl(dictionary, manager, iri, e);
  }
}
