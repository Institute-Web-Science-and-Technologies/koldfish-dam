/**
 * 
 */
package de.unikoblenz.west.koldfish.messages;

import java.util.Collection;
import java.util.Iterator;

/**
 * implementation for DerefResponse
 * 
 * @author lkastler
 */
public class DerefResponse implements KoldfishMessage, Iterable<long[]> {

  private static final long serialVersionUID = -7152350152296447000L;

  private final long compressedIri;

  private Iterable<long[]> data;

  public DerefResponse(long compressedIri, Collection<long[]> data) {
    this.compressedIri = compressedIri;
    this.data = data;
  }

  public DerefResponse(long compressedIri, Iterable<long[]> data) {
    this.compressedIri = compressedIri;
    this.data = data;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<long[]> iterator() {
    return data.iterator();
  }

  @Override
  public String toString() {
    return "DerefResponse [compressedIri=" + compressedIri + "]";
  }

  /**
   * returns the encoded dereferenced IRI.
   * 
   * @return the encoded dereferenced IRI.
   */
  public long getEncodedDerefIri() {
    return compressedIri;
  }


}
