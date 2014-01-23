/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import static edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus.State.PENDING;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CLIENT_ID;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CLIENT_SECRET;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;

import edu.cornell.mannlib.orcidclient.auth.AccessToken;
import edu.cornell.mannlib.orcidclient.auth.AccessTokenFormatException;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationManager;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
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
		String token = req.getParameter("state");
		if (token == null) {
			fail(req, resp, "Request did not contain a 'state' parameter");
			return;
		}

		AuthorizationManager authManager = occ.getAuthorizationManager(req);
		AuthorizationStatus authStatus = authManager
				.getAuthorizationStatus(token);
		if (authStatus.getState() != PENDING) {
			fail(req, resp, "Request not pending: token=, status=" + authStatus);
			return;
		}

		String authCode = req.getParameter("code");
		if (authCode == null) {
			fail(req, resp, "No access code returned: " + dumpRequest(req));
		}

		try {
			authStatus.setSuccess(getAccessTokenFromAuthCode(authCode));
		} catch (Exception e) {
			authStatus.setFailure(e.toString());
			fail(req, resp, "Failed to get the Access Token: " + e);
		}
	}

	private AccessToken getAccessTokenFromAuthCode(String authCode)
			throws ClientProtocolException, IOException,
			AccessTokenFormatException {
		Request request = Request
				.Post(occ.getAccessTokenRequestUrl())
				.addHeader("Accept", "application/json")
				.bodyForm(
						Form.form()
								.add("client_id", occ.getSetting(CLIENT_ID))
								.add("client_secret",
										occ.getSetting(CLIENT_SECRET))
								.add("grant_type", "authorization_code")
								.add("code", authCode)
								.add("redirect_uri", occ.getCallbackUrl())
								.build());
		Response response = request.execute();
		Content content = response.returnContent();
		String string = content.asString();
		return new AccessToken(string);
	}

	private void fail(HttpServletRequest req, HttpServletResponse resp,
			String message) throws IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<html><head></head><body>");
		out.println("<h1>Callback Failure</h1>");
		out.println("<h2>" + message + "</h2>");
		out.println("</body></html");
	}

	private String dumpRequest(HttpServletRequest req) {
		@SuppressWarnings("unchecked")
		Map<String, String[]> rawMap = req.getParameterMap();
		Map<String, List<String>> prettyMap = new HashMap<>();
		for (String key : rawMap.keySet()) {
			prettyMap.put(key, Arrays.asList(rawMap.get(key)));
		}
		return "Request: " + req.getRequestURL() + ", parameters=" + prettyMap;
	}

}
