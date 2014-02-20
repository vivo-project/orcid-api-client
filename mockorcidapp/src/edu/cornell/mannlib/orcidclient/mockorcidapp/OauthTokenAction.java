/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.cornell.mannlib.orcidclient.mockorcidapp.OrcidSessionStatus.ScopeStatus;

/**
 * Ignore client_id, client_secret, grant_type, redirect_uri
 * 
 * Require code parameter, and Accept header.
 * 
 * On doPost, parse the URL:
 * 
 * <pre>
 *    POST /oauth/token
 * 		List<NameValuePair> form = Form.form()
 * 				.add("client_id", context.getSetting(CLIENT_ID))
 * 				.add("client_secret", context.getSetting(CLIENT_SECRET))
 * 				.add("grant_type", "authorization_code")
 * 				.add("code", auth.getAuthorizationCode())
 * 				.add("redirect_uri", context.getCallbackUrl()).build();
 * 		Request request = Request.Post(context.getAccessTokenRequestUrl())
 * 				.addHeader("Accept", "application/json").bodyForm(form);
 * </pre>
 * 
 * 
 * Respond with
 * 
 * <pre>
 *    {
 *       "access_token":"785e8e34-0f66-4c98-a138-c89bc8cb3886",
 *       "token_type":"bearer",
 *       "refresh_token":"5ecda111-b6f9-4bd4-a21d-153962eabca9",
 *       "expires_in":628207503,
 *       "scope":"/orcid-profile/read-limited",
 *       "orcid":"0000-0003-3479-6011"
 *    }
 *    Where the ORCID is a truncation of the accessToken, which is a form of the auth_code.
 *    Scope is what was requested when the auth_code was created
 * </pre>
 * 
 * token_type is hard-coded. Use hard-coded bogus values for refresh_token,
 * expires_in. Get scope and orcid from the authorization with this code.
 * 
 * TODO: How do we respond if the authCode is not found?
 */
public class OauthTokenAction extends AbstractAction {

	public static boolean matches(String pathInfo) {
		return "/oauth/token".equals(pathInfo);
	}

	private String authCode;
	private ScopeStatus status;

	public OauthTokenAction(HttpServletRequest req, HttpServletResponse resp) {
		super(req, resp);
	}

	public void doPost() throws IOException {
		authCode = getRequiredParameter("code");
		status = oss.getStatusByAuthCode(authCode);
		if (status == null) {
			returnFailure();
		} else {
			returnSuccess();
		}
	}

	private void returnFailure() {
		throw new IllegalStateException("No such authCode on record: "
				+ authCode);
	}

	private void returnSuccess() throws IOException {
		String json = String
				.format("{ \n" //
						+ "   \"access_token\":\"%s\", \n" //
						+ "   \"token_type\":\"bearer\", \n" //
						+ "   \"refresh_token\":\"bogus\", \n" //
						+ "   \"expires_in\":628207503, \n" //
						+ "   \"scope\":\"%s\", \n" //
						+ "   \"orcid\":\"%s\" \n" //
						+ "}", //
						"ACCESS-" + authCode, status.getScope().value(),
						oss.getOrcid());
		resp.setContentType("application/json");
		resp.getWriter().println(json);
	}
}
