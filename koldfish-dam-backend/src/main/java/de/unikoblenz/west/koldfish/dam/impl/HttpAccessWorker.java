package de.unikoblenz.west.koldfish.dam.impl;

import java.io.InputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import de.unikoblenz.west.koldfish.dam.DataAccessWorker;
import de.unikoblenz.west.koldfish.dam.EncodingParser;
import de.unikoblenz.west.koldfish.dictionary.Dictionary;
import de.unikoblenz.west.koldfish.messages.DerefResponse;

/**
 * DataAccessWorker for HTTP calls with RDF encoding.
 * 
 * @author lkastler
 */
public class HttpAccessWorker implements DataAccessWorker<DerefResponse> {
  private static final Logger log = LogManager.getLogger(HttpAccessWorker.class);

  private static final Header[] headers =
      new Header[] {new BasicHeader("Accept", "text/turtle, application/rdf+xml, application/xml"),
          new BasicHeader("User-Agent", "koldfish"), // agent name
          new BasicHeader("Accept-Encoding", "gzip")// gzip is allowed
      };

  private final HttpClientBuilder builder;

  private final String iri;
  private final Dictionary dict;
  private final EncodingParser parser;

  public HttpAccessWorker(Dictionary dict, EncodingParser parser, String iri,
      HttpClientBuilder builder) {
    this.dict = dict;
    this.iri = iri;
    this.parser = parser;
    this.builder = builder;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Callable#call()
   */
  @Override
  public DerefResponse call() throws Exception {
    log.debug("accessing: {}", iri);

    HttpGet httpget = new HttpGet(iri);
    httpget.setHeaders(headers);
    httpget.getConfig();

    try (CloseableHttpClient client = builder.build();
        CloseableHttpResponse response = client.execute(httpget);) {

      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        throw new Exception(String.format("could not acces %s: %s", iri, response.getStatusLine()));
      }

      HttpEntity entity = response.getEntity();

      if (entity == null) {
        throw new Exception(String.format("received nothing from: %s", iri));
      }
      try {
        InputStream rdf = entity.getContent();

        log.debug("data retrieved");

        List<long[]> result = parser.parse(iri, rdf);
        log.debug("data parsed");

        return new DerefResponse(dict.convertIris(Lists.newArrayList(iri)).get(0), result);
      } finally {
        EntityUtils.consumeQuietly(entity);
      }
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.function.Supplier#get()
   */
  @Override
  public DerefResponse get() {
    try {
      return call();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
