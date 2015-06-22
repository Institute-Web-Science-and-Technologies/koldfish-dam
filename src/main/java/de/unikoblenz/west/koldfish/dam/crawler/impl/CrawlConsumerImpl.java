package de.unikoblenz.west.koldfish.dam.crawler.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unikoblenz.west.koldfish.dam.Controller;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlConsumer;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlMessage;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlQueue;
import de.unikoblenz.west.koldfish.dam.crawler.impl.messages.CrawlIri;
import de.unikoblenz.west.koldfish.dam.crawler.impl.messages.CrawlPoison;
import de.unikoblenz.west.koldfish.dam.except.ControllerException;

/**
 * simple CrawlConsumer implementation with a nice crawling strategy.
 * @author lkastler@uni-koblenz.de
 *
 */
public class CrawlConsumerImpl implements CrawlConsumer {
	
	private static final Logger log = LoggerFactory.getLogger(CrawlConsumerImpl.class);
	
	private static final int NICE_WAIT = 500;
	
	private final Controller<?> ctrl;
	private final CrawlQueue queue;
	private Thread t;
	private boolean niceCrawl;
	
	/**
	 * initializes this CrawlConsumerImpl to use the given Controller and the given CrawlQueue for processing CrawlMessages, using a nice crawl strategy.
	 * @param ctrl - Controller to process IRI dereferencing.
	 * @param queue - queue to pull CrawlMessages from.
	 */
	public CrawlConsumerImpl(Controller<?> ctrl, CrawlQueue queue) {
		this(ctrl, queue, true);
	}
	
	/**
	 * initializes this CrawlConsumerImpl to use the given Controller and the given CrawlQueue for processing CrawlMessages.
	 * @param ctrl - Controller to process IRI dereferencing.
	 * @param queue - queue to pull CrawlMessages from.
	 * @param niceCrawl - specifies the crawl strategy, either nicely given <code>true</code> or forceful given <code>false</code>.
	 */
	public CrawlConsumerImpl(Controller<?> ctrl, CrawlQueue queue, boolean niceCrawl) {
		this.ctrl = ctrl;
		this.queue = queue;
		this.niceCrawl = niceCrawl;
		
		t = new Thread(create());
	}
	
	@Override
	public void start() throws Exception {
		t.start();
	}

	@Override
	public void terminate() throws Exception {
		queue.add(new CrawlPoison());
	}
	
	/**
	 * creates a new Runnable that consumes CrawlMessages by dereferencing the provided IRI. 
	 * @return a new Runnable that consumes CrawlMessages by dereferencing the provided IRI.
	 */
	private Runnable create() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					while(true) {
						// wait if nice crawl
						if(niceCrawl) {
							synchronized(t) {
								try {
									t.wait(NICE_WAIT);
								} catch (InterruptedException e) {
									log.error(e.toString(),e);
								}
							}
						}
						
						// get message
						CrawlMessage msg = queue.take();
						
						// if poison, finish yourself
						if(msg instanceof CrawlPoison) {
							log.debug("got poisoned, finish thead");
							return;
						}
						
						// else deref IRI.
						else if (msg instanceof CrawlIri) {
							try {
								ctrl.derefAsync(((CrawlIri) msg).getIRI());
							} catch (ControllerException e) {
								log.error(e.toString(),e);
							}
						}
					}	
				} catch (InterruptedException e) {
					log.error(e.toString(),e);
				}
			}
			
		};
	}

	@Override
	public String toString() {
		return "CrawlConsumerImpl [ctrl=" + ctrl + ", queue=" + queue + ", niceCrawl=" + niceCrawl + "]";
	}
}
