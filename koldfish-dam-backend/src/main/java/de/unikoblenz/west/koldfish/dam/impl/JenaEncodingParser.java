/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.jms.JMSException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.primitives.Longs;

import de.unikoblenz.west.koldfish.dam.EncodingParser;
import de.unikoblenz.west.koldfish.dam.ParserException;
import de.unikoblenz.west.koldfish.dictionary.Dictionary;

/**
 * @author lkastler
 *
 */
public class JenaEncodingParser implements EncodingParser {

  private static final Logger log = LogManager.getLogger(JenaEncodingParser.class);

  private final Dictionary dict;
  private final long encodedDefaultNamespace;

  /**
   * creates a new NxEncodingParser object with given Dictionary.
   * 
   * @param dict - dictionary to encode IRIs and labels.
   */
  public JenaEncodingParser(Dictionary dict, long encodedDefaultNamespace) {
    this.dict = dict;
    this.encodedDefaultNamespace = encodedDefaultNamespace;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.unikoblenz.west.koldfish.dam.EncodingParser#parse(java.io.InputStream)
   */
  @Override
  public List<long[]> parse(InputStream input) throws ParserException {
    try {
      List<long[]> result = new LinkedList<long[]>();

      Model m = ModelFactory.createDefaultModel();

      m.read(input, null);

      StmtIterator it = m.listStatements();

      Statement s;
      while (it.hasNext()) {
        s = it.next();

        List<String> nodeValues = new LinkedList<String>();
        nodeValues.add(s.getSubject().toString());
        nodeValues.add(s.getPredicate().toString());
        nodeValues.add(s.getObject().toString());

        try {
          List<Long> convertedIris =
              dict.convertIris(nodeValues).stream().collect(Collectors.toList());

          if (convertedIris.size() == 3) {
            convertedIris.add(encodedDefaultNamespace);
          }

          result.add(Longs.toArray(convertedIris));
        } catch (JMSException e) {
          log.error(e);
        }
      }
      return result;
    } catch (Exception e) {
      log.error(e);
      throw new ParserException(e);
    }
  }
}
