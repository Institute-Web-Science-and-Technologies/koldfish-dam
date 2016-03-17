/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

import de.uni_koblenz.west.koldfish.messaging.KoldfishMessage;

/**
 * response from the DAM backend when successfully dereferenced a given IRI.
 * 
 * @author lkastler
 *
 */
public interface DerefResponse extends KoldfishMessage, Iterable<long[]> {

}
