/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import de.unikoblenz.west.koldfish.dam.DerefEncodedMessage;
import de.unikoblenz.west.koldfish.dam.DerefMessage;
import de.unikoblenz.west.koldfish.dam.DictionaryHelper;
import de.unikoblenz.west.koldfish.dam.EncodingParser;
import de.unikoblenz.west.koldfish.dam.ErrorResponse;
import de.unikoblenz.west.koldfish.dictionary.Dictionary;
import de.unikoblenz.west.koldfish.messages.KoldfishMessage;
import de.unikoblenz.west.koldfish.messaging.TopicSender;
import de.unikoblenz.west.koldfish.serverSkeletons.RequestHandler;

/**
 * @author lkastler
 *
 */
public class DerefRequestHandler extends RequestHandler {

  private final ExecutorService service = Executors.newCachedThreadPool();
  private final Dictionary dictionary;
  private final EncodingParser parser;
  private final TopicSender errors;
  private final TopicSender damOutput;

  public DerefRequestHandler(Dictionary dictionary, EncodingParser parser, TopicSender errors,
      TopicSender damOutput) throws JMSException {
    super("admin", "admin", "tcp://141.26.208.203:61616", "dam.deref");


    this.dictionary = dictionary;
    this.parser = parser;

    this.errors = errors;
    this.damOutput = damOutput;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikoblenz.west.koldfish.serverSkeletons.RequestHandler#processMessage(de.unikoblenz.west.
   * koldfish.messages.KoldfishMessage)
   */
  @Override
  protected void processMessage(KoldfishMessage msg) {
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


  /**
   * handles a dereferenciation of an IRI as String.
   * 
   * @param iri - IRI to dereference.
   */
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
                errors.sendMessage((ErrorResponse) ex);
              } else {
                damOutput.sendMessage(result);
              }
            } catch (Exception e) {
              log.error(e);
            }
          });
    } else {
      log.error("executor already shut down");
    }
  }

  @Override
  public void close() throws Exception {
    service.shutdown();
    if (!service.awaitTermination(30, TimeUnit.SECONDS)) {
      service.shutdownNow();
    }
  }
}
