/**
 * 
 */
package org.ow2.play.logger.api;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author chamerling
 * 
 */
@XmlRootElement
public class Log {

	private String context;

	private String date;

	private String message;
	
	public Log() {
	}

	public Log(String context, String date, String message) {
		this.context = context;
		this.date = date;
		this.message = message;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
