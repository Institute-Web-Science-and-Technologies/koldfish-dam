/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.cli;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.JMSException;

import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIException;
import org.apache.jena.iri.IRIFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.primitives.Longs;

import de.unikoblenz.west.koldfish.dam.DataAccessModule;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleException;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleListener;
import de.unikoblenz.west.koldfish.dam.ErrorResponse;
import de.unikoblenz.west.koldfish.dam.impl.JmsDataAccessModule;
import de.unikoblenz.west.koldfish.dictionary.Dictionary;
import de.unikoblenz.west.koldfish.messages.DerefResponse;

/**
 * @author lkastler
 *
 */
public class DataAccessModuleMain {

  private static final Logger log = LogManager.getLogger(DataAccessModuleMain.class);

  public static void main(String[] args) throws Exception {
    List<IRI> iris = new LinkedList<IRI>();

    for (String iri : args) {
      try {
        iris.add(IRIFactory.iriImplementation().construct(iri));
      } catch (IRIException e) {
        log.warn("{} is not a proper IRI", iri);
      }
    }

    DataAccessModuleMain main = new DataAccessModuleMain();

    main.handle(iris);

  }

  private volatile AtomicInteger uris = new AtomicInteger();

  private DataAccessModule dam;

  private DataAccessModuleMain() throws Exception {
    dam = new JmsDataAccessModule();

    dam.addListener(new DataAccessModuleListener() {

      Dictionary dict = new Dictionary();

      @Override
      public void onDerefResponse(DerefResponse response) {
        for (long[] items : response) {
          try {
            log.debug(dict.convertIds(Longs.asList(items)));
          } catch (JMSException e) {
            log.error(e);
          }
        }
      }

      @Override
      public void onErrorResponse(ErrorResponse response) {
        log.error(response.getException());
      }

    });

    dam.start();
  }

  private void handle(List<IRI> iris) {
    uris.addAndGet(iris.size());

    for (IRI iri : iris) {
      log.info("trying to deref IRI {}", iri);
      try {
        dam.deref(iri);
      } catch (DataAccessModuleException e) {
        log.error(e);
        uris.decrementAndGet();
      }
    }
  }

}
