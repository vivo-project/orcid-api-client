/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.mockorcidapp.OrcidSessionStatus.ScopeStatus;

/**
 * <pre>
 *    GET /login?orcid=xxxx-xxxx-xxxx-xxxx
 *        Record the log in, in the session. retrieve the auth parameters from the hidden fields, 
 *            and continue with the auth request
 *        Show the appropriate auth screen, with the scope in a hidden field
 * </pre>
 * 
 * TODO
 */
public class LoginAction {
	private static final Log log = LogFactory.getLog(LoginAction.class);

	private static final Pattern LOGIN_LINK_PATTERN = Pattern
			.compile("\\{\\{loginLink(.*)\\}\\}");

	private final HttpServletRequest req;
	private final HttpSession session;
	private final ServletContext ctx;
	private final HttpServletResponse resp;
	private final OrcidSessionStatus oss;

	public static boolean matches(String pathInfo) {
		return "/login".equals(pathInfo);
	}

	public LoginAction(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.session = req.getSession();
		this.ctx = this.session.getServletContext();
		this.resp = resp;
		this.oss = OrcidSessionStatus.fetch(this.session);
	}

	public void doGet() throws IOException {
		String orcid = req.getParameter("orcid");
		if (orcid == null) {
			showLoginPage();
		} else {
			recordLogin(orcid);
			returnToAuthRequest();
		}
	}

	private void recordLogin(String orcid) {
		oss.setOrcid(orcid);
	}

	private void returnToAuthRequest() throws IOException {
		ScopeStatus pending = oss.getPendingAuthorization();
		String url = String
				.format("%s/oauth/authorize?scope=%s&redirect_uri=%s&state=%s",
						req.getContextPath(), pending.getScope(),
						URLEncoder.encode(pending.getRedirectUri(), "UTF-8"),
						pending.getState());
		resp.sendRedirect(url);
	}

	private void showLoginPage() throws IOException {
		resp.setContentType("text/html");
		resp.getWriter().println(insertContext(insertLinks(getTemplate())));
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
		return matcher.group(1);
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

	private String insertContext(String template) {
		return template.replace("_CONTEXT_", req.getContextPath());
	}

}
