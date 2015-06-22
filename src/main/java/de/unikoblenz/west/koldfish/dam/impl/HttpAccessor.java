package de.unikoblenz.west.koldfish.dam.impl;

import java.net.MalformedURLException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

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

	private static final Logger log = LoggerFactory.getLogger(HttpAccessor.class);
	
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
