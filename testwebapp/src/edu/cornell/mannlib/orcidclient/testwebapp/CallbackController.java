/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationManager;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus.State;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;

/**
 * TODO
 */
public class CallbackController extends HttpServlet {
	private static final Log log = LogFactory.getLog(CallbackController.class);

	private OrcidClientContext occ;

	@Override
	public void init() throws ServletException {
		occ = OrcidClientContext.getInstance();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		AuthorizationManager authManager = occ.getAuthorizationManager(req);
		try {
			AuthorizationStatus authStatus = authManager
					.processAuthorizationResponse();
			if (authStatus.getState() == State.SUCCESS) {
				resp.sendRedirect(authStatus.getSuccessUrl());
			} else {
				resp.sendRedirect(authStatus.getFailureUrl());
			}
		} catch (OrcidClientException e) {
			log.error("Invalid authorization response", e);
			fail(resp, "Invalid authorization response: " + e);
		}
	}

	private void fail(HttpServletResponse resp, String message)
			throws IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<html><head></head><body>");
		out.println("<h1>Callback Failure</h1>");
		out.println("<h2>" + message + "</h2>");
		out.println("</body></html");
	}

}
