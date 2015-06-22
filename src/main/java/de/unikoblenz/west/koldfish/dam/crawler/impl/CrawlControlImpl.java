package de.unikoblenz.west.koldfish.dam.crawler.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unikoblenz.west.koldfish.dam.Controller;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlController;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlMessage;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlQueue;
import de.unikoblenz.west.koldfish.dam.except.ControllerException;

public class CrawlControlImpl implements CrawlController {
	
	private static final Logger log = LoggerFactory.getLogger(CrawlControlImpl.class);
	
	private static final int NICE_WAIT = 500;
	
	private final Controller<?> ctrl;
	private final CrawlQueue queue;
	private Thread t;
	private boolean niceCrawl;
	
	public CrawlControlImpl(Controller<?> ctrl, CrawlQueue queue) {
		this(ctrl, queue, true);
	}
	
	public CrawlControlImpl(Controller<?> ctrl, CrawlQueue queue, boolean niceCrawl) {
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
	
	private Runnable create() {
		return new Runnable() {

			@Override
			public void run() {
				try {
					while(true) {

						if(niceCrawl) {
							synchronized(t) {
								try {
									t.wait(NICE_WAIT);
								} catch (InterruptedException e) {
									log.error(e.toString(),e);
								}
							}
						}
						
						CrawlMessage msg = queue.take();
						
						if(msg instanceof CrawlPoison) {
							log.debug("got poisoned, finish thead");
							return;
						}
						
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

}
