/**
 * 
 */
package org.ow2.play.logger.api;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * A simpple logger definition
 * 
 * @author chamerling
 * 
 */
@WebService
public interface LogService {

	/**
	 * Log a new message
	 * 
	 * @param message
	 */
	@WebMethod
	void pushMessage(String message);

	@WebMethod
	void push(Log log);

	/**
	 * Get all the logs
	 * 
	 * @return
	 */
	@WebMethod
	List<Log> list();

	/**
	 * Clear the logs
	 */
	@WebMethod
	void clear();

}
