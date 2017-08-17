/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.actions;

/**
 * TODO
 */
public enum ApiAction {
	NO_ACTION(""),
	
	AUTHENTICATE("/authenticate"),

	READ_PROFILE("/orcid-profile/read-limited"),

	ADD_EXTERNAL_ID("/orcid-bio/external-identifiers/create"),

	UPDATE_BIO("/orcid-bio/update");

	private final String scope;

	private ApiAction(String scope) {
		this.scope = scope;
	}

	public String getScope() {
		return scope;
	}
}
