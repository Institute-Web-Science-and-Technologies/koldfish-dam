/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jms.JMSException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.unikoblenz.west.koldfish.dam.EncodingParser;
import de.unikoblenz.west.koldfish.dam.ParserException;
import de.unikoblenz.west.koldfish.dictionary.Dictionary;

/**
 * EncodingParser implementation using Apache Jena.
 * 
 * @author lkastler
 */
public class JenaEncodingParser implements EncodingParser {

  private static final Logger log = LogManager.getLogger(JenaEncodingParser.class);

  private final Dictionary dict;
  private final long encodedDefaultNamespace;
  private final RdfNodeHandler handler = new SimpleRdfNodeHandler();

  /**
   * creates a new NxEncodingParser object with given Dictionary.
   * 
   * @param dict - dictionary to encode IRIs and labels.
   * @param encodedDefaultNamespace - id of default namespace.
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
  public List<long[]> parse(String iri, InputStream input) throws ParserException {
    try {
      log.debug("loading model");

      Model m = ModelFactory.createDefaultModel();
      try {
        m.read(input, null);
      } catch (Throwable t) {
        log.error("could not load data", t);
        throw new ParserException(t);
      }



      log.debug("extracting iris");

      StmtIterator it = m.listStatements();
      Set<String> nodeValuesSet = new HashSet<String>();

      Statement s;
      while (it.hasNext()) {
        s = it.next();

        log.debug("{} {} {}.", handler.handle(iri, s.getSubject()),
            handler.handle(iri, s.getPredicate()), handler.handle(iri, s.getObject()));

        nodeValuesSet.add(handler.handle(iri, s.getSubject()));
        nodeValuesSet.add(handler.handle(iri, s.getPredicate()));
        nodeValuesSet.add(handler.handle(iri, s.getObject()));
      }
      it.close();

      log.debug("converting {} iris", nodeValuesSet.size());

      List<String> nodeValues = new LinkedList<String>(nodeValuesSet);
      List<Long> convertedIris;
      try {
        convertedIris = dict.convertIris(nodeValues).stream().collect(Collectors.toList());
      } catch (JMSException e) {
        log.error(e);
        throw new ParserException(e);
      }

      log.debug("check converting");

      if (convertedIris.size() != nodeValues.size()) {
        throw new ParserException("number of retrieved IDs not equal to requested number");
      }

      log.debug("generate results");
      Map<String, Long> nodes = new HashMap<String, Long>();
      for (int i = 0; i < nodeValues.size(); ++i) {
        nodes.put(nodeValues.get(i), convertedIris.get(i));
      }

      List<long[]> result = new LinkedList<long[]>();

      it = m.listStatements();
      while (it.hasNext()) {
        s = it.next();
        Long sub = nodes.get(handler.handle(iri, s.getSubject()));
        Long pred = nodes.get(handler.handle(iri, s.getPredicate()));
        Long obj = nodes.get(handler.handle(iri, s.getObject()));
        log.debug("{} {} {} = {} {} {}", handler.handle(iri, s.getSubject()),
            handler.handle(iri, s.getPredicate()), handler.handle(iri, s.getObject()), sub, pred,
            obj);
        result.add(new long[] {sub, pred, obj, encodedDefaultNamespace});
      }

      log.debug("parsing done");
      return result;
    } catch (Exception e) {
      log.error("error occurred during parsing!", e);
      throw new ParserException(e);
    }
  }
}
