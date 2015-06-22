package de.unikoblenz.west.koldfish.dam.crawler;

import de.unikoblenz.west.koldfish.dam.Executable;

/**
 * Consumes a CrawlMessage from a CrawlQueue, by executing the contained information.
 * 
 * @author lkastler@uni-koblenz.de
 */
public interface CrawlConsumer extends Executable<Exception> {
	
}
