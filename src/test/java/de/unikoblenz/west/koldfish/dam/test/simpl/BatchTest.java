package de.unikoblenz.west.koldfish.dam.test.simpl;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.rdf.model.Model;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unikoblenz.west.koldfish.dam.Controller;
import de.unikoblenz.west.koldfish.dam.Negotiator;
import de.unikoblenz.west.koldfish.dam.Receiver;
import de.unikoblenz.west.koldfish.dam.except.ControllerException;
import de.unikoblenz.west.koldfish.dam.except.NegotiatorException;
import de.unikoblenz.west.koldfish.dam.impl.SimpleController;
import de.unikoblenz.west.koldfish.dam.impl.SimpleNegotiator;
import de.unikoblenz.west.koldfish.dam.messages.ReportMessage;

public class BatchTest {

	private static final Logger log = LoggerFactory.getLogger(BatchTest.class);

	private final List<IRI> data = Arrays.asList(
		new IRI[] {
				IRIFactory.iriImplementation().construct("http://dbpedia.org/resource/Koblenz"),
				IRIFactory.iriImplementation().construct("http://dbpedia.org/resource/Red"),
				// not a correct endpoint.
				IRIFactory.iriImplementation().construct("http://foo"),
				// not a correct URI.
				IRIFactory.iriImplementation().construct("http://füü"),
				IRIFactory.iriImplementation().construct("http://dbpedia.org/resource/Mainz")
		}
	);
			
	@Test
	public void testBatch() {
		log.debug("start");

		// initialize negotiator
		Negotiator<Model> neg = new SimpleNegotiator();

		// add simple Receiver
		neg.addReceiver(new Receiver() {
			@Override
			public void report(ReportMessage<?> rm) {
				if (rm.getPayload() instanceof Exception) {
					log.error("REPORT 1: " + rm.getPayload());
				} 
				else if(rm.getPayload() instanceof Model) {
					log.debug("REPORT 1: " + ((Model)rm.getPayload()).size());
				}
				else {
					log.error("unknown type of payload: " + rm.getPayload());
				}

			}
		});

		// add another simple Receiver
		neg.addReceiver(new Receiver() {
			@Override
			public void report(ReportMessage<?> rm) {
				if (rm.getPayload() instanceof Exception) {
					log.error("REPORT 2: " + rm.toString());
				} else {
					log.debug("REPORT 2: got something");
				}

			}
		});
		
		// new Controller
		Controller<Model> c = new SimpleController(neg);
		
		//
		for(IRI iri : data) {
			try {
				log.debug("iri: " + iri);
				c.derefAsync(iri);
			} catch (ControllerException e) {
				log.error(e.toString(),e);
			}
		}

		// start Negotiator
		try {
			neg.start();
		} catch (NegotiatorException e) {
			log.error(e.toString(),e);
			
			try {
				neg.terminate();
			} catch (NegotiatorException e1) {
				log.error(e1.toString(),e1);
			}
			
			fail();
		}
		

		
		
		// shut down Negotiator
		try {
			neg.terminate();
		} catch (NegotiatorException e) {
			log.error(e.toString(),e);
			fail();
		}
		log.debug("end");
	}
}
