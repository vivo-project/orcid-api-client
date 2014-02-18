/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
public class BadRequestAction {
	private static final Log log = LogFactory.getLog(BadRequestAction.class);
	
	private final HttpServletRequest req;
	private final HttpServletResponse resp;

	public BadRequestAction(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}

	/**
	 * 
	 */
	public void doPost() {
		// TODO Auto-generated method stub
		throw new RuntimeException("BadRequestAction.doPost() not implemented.");
	}

	/**
	 * 
	 */
	public void doGet() {
		// TODO Auto-generated method stub
		throw new RuntimeException("BadRequestAction.doGet() not implemented.");
	}

	public void exception(Exception e) {
		log.error("Threw an exception: " + e);
	}

}
