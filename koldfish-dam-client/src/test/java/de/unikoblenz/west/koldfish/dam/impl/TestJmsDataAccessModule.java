/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import org.apache.jena.iri.IRIFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import de.unikoblenz.west.koldfish.dam.DataAccessModule;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleListener;
import de.unikoblenz.west.koldfish.dam.DerefResponse;
import de.unikoblenz.west.koldfish.dam.ErrorResponse;

/**
 * @author lkastler
 *
 */
public class TestJmsDataAccessModule {
  private static final Logger log = LogManager.getLogger(TestJmsDataAccessModule.class);

  private DataAccessModule jms;

  @Test
  public void test() throws Exception {

    jms = new JmsDataAccessModule(new DataAccessModuleListener() {

      @Override
      public void onDerefResponse(DerefResponse response) {
        log.debug("response: {}", response);
        try {
          jms.stop();
        } catch (Exception e) {
          log.error(e);
        }
      }

      @Override
      public void onErrorResponse(ErrorResponse response) {
        log.debug("error: {}", response);
        try {
          jms.stop();
        } catch (Exception e) {
          log.error(e);
        }
      }

    });
    jms.start();

    jms.deref(IRIFactory.iriImplementation().create("http://dbpedia.org/resource/Koblenz"));
  }
}
