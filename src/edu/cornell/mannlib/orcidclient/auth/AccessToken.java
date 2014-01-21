/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.auth;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 * The information associated with an ORCID Access Token.
 */
public class AccessToken {
	private final String accessToken;
	private final String tokenType;
	private final String refreshToken;
	private final Integer expiresIn;
	private final String scope;
	private final String orcid;

	public AccessToken(String jsonString) throws AccessTokenFormatException {
		try {
			JsonReader reader = Json.createReader(new StringReader(jsonString));
			JsonObject json = (JsonObject) reader.read();
			this.accessToken = json.getString("access_token");
			this.tokenType = json.getString("token_type");
			this.refreshToken = json.getString("refresh_token");
			this.expiresIn = json.getInt("expires_in");
			this.scope = json.getString("scope");
			this.orcid = json.getString("orcid");
		} catch (Exception e) {
			throw new AccessTokenFormatException(
					"Failed to parse the ORID Access Token. JSON is '"
							+ jsonString + "'");
		}
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public Integer getExpiresIn() {
		return expiresIn;
	}

	public String getScope() {
		return scope;
	}

	public String getOrcid() {
		return orcid;
	}

	@Override
	public String toString() {
		return "AccessToken[accessToken=" + accessToken + ", tokenType="
				+ tokenType + ", refreshToken=" + refreshToken + ", expiresIn="
				+ expiresIn + ", scope=" + scope + ", orcid=" + orcid + "]";
	}

}
