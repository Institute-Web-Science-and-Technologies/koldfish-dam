/**attempt
 * 
 */
package de.unikoblenz.west.koldfish.dam;

/**
 * interface to listen on a DataAccessModule object, handling all incoming information.
 * 
 * @author lkastler
 */
public interface DataAccessModuleListener {

	/**
	 * triggered if the DataAccessModule receives given DerefResponse from the backend
	 * @param response - response from the DataAccessModule for an IRI deref attempt.
	 */
	public void onDerefResponse(DerefResponse response);
	
	/**
	 * triggered if the DataAccessModule receives given ErrorResponse from the backend.
	 * @param response - response from the DataAccessModule backend for an unsuccessful IRI deref attempt.
	 */
	public void onErrorResponse(ErrorResponse response);
}
