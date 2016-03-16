/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.unikoblenz.west.koldfish.dam.DataAccessModule;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleException;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleListener;
import de.unikoblenz.west.koldfish.dam.DerefResponse;

/**
 * @author lkastler
 *
 */
@RunWith(Parameterized.class)
public class TestDummyDataAccessModule {

	private static final Logger log = LogManager
			.getLogger(TestDummyDataAccessModule.class);

	private final IRI someIRI = IRIFactory.iriImplementation().create("http://dbpedia.org/");

	@Parameters(name="{0}")
	public static List<Object[]> data() {
		return Arrays.asList(
				new Object[] {// random dummy
					new DummyDataAccessModule(new Random(1000)),
					TestDummyDataAccessModule.getRandomDAMListener()
				}, 
				new Object[] {// no op dummy
					new DummyDataAccessModule(),
					TestDummyDataAccessModule.getNoopDAMListener()
				}
			);
	}

	// Listener to test random dummy
	private static DataAccessModuleListener getRandomDAMListener() {
		return new DataAccessModuleListener() {
			@Override
			public void derefResponse(DerefResponse response) {
				log.debug(response);
					for (long[] item : response) {
					assertTrue(item.length == 3 || item.length == 4);
					for (long id : item) {
						assertTrue(id > 0);
					}
				}
			}
		};
	}
	
	// Listener to test no op dummy
	private static DataAccessModuleListener getNoopDAMListener() {
		return new DataAccessModuleListener() {
			@Override
			public void derefResponse(DerefResponse response) {
				log.debug(response + " : how did that happen ???");
				fail();
			}
		};
	}
	
	private DataAccessModule dam;
	private DataAccessModuleListener listener;

	
	public TestDummyDataAccessModule(DataAccessModule dam, DataAccessModuleListener listener) {
		this.dam = dam;
		this.listener = listener;
	}

	@Test
	/**
	 * tests data streaming.
	 * @throws Exception thrown if something unforeseen happens
	 */
	public void testDataStreaming() throws Exception {
		assertFalse(dam.isStarted());
		
		try {
			dam.addListener(listener);

			assertFalse(dam.isStarted());
			
			dam.start();

			assertTrue(dam.isStarted());

			Thread.sleep(10_000);

			assertTrue(dam.isStarted());
		} finally {
			dam.close();
		}

		assertFalse(dam.isStarted());
	}

	@Test
	/**
	 * tests DataAccessModule.deref() with a started DAM.
	 * @throws Exception thrown if something unforeseen happens
	 */
	public void testDeref() throws Exception {
		assertFalse(dam.isStarted());
		
		try {
			dam.start();
			
			assertTrue(dam.isStarted());
			
			dam.deref(someIRI);
		} finally {
			dam.close();
		}
		
		assertFalse(dam.isStarted());
	}
	
	@Test(expected=DataAccessModuleException.class)
	/**
	 * tests DataAccessModule.deref() with a halted DAM, should throw an exception.
	 * @throws Exception should be thrown since DAM has not been started.
	 */
	public void testUnstartedDeref() throws Exception {
		assertFalse(dam.isStarted());
		dam.deref(someIRI);
	}
}
