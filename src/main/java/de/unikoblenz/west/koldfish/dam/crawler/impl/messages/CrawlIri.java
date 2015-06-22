package de.unikoblenz.west.koldfish.dam.crawler.impl.messages;

import org.apache.jena.iri.IRI;

import de.unikoblenz.west.koldfish.dam.crawler.CrawlMessage;

/**
 * identifies a CrawlMessage where an IRI has to be dereferenced.
 * @author lkastler@uni-koblenz.de
 *
 */
public class CrawlIri implements CrawlMessage {

	private final IRI iri;

	public CrawlIri(IRI iri) {
		this.iri = iri;
	}
	
	public IRI getIRI() {
		return iri;
	}
}
