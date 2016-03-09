/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.dam.impl.DataAccessMasterImpl;

/**
 * Teh Main.
 * @author lkastler
 */
public class Main {

	private static final Logger log = LogManager.getLogger(Main.class);

	/**
	 * starts the DataAccessManager
	 * @param args - no arguments.
	 */
	public static void main(String[] args) {
		log.info("starting");
		log.info("accessing {}", ActiveMQConnectionFactory.DEFAULT_BROKER_BIND_URL);
		
		ActiveMQConnectionFactory fac = new ActiveMQConnectionFactory(
				ActiveMQConnectionFactory.DEFAULT_USER, 
				ActiveMQConnectionFactory.DEFAULT_PASSWORD, 
				ActiveMQConnectionFactory.DEFAULT_BROKER_BIND_URL
			);
		
		// FIXME hardcore hack: http://activemq.apache.org/objectmessage.html
		fac.setTrustAllPackages(true);
				
		try (DataAccessMaster master = new DataAccessMasterImpl(fac)) {
			Thread t = new Thread(master);
			t.start();
			t.join();
		} catch (Exception e) {
			log.error(e.toString(),e);
		}
		log.info("done");
	}
}
