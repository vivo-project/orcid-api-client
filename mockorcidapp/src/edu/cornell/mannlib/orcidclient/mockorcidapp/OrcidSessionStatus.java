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

	// ----------------------------------------------------------------------
	// The factory
	// ----------------------------------------------------------------------

	public static String create(HttpSession session, ScopePathType scope,
			String redirectUri, String state) {
		throw new RuntimeException("OrcidSessionStatus.create not implemented.");
	}

	public static OrcidSessionStatus fetch(HttpSession session) {
		throw new RuntimeException("OrcidSessionStatus.fetch not implemented.");
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	private String orcid;
	private Map<ScopePathType, ScopeStatus> map;

	// ----------------------------------------------------------------------
	// Helper class
	// ----------------------------------------------------------------------

	private static class ScopeStatus {
		private String redirectUri;
		private ScopePathType scope;
		private String state;
		private String authCode;
		private AccessToken accessToken;
	}
}
