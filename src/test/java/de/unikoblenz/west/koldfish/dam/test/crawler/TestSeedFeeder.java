package de.unikoblenz.west.koldfish.dam.test.crawler;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unikoblenz.west.koldfish.dam.crawler.CrawlQueue;
import de.unikoblenz.west.koldfish.dam.crawler.SeedFeeder;
import de.unikoblenz.west.koldfish.dam.crawler.impl.CrawlQueueImpl;

public class TestSeedFeeder {

	private static final Logger log = LoggerFactory.getLogger(TestSeedFeeder.class);
	
	@Test
	public void testSeedFeeder() throws Exception {
		log.debug("start test");
		
		File seeds = new File(this.getClass().getResource("/seeds.txt").getFile());
		
		CrawlQueue queue = new CrawlQueueImpl();
		
		SeedFeeder feeder = new SeedFeeder(seeds, queue);
		
		feeder.start();
		
		log.debug("test end");
	}
}
