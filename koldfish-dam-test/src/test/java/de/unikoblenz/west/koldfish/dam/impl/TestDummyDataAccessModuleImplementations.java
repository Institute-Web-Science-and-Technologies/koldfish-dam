/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.unikoblenz.west.koldfish.dam.DataAccessModule;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleException;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleListener;
import de.unikoblenz.west.koldfish.dam.ErrorResponse;
import de.unikoblenz.west.koldfish.messages.DerefResponse;

/**
 * testing the DummyDataAccessModuleListener, with random answers and without
 * 
 * @author lkastler
 *
 */
@RunWith(Parameterized.class)
public class TestDummyDataAccessModuleImplementations {

  private static final Logger log =
      LogManager.getLogger(TestDummyDataAccessModuleImplementations.class);

  private final IRI someIRI = IRIFactory.iriImplementation().create("http://dbpedia.org/");

  @Parameters
  public static Collection<Object> data() {
    return Arrays.asList(new Random(1000), // with randomizer
        new Object() // without randomizer, just no op
    );
  }

  @Parameter
  public Object rand;

  private DataAccessModule dam;

  @Before
  public void setUp() {
    // initializes mock dictionary


    // sets up the DAMListener
    dam = rand instanceof Random ? new DummyDataAccessModule((Random) rand)
        : new DummyDataAccessModule();

    // sets up listener
    dam.addListener(rand instanceof Random ? getRandomDAMListener() : getNoopDAMListener());
  }

  // Listener to test random dummy
  private DataAccessModuleListener getRandomDAMListener() {
    return new DataAccessModuleListener() {
      @Override
      public void onDerefResponse(DerefResponse response) {
        log.debug(response);
        for (long[] item : response) {
          assertEquals(4, item.length);// check for quadtruple
          assertEquals(Long.MAX_VALUE, item[3]);// check graph name
          for (long id : item) {
            assertTrue(id > 0);
          }
        }
      }

      @Override
      public void onErrorResponse(ErrorResponse response) {
        fail("unexpected");
      }
    };
  }

  // Listener to test no operation dummy
  private DataAccessModuleListener getNoopDAMListener() {
    return new DataAccessModuleListener() {
      @Override
      public void onDerefResponse(DerefResponse response) {
        log.debug("{} : how did that happen ???", response);
        fail();
      }

      @Override
      public void onErrorResponse(ErrorResponse response) {
        fail("unexpected");
      }
    };
  }



  /**
   * tests data streaming.
   * 
   * @throws Exception thrown if something unforeseen happens
   */
  @Test
  public void testDataStreaming() throws Exception {
    assertFalse(dam.isStarted());

    try {
      assertFalse(dam.isStarted());

      dam.start();

      assertTrue(dam.isStarted());

      Thread.sleep(10_000);

      assertTrue(dam.isStarted());
    } finally {
      dam.stop();
    }

    assertFalse(dam.isStarted());
  }

  /**
   * tests DataAccessModule.deref() with a started DAM.
   * 
   * @throws Exception thrown if something unforeseen happens
   */
  @Test
  public void testDerefIri() throws Exception {
    assertFalse(dam.isStarted());

    try {
      dam.start();

      assertTrue(dam.isStarted());

      dam.deref(someIRI);
    } finally {
      dam.stop();
    }

    assertFalse(dam.isStarted());
  }


  /**
   * tests DataAccessModule.deref() with a started DAM.
   * 
   * @throws Exception thrown if something unforeseen happens
   */
  @Test
  public void testDerefId() throws Exception {
    assertFalse(dam.isStarted());

    try {
      dam.start();

      assertTrue(dam.isStarted());

      dam.deref(100);
    } finally {
      dam.stop();
    }

    assertFalse(dam.isStarted());
  }


  /**
   * tests dereferencing of 0, leafs to InvalidParameterException
   * 
   * @throws Exception
   */
  @Test(expected = InvalidParameterException.class)
  public void testZero() throws Exception {
    try {
      dam.start();

      dam.deref(0);
    } finally {
      dam.stop();
    }
  }


  /**
   * tests DataAccessModule.deref() with a halted DAM, should throw an exception.
   * 
   * @throws Exception should be thrown since DAM has not been started.
   */
  @Test(expected = DataAccessModuleException.class)
  public void testUnstartedDerefIri() throws Exception {
    assertFalse(dam.isStarted());
    dam.deref(someIRI);
  }


  /**
   * tests DataAccessModule.deref() with a halted DAM, should throw an exception.
   * 
   * @throws Exception should be thrown since DAM has not been started.
   */
  @Test(expected = DataAccessModuleException.class)
  public void testUnstartedDerefId() throws Exception {
    assertFalse(dam.isStarted());
    dam.deref(100);
  }
}
