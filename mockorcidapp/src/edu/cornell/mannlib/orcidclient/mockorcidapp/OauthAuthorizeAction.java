/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
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
 * 
 * 
 * </pre>
 */
public class OauthAuthorizeAction {
	private static final Log log = LogFactory
			.getLog(OauthAuthorizeAction.class);

	private static final Pattern LOGIN_LINK_PATTERN = Pattern
			.compile("{{loginLink.*}}");

	private final HttpServletRequest req;
	private final HttpSession session;
	private final ServletContext ctx;
	private final HttpServletResponse resp;

	private String clientId;
	private String redirectUri;
	private String state;
	private ScopePathType scope;
	private OrcidSessionStatus oss;

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
		this.ctx = this.session.getServletContext();
		this.resp = resp;
	}

	public void doGet() throws IOException {
		obtainAndValidateParameters();
		getOrcidStatus();
		if (oss.isLoggedIn()) {
			forwardToLoggedIn();
		} else {
			showLoginPage();
		}
	}

	private void obtainAndValidateParameters() {
		clientId = getRequiredParameter("client_id");
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

	private void getOrcidStatus() {
		oss = OrcidSessionStatus.fetch(session);
	}

	private void forwardToLoggedIn() throws IOException {
		String loggedInUrl = req.getContextPath() + "/login?orcid="
				+ oss.getOrcid();
		resp.sendRedirect(loggedInUrl);
	}

	private void showLoginPage() throws IOException {
		resp.setContentType("text/html");
		resp.getWriter().println(insertLinks(getTemplate()));
	}

	private String getTemplate() throws IOException {
		return IOUtils.toString(ctx.getResourceAsStream("/login.template"));
	}

	private String insertLinks(String htmlTemplate) {
		String linkTemplate = findLinkTemplate(htmlTemplate);
		String links = assembleLinksHtml(linkTemplate);
		String html = putLinksIntoTemplate(htmlTemplate, links);
		
		log.debug("html with links: \n" + html);
		return html;
	}

	private String findLinkTemplate(String htmlTemplate) {
		Matcher matcher = LOGIN_LINK_PATTERN.matcher(htmlTemplate);
		if (!matcher.find()) {
			throw new IllegalStateException("Didn't find the "
					+ "login link pattern in the login.html template.");
		}
		return matcher.group();
	}

	private String assembleLinksHtml(String linkTemplate) {
		StringBuilder links = new StringBuilder();
		for (String orcid : OrcidProfiles.getOrcids()) {
			links.append(linkTemplate.replace("_ORCID_", orcid));
		}
		return links.toString();
	}

	private String putLinksIntoTemplate(String htmlTemplate, String links) {
		return LOGIN_LINK_PATTERN.matcher(htmlTemplate).replaceFirst(links);
	}

}
