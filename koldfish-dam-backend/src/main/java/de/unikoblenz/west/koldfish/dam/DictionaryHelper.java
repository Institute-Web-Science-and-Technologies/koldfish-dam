/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

import com.google.common.collect.Lists;

import de.unikoblenz.west.koldfish.dictionary.Dictionary;

/**
 * @author lkastler
 *
 */
abstract public class DictionaryHelper {

  public static long convertIri(Dictionary dict, String iri) throws Exception {
    return dict.convertIris(Lists.newArrayList(iri)).get(0);
  }

  public static String convertId(Dictionary dict, long id) throws Exception {
    return dict.convertIds(Lists.newArrayList(id)).get(0);
  }
}
