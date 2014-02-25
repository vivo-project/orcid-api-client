/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.context;

import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.actions.ActionManager;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationManager;
import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidMessage;

/**
 * TODO
 */
public abstract class OrcidClientContext {
	private static final Log log = LogFactory.getLog(OrcidClientContext.class);

	public enum Setting {
		/**
		 * ID assigned by ORCID to the application.
		 */
		CLIENT_ID,
		/**
		 * Secret code assigned by ORCID to the application.
		 */
		CLIENT_SECRET,

		/**
		 * Root of the public API (requires no authorization)
		 */
		PUBLIC_API_BASE_URL,

		/**
		 * Root of the restricted API (requires authorization)
		 */
		AUTHORIZED_API_BASE_URL,

		/**
		 * URL to obtain an authorization code. This is sent to the browser as a
		 * redirect, so the user can log in at the ORCID site.
		 */
		OAUTH_AUTHORIZE_URL,

		/**
		 * URL to exchange the authorization code for an OAuth access token.
		 */
		OAUTH_TOKEN_URL,

		/**
		 * Version of the OrcidMessage schema that the API uses.
		 */
		MESSAGE_VERSION,

		/**
		 * The base URL for contacting this webapp (including context path. Used
		 * when building the redirect URL for the browser.
		 */
		WEBAPP_BASE_URL,

		/**
		 * Where should ORCID call back to during the auth dance? Path within
		 * this webapp.
		 */
		CALLBACK_PATH
	}

	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	private static volatile OrcidClientContext instance = new OrcidClientContextNotInitialized();

	public static synchronized void initialize(Map<Setting, String> settings)
			throws OrcidClientException {
		if (instance instanceof OrcidClientContextImpl) {
			throw new IllegalStateException("Already initialized: " + instance);
		} else {
			instance = new OrcidClientContextImpl(settings);
			log.debug("initialized: " + instance);
		}
	}

	public static OrcidClientContext getInstance() {
		return instance;

	}

	// ----------------------------------------------------------------------
	// The interface
	// ----------------------------------------------------------------------

	public abstract ActionManager getActionManager(HttpServletRequest req);

	public abstract AuthorizationManager getAuthorizationManager(
			HttpServletRequest req);

	public abstract String getSetting(Setting setting);

	public abstract String getCallbackUrl();

	public abstract String getAuthCodeRequestUrl();

	public abstract String getAccessTokenRequestUrl();

	public abstract OrcidMessage unmarshall(String xml)
			throws OrcidClientException;

	public abstract String marshall(OrcidMessage message)
			throws OrcidClientException;

	public abstract String resolvePathWithWebapp(String path)
			throws URISyntaxException;

	// ----------------------------------------------------------------------
	// The empty implementation
	// ----------------------------------------------------------------------

	private static class OrcidClientContextNotInitialized extends
			OrcidClientContext {
		private static final String MESSAGE = "OrcidClientContext has not been initialized";

		@Override
		public ActionManager getActionManager(HttpServletRequest req) {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public AuthorizationManager getAuthorizationManager(
				HttpServletRequest req) {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getSetting(Setting setting) {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getCallbackUrl() {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getAuthCodeRequestUrl() {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String getAccessTokenRequestUrl() {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public OrcidMessage unmarshall(String xml) {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String marshall(OrcidMessage message) {
			throw new IllegalStateException(MESSAGE);
		}

		@Override
		public String resolvePathWithWebapp(String path) {
			throw new IllegalStateException(MESSAGE);
		}

	}

}
