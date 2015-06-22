package de.unikoblenz.west.koldfish.dam.test.crawler;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;

import de.unikoblenz.west.koldfish.dam.Negotiator;
import de.unikoblenz.west.koldfish.dam.Receiver;
import de.unikoblenz.west.koldfish.dam.crawler.Crawler;
import de.unikoblenz.west.koldfish.dam.messages.ReportMessage;
import de.unikoblenz.west.koldfish.dam.simpl.SimpleNegotiator;
import de.unikoblenz.west.koldfish.dam.simpl.messages.ModelReportMessage;

public class TestCrawler implements Receiver {

	
	private static final Logger log = LoggerFactory.getLogger(TestCrawler.class);
	
	public static void main(String[] args) throws Exception {
		TestCrawler obj = new TestCrawler();
		
		log.debug("start");
		
		Negotiator<Model> neg = new SimpleNegotiator();
		neg.addReceiver(obj);
		
		neg.start();
		
		Crawler crawl = new Crawler(neg);
		
		crawl.start();
		
		crawl.feed(new File(obj.getClass().getResource("/seeds.txt").getFile()));
		
		log.debug("end");
	}

	@Override
	public void report(ReportMessage<?> rm) {
		if(rm instanceof ModelReportMessage) {
			log.info(((ModelReportMessage)rm).getResourceIRI().toString());
		}
	}
	
}
