/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.util.Map;

import javax.servlet.http.HttpSession;

import edu.cornell.mannlib.orcidclient.auth.AccessToken;
import edu.cornell.mannlib.orcidclient.orcidmessage.ScopePathType;

/**
 * TODO
 */
public class OrcidSessionStatus {
	private static final String ATTRIBUTE_NAME = OrcidSessionStatus.class
			.getName();

	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	public static OrcidSessionStatus fetch(HttpSession session) {
		Object o = session.getAttribute(ATTRIBUTE_NAME);
		if (o instanceof OrcidSessionStatus) {
			return (OrcidSessionStatus) o;
		} else {
			OrcidSessionStatus oss = new OrcidSessionStatus();
			session.setAttribute(ATTRIBUTE_NAME, oss);
			return oss;
		}
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	private String orcid;
	private Map<ScopePathType, ScopeStatus> map;
	private ScopeStatus pendingAuthorization;

	public boolean isLoggedIn() {
		return orcid != null;
	}

	public String getOrcid() {
		return orcid;
	}

	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}

	public ScopeStatus getPendingAuthorization() {
		return pendingAuthorization;
	}

	public void setPendingAuthorization(String redirectUri,
			ScopePathType scope, String state) {
		this.pendingAuthorization = new ScopeStatus(redirectUri, scope, state);
	}

	// ----------------------------------------------------------------------
	// Helper class
	// ----------------------------------------------------------------------

	public static class ScopeStatus {
		private final String redirectUri;
		private final ScopePathType scope;
		private final String state;
		private String authCode;
		private AccessToken accessToken;

		public ScopeStatus(String redirectUri, ScopePathType scope, String state) {
			this.redirectUri = redirectUri;
			this.scope = scope;
			this.state = state;
		}

		public String getAuthCode() {
			return authCode;
		}

		public void setAuthCode(String authCode) {
			this.authCode = authCode;
		}

		public AccessToken getAccessToken() {
			return accessToken;
		}

		public void setAccessToken(AccessToken accessToken) {
			this.accessToken = accessToken;
		}

		public String getRedirectUri() {
			return redirectUri;
		}

		public ScopePathType getScope() {
			return scope;
		}

		public String getState() {
			return state;
		}

	}
}
