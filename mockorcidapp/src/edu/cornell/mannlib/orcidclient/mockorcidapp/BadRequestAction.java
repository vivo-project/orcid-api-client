/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.IOException;
import java.io.PrintWriter;

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

	public void exception(Exception e) throws IOException {
		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		PrintWriter out = resp.getWriter();
		out.println("<html><head></head><body>");
		out.println("<h1>Path info was '" + req.getPathInfo() + "'</h1>");
		out.println("<h1>Servlet threw an exception:</h1>");
		out.println("<pre>");
		e.printStackTrace(out);
		out.println("</pre>");
		out.println("</body></html>");

		log.error("Servlet threw an exception" + e);
		log.error("Path info was '" + req.getPathInfo() + "'");
	}

}
