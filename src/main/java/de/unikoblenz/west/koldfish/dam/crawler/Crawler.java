package de.unikoblenz.west.koldfish.dam.crawler;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;

import de.unikoblenz.west.koldfish.dam.Executable;
import de.unikoblenz.west.koldfish.dam.Negotiator;
import de.unikoblenz.west.koldfish.dam.crawler.impl.CrawlControlImpl;
import de.unikoblenz.west.koldfish.dam.crawler.impl.CrawlQueueImpl;
import de.unikoblenz.west.koldfish.dam.crawler.impl.CrawlReceiverImpl;
import de.unikoblenz.west.koldfish.dam.simpl.SimpleController;

public class Crawler implements Executable<Exception> {

	
	private static final Logger log = LoggerFactory.getLogger(Crawler.class);
	
	private final CrawlController control;
	private final CrawlQueue queue;
	private final CrawlReceiver receiver;
	
	public Crawler(Negotiator<Model> neg) {
		this.queue = new CrawlQueueImpl();
		control = new CrawlControlImpl(new SimpleController(neg), queue);
		receiver = new CrawlReceiverImpl(queue);
		
		neg.addReceiver(receiver);
		
		log.debug("created");
	}

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
	
	
}
