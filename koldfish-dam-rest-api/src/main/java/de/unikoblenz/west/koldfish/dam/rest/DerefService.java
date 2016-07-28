/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIException;
import org.apache.jena.iri.IRIFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import de.unikoblenz.west.koldfish.dam.DataAccessModule;
import de.unikoblenz.west.koldfish.dam.impl.JmsDataAccessModule;

/**
 * handles deref tasks.
 * 
 * @author lkastler
 *
 */
public class DerefService extends ServerResource {

  private static final Logger log = LogManager.getLogger(DerefService.class);

  private String encodedIri;

  @Override
  protected void doInit() throws ResourceException {
    encodedIri = getAttribute("iri");

  }

  @Override
  @Get("application/ld+json")
  public String toString() {
    try {
      String doubleDecodedIri = URLDecoder.decode(URLDecoder.decode(encodedIri, "UTF-8"), "UTF-8");
      IRI iri = IRIFactory.iriImplementation().construct(doubleDecodedIri);
      if (iri.violations(true).hasNext()) {
        throw new ResourceException(400,
            String.format("violations during IRI parsing for: %s", iri));
      }
      try {
        return deref(iri);
      } catch (Exception e) {
        throw new ResourceException(400, "malformed IRI");
      }
    } catch (UnsupportedEncodingException e) {
      throw new ResourceException(500, "encoding error");
    } catch (IRIException e) {
      throw new ResourceException(400, "IRI not decodable");
    }
  }

  private String deref(IRI iri) throws Exception {
    DataAccessModule dam = new JmsDataAccessModule();
    try {
      dam.start();
      dam.deref(iri);
      return "Result";
    } catch (Exception e) {
      log.error(e);
    } finally {
      dam.stop();
    }
    throw new IllegalStateException();
  }
}
