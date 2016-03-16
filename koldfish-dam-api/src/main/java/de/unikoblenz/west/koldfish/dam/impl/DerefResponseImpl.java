/**
 * 
 */
package de.unikoblenz.west.koldfish.dam.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.unikoblenz.west.koldfish.dam.DerefResponse;

/**
 * @author lkastler
 *
 */
public class DerefResponseImpl implements DerefResponse {

	
	private LinkedList<long[]> data;
	
	public DerefResponseImpl() {
		this.data = new LinkedList<long[]>();
	}
	
	public DerefResponseImpl(List<long[]> data) {
		this.data = new LinkedList<long[]>(data);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<long[]> iterator() {
		return data.iterator();
	}

	@Override
	public String toString() {
		return "DerefResponseImpl [size=" + data.size() +"]";
	}

	
}
