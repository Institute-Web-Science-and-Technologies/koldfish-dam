/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

import de.uni_koblenz.west.koldfish.messaging.KoldfishMessage;

/**
 * @author lkastler
 *
 */
public interface DerefResponse extends KoldfishMessage, Iterable<long[]> {

}
