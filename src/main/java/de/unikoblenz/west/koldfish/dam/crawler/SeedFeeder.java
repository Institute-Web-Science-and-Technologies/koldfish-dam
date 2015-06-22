package de.unikoblenz.west.koldfish.dam.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

import org.apache.jena.iri.IRIFactory;

import de.unikoblenz.west.koldfish.dam.crawler.impl.CrawlIri;

public class SeedFeeder {

	private final File seedFile;
	private final ControllerConsumer consumer;
	private final IRIFactory fac = IRIFactory.iriImplementation();
	
	public SeedFeeder(final File seedFile, CrawlQueue queue) {
		this.seedFile = seedFile;
		this.consumer = new ControllerConsumer(queue);
	}
	
	public void start() throws IOException {
		BufferedReader read = new BufferedReader(new FileReader(seedFile));
		read.lines().forEach(consumer);
		read.close();
	}
	
	
	private class ControllerConsumer implements Consumer<String> {
		private final CrawlQueue queue;
		
		private ControllerConsumer(CrawlQueue queue) {
			this.queue = queue;
		}
		
		@Override
		public void accept(String t) {
			queue.add(new CrawlIri(fac.construct(t)));
		}
		
	}
}
