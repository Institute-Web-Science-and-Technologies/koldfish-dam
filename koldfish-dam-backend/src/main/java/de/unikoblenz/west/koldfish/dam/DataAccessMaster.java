package de.unikoblenz.west.koldfish.dam;

import java.security.InvalidParameterException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.dam.impl.DerefResponseWorker;
import de.unikoblenz.west.koldfish.dam.impl.ErrorWorkerFactory;
import de.unikoblenz.west.koldfish.dam.impl.HttpAccessWorker;
import de.unikoblenz.west.koldfish.dam.impl.JenaEncodingParser;
import de.unikoblenz.west.koldfish.dictionary.Dictionary;
import de.unikoblenz.west.koldfish.messages.DerefEncodedIriMessage;
import de.unikoblenz.west.koldfish.messages.DerefIriMessage;
import de.unikoblenz.west.koldfish.messages.KoldfishMessage;
import de.unikoblenz.west.koldfish.messaging.ConnectionManager;
import de.unikoblenz.west.koldfish.messaging.KoldfishMessageListener;
import de.unikoblenz.west.koldfish.messaging.impl.ConnectionManagerImpl;

/**
 * responsible for JMS connections and controls data access workers.
 * 
 * @author lkastler
 */
public class DataAccessMaster {

  private static final Logger log = LogManager.getLogger(DataAccessMaster.class);

  private final ExecutorService service = Executors.newCachedThreadPool();

  private final PoolingHttpClientConnectionManager httpConnectionManager =
      new PoolingHttpClientConnectionManager();

  private final RequestConfig config;

  private final ConnectionManager manager;

  private final Dictionary dictionary;
  private final EncodingParser parser;

  /**
   * starts the DataAccessManager
   * 
   * @param args - no arguments.
   */
  public static void main(String[] args) {
    log.info("starting data access module");

    try {
      // set up dictionary
      Dictionary dict = new Dictionary();

      // set up encoding parser
      EncodingParser parser = new JenaEncodingParser(dict, DictionaryHelper.convertIri(dict, ""));


      // create master
      new DataAccessMaster(dict, parser);
    } catch (Exception e) {
      log.error("could not initialized DAM", e);
    }
  }

  private DataAccessMaster(Dictionary dictionary, EncodingParser parser) throws Exception {
    this.dictionary = dictionary;
    this.parser = parser;

    // setting request config
    config = RequestConfig.custom().setConnectTimeout(10000).build();

    manager = new ConnectionManagerImpl();
    manager.queueReceiver("dam.deref").addListener(new KoldfishMessageListener() {

      @Override
      public void onMessage(KoldfishMessage msg) {
        log.debug("received: {}", msg);
        try {
          if (msg instanceof DerefIriMessage) {
            handleDeref(((DerefIriMessage) msg).getIRI());
          } else if (msg instanceof DerefEncodedIriMessage) {
            handleDeref(DictionaryHelper.convertId(dictionary,
                ((DerefEncodedIriMessage) msg).getEncodedIri()));
          }
        } catch (Exception e) {
          log.warn("error during processing {}: {}", msg, e.getMessage());
        }
      }

    });

    log.debug("created");

    manager.start();

    log.info("started");

  }

  /**
   * handles a dereferencing of an IRI as String.
   * 
   * @param iri - IRI to dereference.
   */
  private void handleDeref(String iri) {
    if (iri == null) {
      throw new InvalidParameterException("iri is null");
    }

    log.debug("deref: {}", iri);

    if (!service.isShutdown()) {
      service.execute(new DerefResponseWorker(manager, service,
          new HttpAccessWorker(dictionary, parser, iri,
              HttpClientBuilder.create().setConnectionManager(httpConnectionManager)
                  .setConnectionManagerShared(true).setDefaultRequestConfig(config)),
          new ErrorWorkerFactory(dictionary, manager, iri)));

    } else {
      log.error("executor already shut down");
    }
  }

  /**
   * closes this DataAccessManager.
   */
  public void close() {
    log.debug("closing");

    try {
      httpConnectionManager.shutdown();
    } catch (Exception e) {
      log.warn(e);
    }

    try {
      manager.stop();
    } catch (Exception e) {
      log.warn(e);
    }

    try {
      service.shutdown();
      if (!service.awaitTermination(30, TimeUnit.SECONDS)) {
        service.shutdownNow();
      }
    } catch (Exception e) {
      log.warn(e);
    }
    log.info("closed");
  }

  @Override
  public String toString() {
    return "DataAccessMaster []";
  }
}
