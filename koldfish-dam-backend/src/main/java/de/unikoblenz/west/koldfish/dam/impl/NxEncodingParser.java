/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

import de.unikoblenz.west.koldfish.dam.EncodingParser;
import de.unikoblenz.west.koldfish.dictionary.Dictionary;

/**
 * @author lkastler
 *
 */
public class NxEncodingParser implements EncodingParser {

  private static final Logger log = LogManager.getLogger(NxEncodingParser.class);

  private final NxParser parser = new NxParser();
  private final Dictionary dict;

  /**
   * creates a new NxEncodingParser object with given Dictionary.
   * 
   * @param dict - dictionary to encode IRIs and labels.
   */
  public NxEncodingParser(Dictionary dict) {
    this.dict = dict;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.EncodingParser#parse(java.io.InputStream)
   */
  @Override
  public List<long[]> parse(InputStream input) {
    List<long[]> result = new LinkedList<long[]>();

    Iterator<Node[]> it = parser.parse(input);
    Node[] nodes;

    while (it.hasNext()) {
      nodes = it.next();

      List<String> nodeValues =
          Arrays.asList(nodes).stream().map(n -> n.getLabel()).collect(Collectors.toList());

      try {
        result.add(dict.convertIris(nodeValues).stream().mapToLong(l -> l).toArray());
      } catch (JMSException e) {
        log.error(e);
      }
    }
    return result;
  }
}
