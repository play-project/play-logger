/**
 * 
 */
package org.ow2.play.logger.api.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST service
 * 
 * @author chamerling
 * 
 */
@Path("/log/")
public interface LogService {

	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	Response logs();

	@GET
	@Path("clear")
	@Produces(MediaType.APPLICATION_JSON)
	Response clear();

}
