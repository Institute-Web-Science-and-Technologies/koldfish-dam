package de.unikoblenz.west.koldfish.dam.crawler.impl;

import java.util.concurrent.LinkedBlockingQueue;

import de.unikoblenz.west.koldfish.dam.crawler.CrawlMessage;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlQueue;

/**
 * crawl queue, enables communication between CrawlProducers and CrawlConsumers.
 * 
 * @author lkastler@uni-koblenz.de
 *
 */
public class CrawlQueueImpl extends LinkedBlockingQueue<CrawlMessage> implements CrawlQueue {

	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "CrawlQueueImpl [" + size() + "]";
	}

	
}
