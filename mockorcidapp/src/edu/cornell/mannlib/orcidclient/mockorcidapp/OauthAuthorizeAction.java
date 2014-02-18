/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.orcidmessage.ScopePathType;

/**
 * TODO
 * 
 * Ignore client_id and response_type.
 * 
 * <pre>
 *    GET http://sandbox-1.orcid.org/oauth/authorize ?
 * 	      client_id=0000-0002-4639-029X & 
 * 	      scope=%2Forcid-profile%2Fread-limited &
 * 	      response_type=code & 
 * 	      redirect_uri=http%3A%2F%2Fjeb228-dev.library.cornell.edu%2Forcivo%2Fcallback & 
 * 	      state=1728933982
 *    If not logged in, show the login screen, with the parameters in hidden fields
 *        Else, redirect to /login with the orcid and parameters
 * </pre>
 * 
 * TODO What do we do if they are already authorized for a scope? It appears
 * that we go through it again.
 */
public class OauthAuthorizeAction {
	private static final Log log = LogFactory
			.getLog(OauthAuthorizeAction.class);

	private final HttpServletRequest req;
	private final HttpSession session;
	private final HttpServletResponse resp;
	private final OrcidSessionStatus oss;

	private String redirectUri;
	private String state;
	private ScopePathType scope;


	/**
	 * Match something like http://sandbox-1.orcid.org/oauth/authorize
	 */
	public static boolean matches(String pathInfo) {
		String[] parts = pathInfo.split("/");
		return (parts.length == 3) && "oauth".equals(parts[1])
				&& "authorize".equals(parts[2]);
	}

	public OauthAuthorizeAction(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.session = req.getSession();
		this.resp = resp;
		this.oss = OrcidSessionStatus.fetch(this.session);
	}

	public void doGet() throws IOException {
		obtainAndValidateParameters();
		setPendingAuthorization();

		if (oss.isLoggedIn()) {
			redirectToLoggedIn();
		} else {
			redirectToLoggingIn();
		}
	}

	private void obtainAndValidateParameters() {
		redirectUri = getRequiredParameter("redirect_uri");
		state = getRequiredParameter("state");
		scope = convertToScope(getRequiredParameter("scope"));
	}

	private String getRequiredParameter(String name) {
		String value = req.getParameter(name);
		if (value == null) {
			throw new IllegalStateException(
					"Authorize request has no parameter for '" + name + "'");
		}
		return value;
	}

	private ScopePathType convertToScope(String scopeString) {
		return ScopePathType.fromValue(scopeString);
	}

	private void setPendingAuthorization() {
		oss.setPendingAuthorization(redirectUri, scope, state);
	}

	private void redirectToLoggedIn() throws IOException {
		String loggedInUrl = req.getContextPath() + "/login?orcid="
				+ oss.getOrcid();
		resp.sendRedirect(loggedInUrl);
	}
	
	private void redirectToLoggingIn() throws IOException {
		String loggingInUrl = req.getContextPath() + "/login";
		resp.sendRedirect(loggingInUrl);
	}

}
