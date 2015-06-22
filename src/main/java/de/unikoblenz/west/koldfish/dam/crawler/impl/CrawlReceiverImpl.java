package de.unikoblenz.west.koldfish.dam.crawler.impl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.jena.iri.IRIException;
import org.apache.jena.iri.IRIFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

import de.unikoblenz.west.koldfish.dam.crawler.CrawlQueue;
import de.unikoblenz.west.koldfish.dam.crawler.CrawlReceiver;
import de.unikoblenz.west.koldfish.dam.messages.ReportMessage;
import de.unikoblenz.west.koldfish.dam.simpl.messages.ModelReportMessage;

public class CrawlReceiverImpl implements CrawlReceiver {

	private static final Logger log = LoggerFactory.getLogger(CrawlReceiverImpl.class);
	
	private final Executor exec = Executors.newCachedThreadPool();
	private final IRIFactory fac = IRIFactory.iriImplementation();
	private final CrawlQueue queue;
	
	
	public CrawlReceiverImpl(CrawlQueue queue) {
		this.queue = queue;
	}

	@Override
	public void report(ReportMessage<?> rm) {
		exec.execute(new Runnable() {

			@Override
			public void run() {
				if(rm instanceof ModelReportMessage) {
					ModelReportMessage mrm = (ModelReportMessage)rm;
					log.debug("got message: " + mrm);
					
					ResIterator it = mrm.getPayload().listSubjects();
					
					while(it.hasNext()) {
						Resource r = it.next();
						
						try {
							if(r.isURIResource()) {
								queue.add(new CrawlIri(fac.construct(r.getURI())));
							}
						}catch(IRIException e) {
							log.warn(e.getLocalizedMessage(),e);
						}
					}
					
					it.close();
				}
			}
			
		});
	}

	@Override
	public void start() throws Exception {
		log.debug("start");
	}

	@Override
	public void terminate() throws Exception {
		log.debug("terminate");
	}

	

}
