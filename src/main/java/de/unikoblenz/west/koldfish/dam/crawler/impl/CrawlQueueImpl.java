package de.unikoblenz.west.koldfish.dam.crawler.impl;

import java.util.concurrent.LinkedBlockingQueue;

import de.unikoblenz.west.koldfish.dam.crawler.CrawlMessage;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlQueue;

public class CrawlQueueImpl extends LinkedBlockingQueue<CrawlMessage> implements CrawlQueue {

	private static final long serialVersionUID = 1L;

}
