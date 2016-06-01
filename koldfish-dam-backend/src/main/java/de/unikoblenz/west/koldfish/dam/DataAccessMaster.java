package de.unikoblenz.west.koldfish.dam;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.dam.impl.HttpAccessWorker;
import de.unikoblenz.west.koldfish.dam.impl.NxEncodingParser;
import de.unikoblenz.west.koldfish.dictionary.Dictionary;
import de.unikoblenz.west.koldfish.fluid.ConnectionManager;

/**
 * responsible for JMS connections and controls data access workers.
 * 
 * @author lkastler
 */
public class DataAccessMaster {

  private static final Logger log = LogManager.getLogger(DataAccessMaster.class);

  private final ExecutorService service = Executors.newCachedThreadPool();

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
      Dictionary dict = Dictionary.get();
      new DataAccessMaster(dict, new NxEncodingParser(dict, DictionaryHelper.convertIri(dict, "")));

      log.debug("started");
    } catch (Exception e) {
      log.error("could not initialized DAM", e);
    }
  }

  private DataAccessMaster(Dictionary dictionary, EncodingParser parser) {
    this.dictionary = dictionary;
    this.parser = parser;

    init();

    Runtime.getRuntime().addShutdownHook(new Thread(this::close));
  }

  /**
   * initializes connections.
   */
  private void init() {
    try {
      ConnectionManager.get().init().createQueue("dam.deref", new MessageListener() {
        @Override
        public void onMessage(Message msg) {
          try {
            // is ObjectMessage?
            if (msg instanceof ObjectMessage) {
              Serializable content = ((ObjectMessage) msg).getObject();

              log.debug("received: {}", content);

              // is deref command?
              if (content instanceof DerefMessage) {
                handleDeref(((DerefMessage) content).getIRI());
              } else if (content instanceof DerefEncodedMessage) {
                handleDeref(DictionaryHelper.convertId(dictionary,
                    ((DerefEncodedMessage) content).getEncodedIri()));
              }
            }
          } catch (Exception e) {
            log.error("error during processing {}:", msg);
            log.error(e);
          }
        }
      }).start();
    } catch (JMSException e) {
      log.error(e);
    }
  }

  private void handleDeref(String iri) {
    if (iri == null) {
      throw new InvalidParameterException("iri is null");
    }

    log.debug("retrieving: {}", iri);

    if (!service.isShutdown()) {
      CompletableFuture.supplyAsync(new HttpAccessWorker(dictionary, parser, iri), service)
          .whenComplete((result, ex) -> {
            try {
              if (ex != null && ex instanceof ErrorResponse) {
                ConnectionManager.get().sendToTopic("dam.errors", (ErrorResponse) ex);
              } else {
                ConnectionManager.get().sendToTopic("dam.data", result);
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
   * closes this DataAccessManager.
   */
  public void close() {
    log.debug("closing");

    service.shutdown();
    try {
      if (service.awaitTermination(60, TimeUnit.SECONDS)) {
        service.shutdownNow();
      }
    } catch (InterruptedException e) {
      log.error(e);
    }
  }
}
