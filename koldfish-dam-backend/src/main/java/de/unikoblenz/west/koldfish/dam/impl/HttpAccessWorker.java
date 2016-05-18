package de.unikoblenz.west.koldfish.dam.impl;

import javax.jms.JMSException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Lists;

import de.unikoblenz.west.koldfish.dam.DataAccessWorker;
import de.unikoblenz.west.koldfish.dam.DerefResponse;
import de.unikoblenz.west.koldfish.dam.EncodingParser;
import de.unikoblenz.west.koldfish.dictionary.Dictionary;

/**
 * DataAccessWorker for HTTP calls with RDF encoding.
 * 
 * @author lkastler
 */
public class HttpAccessWorker implements DataAccessWorker<DerefResponse> {

  private final String iri;
  private final Dictionary dict;
  private final EncodingParser parser;

  private static final Header[] headers = new Header[] {
      new BasicHeader("Accept", "text/turtle, application/rdf+xml, application/xml"),
      new BasicHeader("User-Agent", "koldfish"), new BasicHeader("Accept-Encoding", "gzip")};

  public HttpAccessWorker(Dictionary dict, EncodingParser parser, String iri) {
    this.dict = dict;
    this.iri = iri;
    this.parser = parser;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.util.concurrent.Callable#call()
   */
  @Override
  public DerefResponse call() throws ErrorResponseImpl {
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpGet httpget = new HttpGet(iri);
      httpget.setHeaders(headers);

      try (CloseableHttpResponse response = httpclient.execute(httpget)) {

        HttpEntity entity = response.getEntity();

        try {

          return new DerefResponseImpl(dict.convertIris(Lists.newArrayList(iri)).get(0),
              parser.parse(entity.getContent()));
        } finally {
          EntityUtils.consumeQuietly(entity);
        }
      }
    } catch (Exception e) {
      try {
        throw new ErrorResponseImpl(dict.convertIris(Lists.newArrayList(iri)).get(0), e);
      } catch (JMSException e1) {
        throw new ErrorResponseImpl(-1, e1);
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
      return null;
    }
  }
}
