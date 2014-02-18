/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidMessage;

/**
 * TODO
 * 
 * <pre>
 *  On doGet, parse the URL:
 *    GET /0000-0003-3479-6011/orcid-bio
 *    curl -H "Accept: application/orcid+xml" 
 *      'http://pub.sandbox-1.orcid.org/v1.1/0000-0001-7857-2795/orcid-bio' 
 *      -L -i
 *    if we have a bio for the ORCID, return it
 *        don't show any visibility="limited", OK?
 *    else 404
 * </pre>
 */
public class PublicBioAction {
	private static final Log log = LogFactory.getLog(PublicBioAction.class);

	private final HttpServletRequest req;
	private final HttpServletResponse resp;

	public static boolean matches(String pathInfo) {
		String[] parts = pathInfo.split("/");
		log.debug("parts is " + Arrays.toString(parts));
		return (parts.length == 2) && ("orcid-bio".equals(parts[1]));
	}

	public PublicBioAction(HttpServletRequest req, HttpServletResponse resp) {
		this.req = req;
		this.resp = resp;
	}

	public void doGet() throws IOException, OrcidClientException {
		String[] parts = req.getPathInfo().split("/");
		String orcid = parts[0];
		log.debug("Request for public bio of " + orcid);

		if (OrcidProfiles.hasProfile(orcid)) {
			showSuccess(orcid);

		} else {
			showNoProfile(orcid);
		}
	}

	private void showSuccess(String orcid) throws IOException,
			OrcidClientException {
		OrcidMessage profile = OrcidProfiles.getProfile(orcid);
		resp.setContentType("application/vnd.orcid+xml");
		resp.getWriter().println(OrcidMessageUtils.marshall(profile));
	}

	private void showNoProfile(String orcid) throws IOException {
		resp.sendError(SC_NOT_FOUND);
	}

}
