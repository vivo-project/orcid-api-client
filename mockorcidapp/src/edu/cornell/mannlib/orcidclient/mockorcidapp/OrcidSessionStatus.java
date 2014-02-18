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

	
	// ----------------------------------------------------------------------
	// Helper class
	// ----------------------------------------------------------------------

	public boolean isLoggedIn() {
		return orcid != null;
	}

	public String getOrcid() {
		return orcid;
	}


	public void setOrcid(String orcid) {
		this.orcid = orcid;
	}


	private static class ScopeStatus {
		private String redirectUri;
		private ScopePathType scope;
		private String state;
		private String authCode;
		private AccessToken accessToken;
	}
}
