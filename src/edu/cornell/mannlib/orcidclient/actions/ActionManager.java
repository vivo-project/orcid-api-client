/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.actions;

import javax.servlet.http.HttpServletRequest;

import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidMessage;

/**
 * TODO
 */
public class ActionManager {
	private final OrcidClientContext context;
	private final HttpServletRequest req;

	public ActionManager(OrcidClientContext context, HttpServletRequest req) {
		this.context = context;
		this.req = req;
	}

	public OrcidMessage execute(ApiAction action) {
		// TODO Auto-generated method stub
		throw new RuntimeException(
				"OrcidActionManager.execute() not implemented.");
	}

}
