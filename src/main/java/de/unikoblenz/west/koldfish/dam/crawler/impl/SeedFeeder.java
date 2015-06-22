package de.unikoblenz.west.koldfish.dam.crawler.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Consumer;

import org.apache.jena.iri.IRIFactory;

import de.unikoblenz.west.koldfish.dam.Executable;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlQueue;
import de.unikoblenz.west.koldfish.dam.crawler.impl.messages.CrawlIri;

/**
 * reads a text file with one IRI per line and adds it to a given CrawlQueue to start a crawl.
 * 
 * @author lkastler@uni-koblenz.de
 *
 */
public class SeedFeeder implements Executable<IOException> {

	private final File seedFile;
	private final CrawlQueueInputProducer consumer;
	private final IRIFactory fac = IRIFactory.iriImplementation();
	
	public SeedFeeder(final File seedFile, CrawlQueue queue) {
		this.seedFile = seedFile;
		this.consumer = new CrawlQueueInputProducer(queue);
	}
	
	@Override
	public void start() throws IOException {
		BufferedReader read = new BufferedReader(new FileReader(seedFile));
		read.lines().forEach(consumer);
		read.close();
	}
	

	@Override
	public void terminate() throws IOException {
		// nothing to do
	}

	/**
	 * fills the queue with IRIs.
	 * 
	 * @author lkastler@uni-koblenz.de
	 *
	 */
	private class CrawlQueueInputProducer implements Consumer<String> {
		private final CrawlQueue queue;
		
		private CrawlQueueInputProducer(CrawlQueue queue) {
			this.queue = queue;
		}
		
		@Override
		public void accept(String t) {
			queue.add(new CrawlIri(fac.construct(t)));
		}
		
	}

	@Override
	public String toString() {
		return "SeedFeeder [seedFile=" + seedFile + ", consumer=" + consumer+ "]";
	}
}
