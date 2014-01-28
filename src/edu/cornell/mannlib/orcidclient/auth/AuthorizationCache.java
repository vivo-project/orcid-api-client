/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.auth;

import static edu.cornell.mannlib.orcidclient.actions.ApiAction.NO_ACTION;
import static edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus.State.NONE;
import static edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus.State.PENDING;

import java.util.EnumMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.orcidclient.actions.ApiAction;

/**
 * TODO
 */
public class AuthorizationCache {
	private static final Log log = LogFactory.getLog(AuthorizationCache.class);

	private static final String ATTRIBUTE_NAME = AuthorizationCache.class
			.getName();

	public static AuthorizationCache getCache(HttpServletRequest req) {
		HttpSession session = req.getSession();
		Object o = session.getAttribute(ATTRIBUTE_NAME);
		if (o instanceof AuthorizationCache) {
			return (AuthorizationCache) o;
		} else {
			AuthorizationCache cache = new AuthorizationCache();
			session.setAttribute(ATTRIBUTE_NAME, cache);
			return cache;
		}
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	Map<ApiAction, AuthorizationStatus> map = new EnumMap<>(ApiAction.class);

	public AuthorizationStatus createStatus(ApiAction action,
			String successUrl, String failureUrl) {
		log.debug("createStatus: action=" + action + ", successUrl="
				+ successUrl + ", failureUrl=" + failureUrl);
		AuthorizationStatus authStatus = new AuthorizationStatus(action,
				successUrl, failureUrl, PENDING);
		map.put(action, authStatus);
		return authStatus;
	}

	public AuthorizationStatus getStatus(ApiAction action) {
		AuthorizationStatus authStatus = map.get(action);
		if (authStatus == null) {
			authStatus = new AuthorizationStatus(action, "/", "/", NONE);
		}
		log.debug("getStatus for action: " + action +
				", status=" + authStatus);
		return authStatus;
	}

	public AuthorizationStatus getStatus(String token) {
		for (AuthorizationStatus as : map.values()) {
			if (as.getId().equals(token)) {
				log.debug("getStatus for token: " + token +
						", status=" + as);
				return as;
			}
		}
		log.debug("getStatus for token: " + token +
				", status=NONE");
		return new AuthorizationStatus(NO_ACTION, "/", "/", NONE);
	}

	public void clearStatus(ApiAction action) {
		map.remove(action);
	}

}
