package de.unikoblenz.west.koldfish.dam;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.dam.impl.DerefRequestHandler;
import de.unikoblenz.west.koldfish.dam.impl.NxEncodingParser;
import de.unikoblenz.west.koldfish.dictionary.Dictionary;
import de.unikoblenz.west.koldfish.messaging.TopicSender;

/**
 * responsible for JMS connections and controls data access workers.
 * 
 * @author lkastler
 */
public class DataAccessMaster {

  private static final Logger log = LogManager.getLogger(DataAccessMaster.class);

  private final DerefRequestHandler deref;
  private final TopicSender errors;
  private final TopicSender damOutput;

  /**
   * starts the DataAccessManager
   * 
   * @param args - no arguments.
   */
  public static void main(String[] args) {
    log.info("starting data access module");

    try {
      Dictionary dict = new Dictionary();

      new DataAccessMaster(dict, new NxEncodingParser(dict, DictionaryHelper.convertIri(dict, "")));

      log.debug("started");
    } catch (Exception e) {
      log.error("could not initialized DAM", e);
    }
  }

  private DataAccessMaster(Dictionary dictionary, EncodingParser parser) throws JMSException {

    errors = new TopicSender("admin", "admin", "tcp://141.26.208.203:61616", "dam.errors");
    damOutput = new TopicSender("admin", "admin", "tcp://141.26.208.203:61616", "dam.data");

    deref = new DerefRequestHandler(dictionary, parser, errors, damOutput);



    Runtime.getRuntime().addShutdownHook(new Thread(this::close));
  }



  /**
   * closes this DataAccessManager.
   */
  public void close() {
    log.debug("closing");

    try {
      deref.close();
      errors.close();
      damOutput.close();
    } catch (Exception e) {
      log.error(e);
    }
  }
}
