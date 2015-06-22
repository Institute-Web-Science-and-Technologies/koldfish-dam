package de.unikoblenz.west.koldfish.dam.crawler;

import java.util.concurrent.BlockingQueue;

/**
 * identifies a BlockingQueue for CrawlMessage objects.
 * Used for communication between CrawlConsumer and CrawlProducer.
 * 
 * @author lkastler@uni-koblenz.de
 *
 */
public interface CrawlQueue extends BlockingQueue<CrawlMessage> {

}
