/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.nio.file.Paths;

import org.apache.jena.rdf.model.AnonId;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RDFVisitor;
import org.apache.jena.rdf.model.Resource;

/**
 * @author lkastler
 *
 */
public class SimpleRdfNodeHandler implements RdfNodeHandler {
  private final Object here = new Object();

  private class RdfVisitor implements RDFVisitor {


    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jena.rdf.model.RDFVisitor#visitBlank(org.apache.jena.rdf.model.Resource,
     * org.apache.jena.rdf.model.AnonId)
     */
    @Override
    public Object visitBlank(Resource r, AnonId id) {
      return r.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jena.rdf.model.RDFVisitor#visitURI(org.apache.jena.rdf.model.Resource,
     * java.lang.String)
     */
    @Override
    public Object visitURI(Resource r, String uri) {
      if (projectFolder.equalsIgnoreCase(uri)) {
        return here;
      }
      return uri;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jena.rdf.model.RDFVisitor#visitLiteral(org.apache.jena.rdf.model.Literal)
     */
    @Override
    public Object visitLiteral(Literal l) {
      return "\"" + l.getValue() + "\"";
    }
  }

  private final RdfVisitor visitor = new RdfVisitor();
  private final String projectFolder;

  SimpleRdfNodeHandler() {
    projectFolder = Paths.get(System.getProperty("user.dir")).toUri().toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikoblenz.west.koldfish.dam.impl.RdfNodeHandler#handle(org.apache.jena.rdf.model.RDFNode)
   */
  @Override
  public String handle(String iri, RDFNode toVisit) {
    Object o = toVisit.visitWith(visitor);
    if (o == here) {
      return iri;
    }
    return (String) o;
  }
}
