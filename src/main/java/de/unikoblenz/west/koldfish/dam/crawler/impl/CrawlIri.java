package de.unikoblenz.west.koldfish.dam.crawler.impl;

import org.apache.jena.iri.IRI;

import de.unikoblenz.west.koldfish.dam.crawler.CrawlMessage;

public class CrawlIri implements CrawlMessage {

	private final IRI iri;

	public CrawlIri(IRI iri) {
		this.iri = iri;
	}
	
	public IRI getIRI() {
		return iri;
	}
}
