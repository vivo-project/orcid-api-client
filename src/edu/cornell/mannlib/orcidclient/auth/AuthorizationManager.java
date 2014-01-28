/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.auth;

import static edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus.State.PENDING;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CLIENT_ID;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CLIENT_SECRET;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.actions.ApiAction;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;

/**
 * TODO
 */
public class AuthorizationManager {
	private static final Log log = LogFactory
			.getLog(AuthorizationManager.class);

	public static final String NOT_AUTHENTICATED = "Not Authenticated";

	private final OrcidClientContext context;
	private final HttpServletRequest req;
	private final AuthorizationCache cache;

	private String orcId = NOT_AUTHENTICATED;

	public AuthorizationManager(OrcidClientContext context,
			HttpServletRequest req) {
		this.context = context;
		this.req = req;
		this.cache = AuthorizationCache.getCache(req);

	}

	/**
	 * Write a URL that can be sent to the browser as a redirect, that will
	 * attempt to obtain authorization for this action.
	 * 
	 * Create an AuthorizationStatus object in the session, and include that in
	 * the URL, so we can track the progress.
	 */
	public String seekAuthorization(ApiAction action, String returnUrl)
			throws OrcidClientException {
		return seekAuthorization(action, returnUrl, returnUrl);
	}

	public String seekAuthorization(ApiAction action, String successUrl,
			String failureUrl) throws OrcidClientException {
		log.debug("seekAuthorization: action=" + action + ", successUrl="
				+ successUrl + ", failureUrl=" + failureUrl);
		AuthorizationStatus authStatus = cache.createStatus(action, successUrl,
				failureUrl);
		try {
			URI fullUri = new URIBuilder(context.getAuthCodeRequestUrl())
					.addParameter("client_id", context.getSetting(CLIENT_ID))
					.addParameter("scope", action.getScope())
					.addParameter("response_type", "code")
					.addParameter("redirect_uri", context.getCallbackUrl())
					.addParameter("state", authStatus.getId()).build();
			log.debug("fullUri=" + fullUri);
			return fullUri.toString();
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					"Failed to build the authorization URL for " + action, e);
		}
	}

	/**
	 * We received a callback which should contain an authorization code. Use
	 * that code to request an access token. Update the AuthorizationStatus
	 * accordingly.
	 */
	public AuthorizationStatus processAuthorizationResponse()
			throws OrcidClientException {
		String token = req.getParameter("state");
		if (token == null || token.isEmpty()) {
			throw new OrcidClientException(
					"Request did not contain a 'state' parameter");
		}

		AuthorizationStatus authStatus = getAuthorizationStatus(token);
		if (authStatus.getState() != PENDING) {
			throw new OrcidClientException(
					"Authorization request is not pending: token=" + token
							+ ", status=" + authStatus);
		}

		String error = req.getParameter("error");
		if (error != null && !error.isEmpty()) {
			String errorDescription = req.getParameter("error_description");
			if (errorDescription == null || errorDescription.isEmpty()) {
				authStatus.setFailure("unknown error");
			} else {
				authStatus.setFailure(errorDescription);
			}
		}

		String authCode = req.getParameter("code");
		if (authCode == null || authCode.isEmpty()) {
			throw new OrcidClientException(
					"Authorization request did not return an authorization code.");
		}

		// TODO Set the authStatus as "SEEKING_ACCESS_TOKEN"

		List<NameValuePair> form = Form.form()
				.add("client_id", context.getSetting(CLIENT_ID))
				.add("client_secret", context.getSetting(CLIENT_SECRET))
				.add("grant_type", "authorization_code").add("code", authCode)
				.add("redirect_uri", context.getCallbackUrl()).build();

		Request request = Request.Post(context.getAccessTokenRequestUrl())
				.addHeader("Accept", "application/json").bodyForm(form);

		try {
			Response response = request.execute();
			String string = response.returnContent().asString();
			authStatus.setSuccess(new AccessToken(string));
			return authStatus;
		} catch (IOException e) {
			throw new OrcidClientException("Request for access token failed.",
					e);
		}
	}

	public String getOrcId() {
		return orcId;
	}

	public void setOrcId(String orcId) {
		this.orcId = orcId;
	}

	public AuthorizationStatus getAuthorizationStatus(ApiAction action) {
		return cache.getStatus(action);
	}

	public AuthorizationStatus getAuthorizationStatus(String token) {
		return cache.getStatus(token);
	}

	public void clearStatus(ApiAction action) {
		cache.clearStatus(action);
	}

}
