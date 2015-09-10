package de.unikoblenz.west.koldfish.dam.impl.messages;

import org.apache.jena.iri.IRI;

import de.unikoblenz.west.koldfish.dam.messages.ReportMessage;

/**
 * reports an Exception to registered Receiver objects. 
 * 
 * @author lkastler@uni-koblenz.de
 *
 */
public class ExceptionReportMessage extends Exception implements ReportMessage<Throwable> {

	private static final long serialVersionUID = 1L;

	private final IRI iri;
	
	/**
	 * creates an ExceptionReportModel with given Throwable as payload.
	 * @param iri - IRI that caused the execption.
	 * @param cause - the cause for this ExceptionReportMessage.
	 */
	public ExceptionReportMessage(IRI iri, Throwable cause) {
		super(cause);
		this.iri = iri;
	}
	
	@Override
	public Throwable getPayload() {
		return this;
	}

	@Override
	public IRI getResourceIRI() {
		return iri;
	}
}
