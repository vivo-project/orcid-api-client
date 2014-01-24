/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.auth;

import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CLIENT_ID;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

}
