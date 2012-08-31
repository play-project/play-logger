/**
 *
 * Copyright (c) 2012, PetalsLink
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA 
 *
 */
package org.ow2.play.logger.api.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.ow2.play.logger.api.Log;

/**
 * Used to get subscriptions list for REST services without any additional
 * factory, writer...
 * 
 * @author chamerling
 * 
 */
@XmlRootElement(name = "logs")
public class Logs {
	
	@XmlElement(name = "log")
	private List<Log> s;

	public Logs() {
	}

	public Logs(List<Log> s) {
		this.s = s;
	}

	public List<Log> get() {
		return s;
	}

	public void set(List<Log> c) {
		this.s = c;
	}
}
