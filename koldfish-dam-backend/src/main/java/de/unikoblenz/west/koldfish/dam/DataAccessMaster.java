package de.unikoblenz.west.koldfish.dam;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.jena.iri.IRIFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.dam.impl.ErrorWorkerImpl;
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
  private final ConnectionManager manager;
  private final Dictionary dictionary;
  private final EncodingParser parser;

  private final Map<String, Semaphore> domainSemaphores = new HashMap<String, Semaphore>();

  /**
   * starts the DataAccessManager
   * 
   * @param args - no arguments.
   */
  public static void main(String[] args) {
    log.info("starting data access module");

    try {
      Dictionary dict = new Dictionary();

      new DataAccessMaster(dict,
          new JenaEncodingParser(dict, DictionaryHelper.convertIri(dict, "")));
    } catch (Exception e) {
      log.error("could not initialized DAM", e);
    }
  }

  private DataAccessMaster(Dictionary dictionary, EncodingParser parser) throws Exception {
    this.dictionary = dictionary;
    this.parser = parser;

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
          log.error("error during processing {}:", msg);
          log.error(e);
        }
      }

    });

    log.debug("created");
    manager.start();

    log.debug("started");

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
      CompletableFuture
          .supplyAsync(new HttpAccessWorker(dictionary, parser, iri, getSemaphore(iri)), service)
          .whenComplete((result, ex) -> {
            try {
              if (ex != null) {
                service.execute(new ErrorWorkerImpl(dictionary, manager, iri, new Exception(ex)));
              } else if (result != null) {
                log.debug("success, result: {}", result);
                manager.sentToTopic("dam.data", result);
              } else {
                service.execute(new ErrorWorkerImpl(dictionary, manager, iri,
                    new Exception("something went wrong")));
              }
            } catch (Exception e) {
              log.error(e);
            }
          });
    } else {
      log.error("executor already shut down");
    }
  }

  /**
   * 
   * @param iri
   * @return
   */
  private Semaphore getSemaphore(String iri) {
    String domain = IRIFactory.iriImplementation().construct(iri).getHost();

    // question: is this necessary, because IRIs without host name should not be HTTP accessible.
    if (domain == null) {
      return new Semaphore(1);
    }
    if (domainSemaphores.containsKey(domain)) {
      domainSemaphores.put(domain, new Semaphore(4));
    }
    return domainSemaphores.get(iri);
  }

  /**
   * closes this DataAccessManager.
   */
  public void close() {
    log.debug("closing");
    try {
      manager.stop();

      service.shutdown();
      if (!service.awaitTermination(30, TimeUnit.SECONDS)) {
        service.shutdownNow();

      }

    } catch (Exception e) {
      log.error(e);
    } finally {
      log.debug("closed");
    }
  }

  @Override
  public String toString() {
    return "DataAccessMaster []";
  }
}
