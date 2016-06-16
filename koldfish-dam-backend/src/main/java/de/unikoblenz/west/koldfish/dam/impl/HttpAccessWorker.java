package de.unikoblenz.west.koldfish.dam.impl;

import java.io.InputStream;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import de.unikoblenz.west.koldfish.dam.DataAccessWorker;
import de.unikoblenz.west.koldfish.dam.EncodingParser;
import de.unikoblenz.west.koldfish.dam.ParserException;
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

  private final String iri;
  private final Dictionary dict;
  private final EncodingParser parser;

  public HttpAccessWorker(Dictionary dict, EncodingParser parser, String iri) {
    this.dict = dict;
    this.iri = iri;
    this.parser = parser;

    log.debug("accessing: {}", iri);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Callable#call()
   */
  @Override
  public DerefResponse call() throws Exception {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet httpget = new HttpGet(iri);
      httpget.setHeaders(headers);

      try (CloseableHttpResponse response = httpclient.execute(httpget);) {

        log.debug("status line: {}", response.getStatusLine());

        HttpEntity entity = response.getEntity();
        if (entity == null) {
          throw new Exception("received nothing from: " + iri);
        }
        try {
          InputStream rdf = entity.getContent();

          log.debug("data retrieved");

          try {
            List<long[]> result = parser.parse(iri, rdf);
            log.debug("data parsed");

            return new DerefResponse(dict.convertIris(Lists.newArrayList(iri)).get(0), result);
          } catch (ParserException e) {
            throw e;
          }
        } catch (Exception e) {
          throw e;
        } finally {
          EntityUtils.consumeQuietly(entity);
        }
      } catch (Exception e) {
        throw e;
      }
    } catch (Exception e) {
      throw e;
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
      return null;
    }
  }
}
