/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.auth.AccessToken;
import edu.cornell.mannlib.orcidclient.orcidmessage.ScopePathType;

/**
 * Hold the orcid status for this login session to the mock app.
 */
public class OrcidSessionStatus {
	private static final Log log = LogFactory.getLog(OrcidSessionStatus.class);

	private static final String ATTRIBUTE_NAME = OrcidSessionStatus.class
			.getName();

	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	public static OrcidSessionStatus fetch(HttpSession session) {
		log.debug("session: " + session);
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
	private final Map<ScopePathType, ScopeStatus> statusMap = new HashMap<>();
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

	public boolean isAuthorizationPending() {
		return pendingAuthorization != null;
	}

	public ScopeStatus getPendingAuthorization() {
		return pendingAuthorization;
	}

	public void setPendingAuthorization(String redirectUri,
			ScopePathType scope, String state) {
		this.pendingAuthorization = new ScopeStatus(redirectUri, scope, state);
	}

	public void confirmPendingAuthorization() {
		statusMap.put(pendingAuthorization.getScope(), pendingAuthorization);
		clearPendingAuthorization();
	}

	public void clearPendingAuthorization() {
		this.pendingAuthorization = null;
	}

	public ScopeStatus getStatusByAuthCode(String authCode) {
		for (ScopeStatus ss : statusMap.values()) {
			if (authCode.equals(ss.authCode)) {
				return ss;
			}
		}
		return null;
	}

	public ScopeStatus getStatusByScope(ScopePathType scope) {
		return statusMap.get(scope);
	}

	// ----------------------------------------------------------------------
	// Helper class
	// ----------------------------------------------------------------------

	@Override
	public String toString() {
		return "OrcidSessionStatus [orcid=" + orcid + ", pendingAuthorization="
				+ pendingAuthorization + ", statusMap=" + statusMap + "]";
	}

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

		@Override
		public String toString() {
			return "ScopeStatus[redirectUri=" + redirectUri + ", scope="
					+ scope + ", state=" + state + ", authCode=" + authCode
					+ ", accessToken=" + accessToken + "]";
		}

	}

}
