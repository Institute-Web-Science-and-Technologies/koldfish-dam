package de.unikoblenz.west.koldfish.dam;

import java.util.concurrent.Callable;

/**
 * retrieves data from targeted data source.
 * 
 * @author lkastler
 */
public interface DataAccessWorker<T> extends Callable<T> {

}
