/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * TODO
 */
public class OauthAuthorizeAction {
	private final HttpServletRequest req;
	private final HttpServletResponse resp;

	public OauthAuthorizeAction(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}

	/**
	 * 
	 */
	public void doGet() {
		// TODO Auto-generated method stub
		throw new RuntimeException(
				"OauthAuthorizeAction.doGet() not implemented.");
	}

}
