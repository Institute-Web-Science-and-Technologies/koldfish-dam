/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.cli;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIException;
import org.apache.jena.iri.IRIFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.dam.DataAccessModule;
import de.unikoblenz.west.koldfish.dam.DataAccessModuleException;
import de.unikoblenz.west.koldfish.dam.impl.JmsDataAccessModule;

/**
 * @author lkastler
 *
 */
public class DataAccessModuleMain {

  private static final Logger log = LogManager.getLogger(DataAccessModuleMain.class);
  
  public static void main(String[] args) throws Exception {
    if(args.length < 1) {
      log.error("need URIs to deref");
      return;
    }    
    
    List<IRI> iris = new LinkedList<IRI>();
    
    for(String iri : args) {
      try {
        iris.add(IRIFactory.iriImplementation().construct(iri));
      } catch(IRIException e) {
        log.warn("{} is not a proper IRI", iri);
      }
    }
    
    DataAccessModuleMain main = new DataAccessModuleMain();
    
    main.handle(iris);
    
  }
  
  private volatile AtomicInteger uris;
    
  private DataAccessModule dam;
  
  private DataAccessModuleMain() throws Exception {
    DataAccessModule dam = new JmsDataAccessModule();
    
    dam.start();
  }
  
  private void handle(List<IRI> iris) {
    uris.addAndGet(iris.size());
    
    for(IRI iri : iris) {
      log.info("trying to deref IRI {}", iri);
      try {
        dam.deref(iri);
      } catch (DataAccessModuleException e) {
        log.error(e);
        uris.decrementAndGet();
      }
    }
  }
  
}
