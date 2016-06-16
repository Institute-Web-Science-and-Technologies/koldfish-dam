/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import org.apache.jena.rdf.model.RDFNode;

/**
 * @author lkastler
 *
 */
public interface RdfNodeHandler {

  public String handle(String iri, RDFNode toVisit);
}
