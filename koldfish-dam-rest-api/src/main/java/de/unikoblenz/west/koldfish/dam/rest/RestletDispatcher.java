/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.rest;


import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * RESTful service for the Koldfish Data Access Module.
 * 
 * @author lkastler
 */
public class RestletDispatcher {

  /**
   * starts the service.
   * 
   * @param args - no arguments parsed
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    Component component = new Component();

    component.getServers().add(Protocol.HTTP, 8282);

    component.getDefaultHost().attach("/deref/{iri}", DerefService.class);

    component.start();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          component.stop();
        } catch (Exception e) {
          // don't care about
        }
      }
    });
  }
}
