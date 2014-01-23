/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.API_BASE_URL;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CALLBACK_PATH;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CLIENT_ID;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CLIENT_SECRET;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.MESSAGE_VERSION;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.OAUTH_URL;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.WEBAPP_BASE_URL;

import java.util.EnumMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting;

/**
 * TODO
 */
public class OrcidContextSetup implements ServletContextListener {
	private static final Log log = LogFactory.getLog(OrcidContextSetup.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			Map<Setting, String> settings = new EnumMap<>(Setting.class);
			settings.put(API_BASE_URL, "http://api.sandbox-1.orcid.org/v1.0.23");
			settings.put(OAUTH_URL, "http://sandbox-1.orcid.org/oauth");
			settings.put(CLIENT_ID, "0000-0002-4639-029X");
			settings.put(CLIENT_SECRET, "09df8b85-ec19-41ff-944a-9949baf9a6bb");
			settings.put(MESSAGE_VERSION, "1.0.23");
			settings.put(CALLBACK_PATH, "callback");
			settings.put(WEBAPP_BASE_URL,
					"http://jeb228-dev.library.cornell.edu/orcivo");

			OrcidClientContext.initialize(settings);
		} catch (OrcidClientException e) {
			log.error("Failed to initialize OrcidClientContent", e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// Nothing to tear down.
	}

}
