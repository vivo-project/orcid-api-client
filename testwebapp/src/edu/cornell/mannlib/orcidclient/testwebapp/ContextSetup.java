/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
public class ContextSetup implements ServletContextListener {
	private static final Log log = LogFactory.getLog(ContextSetup.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		log.info("ContextSetup.contextInitialized not implemented.");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.info("ContextSetup.contextDestroyed not implemented.");
	}

}
