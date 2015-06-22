package de.unikoblenz.west.koldfish.dam.crawler;

import de.unikoblenz.west.koldfish.dam.Executable;
import de.unikoblenz.west.koldfish.dam.Receiver;

/**
 * Receiver class for the Crawler, receives ReportMessages and adds new CrawlMessages to the CrawlQueue.
 * 
 * @author lkastler@uni-koblenz.de
 */
public interface CrawlProducer extends Receiver, Executable<Exception> {
}
