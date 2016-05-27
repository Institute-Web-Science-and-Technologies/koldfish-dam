/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.unikoblenz.west.koldfish.dam.DerefResponse;

/**
 * implementation for DerefResponse
 * 
 * @author lkastler
 */
public class DerefResponseImpl implements DerefResponse {

  private static final long serialVersionUID = -7152350152296447000L;

  private final long compressedIri;
  
  private LinkedList<long[]> data;

  public DerefResponseImpl(long compressedIri) {
    this.compressedIri = compressedIri;
    this.data = new LinkedList<long[]>();
  }

  public DerefResponseImpl(long compressedIri, List<long[]> data) {
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
    return "DerefResponseImpl [size=" + data.size() + "]";
  }

  /* (non-Javadoc)
   * @see de.unikoblenz.west.koldfish.dam.DerefResponse#getDerefIri()
   */
  @Override
  public long getEncodedDerefIri() {
    return compressedIri;
  }


}
