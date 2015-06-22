package de.unikoblenz.west.koldfish.dam.crawler.impl;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;

import de.unikoblenz.west.koldfish.dam.Executable;
import de.unikoblenz.west.koldfish.dam.Negotiator;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlConsumer;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlQueue;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlProducer;
import de.unikoblenz.west.koldfish.dam.impl.SimpleController;

/**
 * general class for the crawler.
 * Provides all necessary things: controller for dereferencing IRIs, a queue form which the controller
 * receives IRIs and a parallized receiver that gets ReportMessages and creates crawl messages for the controller. 
 * 
 * @author lkastler@uni-koblenz.de
 */
public class Crawler implements Executable<Exception> {

	private static final Logger log = LoggerFactory.getLogger(Crawler.class);
	
	private final CrawlConsumer control;
	private final CrawlQueue queue;
	private final CrawlProducer receiver;
	
	public Crawler(Negotiator<Model> neg) {
		this.queue = new CrawlQueueImpl();
		control = new CrawlConsumerImpl(new SimpleController(neg), queue);
		receiver = new CrawlProducerImpl(queue);
		
		neg.addReceiver(receiver);
		
		log.debug("created");
	}

	/**
	 * feed specified file to CrawlQueue e.g. to start the crawl.
	 * @param file - line separated IRI file to feed.
	 * @throws IOException thrown if file could not be accessed.
	 */
	public void feed(final File file) throws IOException {
		SeedFeeder feed = new SeedFeeder(file, queue);
		feed.start();
	}
		
	@Override
	public void start() throws Exception {
		receiver.start();
		control.start();
		log.debug("started");
	}

	@Override
	public void terminate() throws Exception {
		control.terminate();
		receiver.terminate();
		log.debug("terminated");
	}

	@Override
	public String toString() {
		return "Crawler [control=" + control + ", queue=" + queue
				+ ", receiver=" + receiver + "]";
	}
}
