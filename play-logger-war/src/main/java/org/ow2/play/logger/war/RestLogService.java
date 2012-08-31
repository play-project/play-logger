/**
 * 
 */
package org.ow2.play.logger.war;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.ow2.play.logger.api.rest.LogService;
import org.ow2.play.logger.api.rest.Logs;

/**
 * @author chamerling
 * 
 */
public class RestLogService implements LogService {

	private org.ow2.play.logger.api.LogService logService;

	@Override
	@GET
	@Path("all")
	@Produces("application/json")
	public Response logs() {
		if (logService == null) {
			return Response.serverError().build();
		}
		return Response.ok(new Logs(logService.list())).build();
	}

	@Override
	@GET
	@Path("clear")
	@Produces("application/json")
	public Response clear() {
		if (logService == null) {
			return Response.serverError().build();
		}
		logService.clear();

		return Response.ok(new Boolean(true)).build();
	}
	
	public void setLogService(org.ow2.play.logger.api.LogService logService) {
		this.logService = logService;
	}

}
