/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.actions;

import javax.servlet.http.HttpServletRequest;

import edu.cornell.mannlib.orcidclient.actions.version_1_0.UpdateBioAction;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.responses.message_1_2.OrcidMessage;

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

	public AddExternalIdAction createAddExternalIdAction() {
		return new edu.cornell.mannlib.orcidclient.actions.version_1_0.AddExternalIdAction();
	}

	public ReadProfileAction createReadProfileAction() {
		return new edu.cornell.mannlib.orcidclient.actions.version_1_0.ReadProfileAction();
	}

	public ReadPublicBioAction createReadPublicBioAction() {
		return new edu.cornell.mannlib.orcidclient.actions.version_1_0.ReadPublicBioAction();
	}

	public UpdateBioAction createUpdateBioAction() {
		return new edu.cornell.mannlib.orcidclient.actions.version_1_0.UpdateBioAction();
	}
}
