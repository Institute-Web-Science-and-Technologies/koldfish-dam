/**
 * 
 */
package de.unikoblenz.west.koldfish.dam;

import java.io.InputStream;
import java.util.List;

/**
 * interface to parse RDF encodings.
 * 
 * @author lkastler
 */
public interface EncodingParser {

  /**
   * parses a given input stream with RDF data and returns the data as list of long arrays.
   * 
   * @param input - input stream with RDF data.
   * @return the given RDF data as list of long arrays, where each long is the compressed
   *         information.
   * @throws Exception throws Exception if parsing not possible
   */
  public List<long[]> parse(InputStream input) throws Exception;
}
