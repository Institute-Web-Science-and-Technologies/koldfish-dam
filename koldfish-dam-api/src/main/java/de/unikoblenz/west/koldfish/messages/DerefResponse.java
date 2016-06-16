/**
 * 
 */
package de.unikoblenz.west.koldfish.messages;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * implementation for DerefResponse
 * 
 * @author lkastler
 */
public class DerefResponse implements KoldfishMessage, Iterable<long[]> {

  private static final long serialVersionUID = -7152350152296447000L;

  private final long compressedIri;

  private LinkedList<long[]> data;

  public DerefResponse(long compressedIri, List<long[]> data) {
    this.compressedIri = compressedIri;
    this.data = new LinkedList<long[]>(data);
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
    return "DerefResponse [compressedIri=" + compressedIri + ", size=" + data.size() + "]";
  }

  public long getEncodedDerefIri() {
    return compressedIri;
  }


}
