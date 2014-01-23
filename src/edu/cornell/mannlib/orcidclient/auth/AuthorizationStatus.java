/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.auth;

import edu.cornell.mannlib.orcidclient.actions.ApiAction;

/**
 * Holds information about the authorization request.
 */
public class AuthorizationStatus {
	public enum State {
		NONE, PENDING, SUCCESS, DECLINED, FAILED
	}

	private final ApiAction action;
	private final String successUrl;
	private final String failureUrl;

	private State state = State.NONE;
	private AccessToken accessToken = AccessToken.NO_TOKEN;
	private String message = "";

	public AuthorizationStatus(ApiAction action, String successUrl,
			String failureUrl, State state) {
		this.action = action;
		this.successUrl = successUrl;
		this.failureUrl = failureUrl;
		this.state = state;
	}

	public String getId() {
		return String.valueOf(hashCode());
	}

	public ApiAction getAction() {
		return action;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public String getFailureUrl() {
		return failureUrl;
	}

	public State getState() {
		return state;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public String getMessage() {
		return message;
	}

	public void setSuccess(AccessToken accessToken) {
		this.state = State.SUCCESS;
		this.accessToken = accessToken;
	}

	public void setFailure(String message) {
		this.state = State.FAILED;
		this.message = message;
	}

	@Override
	public String toString() {
		return "AuthorizationStatus[action=" + action + ", successUrl="
				+ successUrl + ", failureUrl=" + failureUrl + ", state="
				+ state + ", accessToken=" + accessToken + ", message="
				+ message + "]";
	}

}
