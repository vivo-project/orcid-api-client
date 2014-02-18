/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidMessage;

/**
 * TODO
 * 
 * On init, read the profiles into OrcidMessage objects using the JAXB classes.
 * Map them by their ORCID IDs
 */
public class OrcidProfiles {
	private static final Log log = LogFactory.getLog(OrcidProfiles.class);

	private static final Map<String, OrcidMessage> map = new HashMap<>();

	/**
	 * @param servletContext
	 */
	public static void load(ServletContext servletContext) {
		log.error("BOGUS OrcidProfiles.load()");
	}

}
