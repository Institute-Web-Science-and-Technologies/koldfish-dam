package de.unikoblenz.west.koldfish.dam.impl;

import java.net.MalformedURLException;
import java.util.concurrent.Callable;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.dam.Accessor;
import de.unikoblenz.west.koldfish.dam.except.AccessorException;
import de.unikoblenz.west.koldfish.dam.messages.ActivationMessage;

/**
 * simple Accessor with HTTP access.
 * 
 * @author lkastler@uni-koblenz.de
 *
 */
public class HttpAccessor implements Accessor<Model> {

	private static final Logger log = LogManager.getLogger(HttpAccessor.class);
	
	@Override
	public Callable<Model> activate(ActivationMessage am) throws AccessorException {
		if(am == null) {
			throw new AccessorException("ActivationMessage is null", new NullPointerException());
		}
		
		log.debug("activate: " + am);
		
		return new Callable<Model>() {
			
			@Override
			public Model call() throws Exception {
				log.debug("access: " + am.getResourceIRI());
				
				Model m = ModelFactory.createDefaultModel();
				
				try {
					m.read(am.getResourceIRI().toURL().toString());
				} catch (MalformedURLException e) {
					throw new AccessorException(e.toString(), e);
				}
				return m;
			}		
		};
	}

	@Override
	public String toString() {
		return "HttpAccessor []";
	}
}
