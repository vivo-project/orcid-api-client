/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import static edu.cornell.mannlib.orcidclient.actions.ApiAction.READ_PROFILE;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.actions.ActionManager;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationManager;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidMessage;

/**
 * TODO
 */
public class ProfileReader {
	private final HttpServletRequest req;
	private final HttpServletResponse resp;
	private final PrintWriter out;
	private final OrcidClientContext occ;
	private final AuthorizationManager authManager;
	private final ActionManager actionManager;

	public ProfileReader(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		this.req = req;
		this.resp = resp;
		this.out = resp.getWriter();
		resp.setContentType("text/html");

		this.occ = OrcidClientContext.getInstance();
		this.authManager = occ.getAuthorizationManager(req);
		this.actionManager = occ.getActionManager(req);
	}

	public void exec() throws IOException {
		AuthorizationStatus authStatus = authManager
				.getAuthorizationStatus(READ_PROFILE);
		switch (authStatus.getState()) {
		case PENDING:
			showAuthorizationPending();
			break;
		case DECLINED:
			showAuthorizationDeclined();
			break;
		case FAILED:
			showAuthorizationFailure(authStatus);
			break;
		case SUCCESS:
			fetchAndShowProfile();
			break;
		default: // NONE
			seekAuthorization();
		}
	}

	private void showAuthorizationPending() {
		out.println("<html><head></head><body>");
		out.println("<h1>Authorization Failure</h1>");
		out.println("</body></html>");
	}

	private void showAuthorizationDeclined() {
		out.println("<html><head></head><body>");
		out.println("<h1>Authorization Declined</h1>");
		out.println("</body></html>");
	}

	private void showAuthorizationFailure(AuthorizationStatus authStatus) {
		out.println("<html><head></head><body>");
		out.println("<h1>Authorization Failure</h1>");
		out.println(authStatus);
		out.println("</body></html>");
	}

	private void fetchAndShowProfile() {
		try {
			OrcidMessage message = actionManager.execute(READ_PROFILE);
			String marshalled = occ.marshall(message);

			out.println("<html><head></head><body>");
			out.println("<h1>User profile for " + authManager.getOrcId()
					+ "</h1>");
			out.println(marshalled);
			out.println("</body></html>");
		} catch (OrcidClientException e) {
			showInternalError(e);
		}
	}

	private void seekAuthorization() throws IOException {
		String authUrl;
		try {
			String returnUrl = occ.resolvePathWithWebapp("request?readProfile=true");
			authUrl = authManager.seekAuthorization(READ_PROFILE, returnUrl);
			resp.sendRedirect(authUrl);
		} catch (OrcidClientException | URISyntaxException e) {
			showInternalError(e);
		}
	}

	private void showInternalError(Exception e) {
		out.println("<html><head></head><body>");
		out.println("<h1>Internal Error</h1>");
		out.println("<pre>");
		e.printStackTrace(out);
		out.println("</pre>");
		out.println("</body></html>");
	}
}
