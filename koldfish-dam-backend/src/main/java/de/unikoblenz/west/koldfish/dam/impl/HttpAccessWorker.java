package de.unikoblenz.west.koldfish.dam.impl;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import de.unikoblenz.west.koldfish.dam.AccessWorkerException;
import de.unikoblenz.west.koldfish.dam.DataAccessWorker;

/**
 * @author lkastler
 *
 */
public class HttpAccessWorker implements DataAccessWorker<String> {

	private final String iri;
	
	private static final Header[] headers = new Header[]{
			new BasicHeader("Accept", "text/turtle, application/rdf+xml, application/xml"),
			new BasicHeader("User-Agent", "koldfish"),
			new BasicHeader("Accept-Encoding", "gzip")
		}; 
	
	public HttpAccessWorker(String iri) {
		this.iri = iri;
	}
	
	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public String call() throws Exception {
		try (CloseableHttpClient httpclient = HttpClients.createDefault()){
			HttpGet httpget = new HttpGet(iri);
			httpget.setHeaders(headers);
			
			try (CloseableHttpResponse response = httpclient.execute(httpget)){
			    return EntityUtils.toString(response.getEntity());
			}
		} catch(Exception e) {
			throw new AccessWorkerException(e);
		}
	}

	/* (non-Javadoc)
	 * @see java.util.function.Supplier#get()
	 */
	@Override
	public String get() {
		try {
			return call();
		} catch (Exception e) {
			throw new AccessWorkerException(e);
		}
	}
}
