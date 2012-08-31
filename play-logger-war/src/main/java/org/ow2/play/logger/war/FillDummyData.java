/**
 * 
 */
package org.ow2.play.logger.war;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.ow2.play.logger.api.Log;
import org.ow2.play.logger.api.LogService;

/**
 * @author chamerling
 * 
 */
@Path("/fill/")
public class FillDummyData {

	private LogService logService;

	@Path("go")
	public Response go() {
		if (logService != null) {
			for (int i = 0; i < 10; i++) {
				Log log = new Log("TEST", System.currentTimeMillis() + "",
						"Message " + i);
				logService.push(log);
			}
		}
		return Response.ok().build();
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}
}
