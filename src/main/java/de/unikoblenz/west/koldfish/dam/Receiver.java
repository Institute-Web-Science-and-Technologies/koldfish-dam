package de.unikoblenz.west.koldfish.dam;

import de.unikoblenz.west.koldfish.dam.messages.ReportMessage;

/**
 * listener to a Negotiator.
 * 
 * @author lkastler@uni-koblenz.de
 */
public interface Receiver {

	/**
	 * reports a ReportMessage with the given data.
	 * @param rm - ReportMessage to report.
	 */
	public void report(ReportMessage<?> rm);
}
