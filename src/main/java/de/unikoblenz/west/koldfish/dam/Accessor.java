package de.unikoblenz.west.koldfish.dam;

import java.util.concurrent.Callable;

import de.unikoblenz.west.koldfish.dam.except.AccessorException;
import de.unikoblenz.west.koldfish.dam.messages.ActivationMessage;

/**
 * accesses RDF data.
 * @author lkastler@uni-koblenz.de
 *
 * @param <T> return value of this Accessor
 */
public interface Accessor<T> {
	
	/**
	 * creates a Callable object that accesses the RDF data specified by the given ActivationMessage.
	 * @param am - Information about the to access RDF data.
	 * @return a Callable that handles the access.
	 * @throws AccessorException thrown if Accessor could not handle activation.
	 */
	Callable<T> activate(ActivationMessage am) throws AccessorException;

}
