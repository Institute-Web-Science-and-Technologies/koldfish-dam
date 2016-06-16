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

  /**
   * converts given IRI to id value, using given dictionary.
   * 
   * @param dict - Dictionary to use.
   * @param iri - IRI to convert
   * @return id value of converted IRI.
   * @throws Exception thrown if IRI could not be converted.
   */
  public static long convertIri(Dictionary dict, String iri) throws Exception {
    return dict.convertIris(Lists.newArrayList(iri)).get(0);
  }

  /**
   * converts given id to an IRI, using given dictionary.
   * 
   * @param dict - Dictionary to use.
   * @param id - IRI to convert
   * @return IRI of converted id.
   * @throws Exception thrown if id could not be converted.
   */
  public static String convertId(Dictionary dict, long id) throws Exception {
    return dict.convertIds(Lists.newArrayList(id)).get(0);
  }
}
