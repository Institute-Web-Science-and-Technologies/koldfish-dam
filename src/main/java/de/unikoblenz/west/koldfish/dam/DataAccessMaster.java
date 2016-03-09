package de.unikoblenz.west.koldfish.dam;

/**
 * responsible for JMS connections and controls data access workers.
 * 
 * @author lkastler
 */
public interface DataAccessMaster extends Runnable, AutoCloseable {
}
