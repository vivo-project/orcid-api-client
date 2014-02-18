/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.mockorcidapp;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO
 */
public class MockOrcidController extends HttpServlet {
	private static final Log log = LogFactory.getLog(MockOrcidController.class);
	
	private static final Pattern PATTERN_PUBLIC_BIO = null;
	private static final Pattern PATTERN_GET_PROFILE = null;
	private static final Pattern PATTERN_OAUTH_AUTHORIZE = null;
	private static final Pattern PATTERN_AUTH_RESPONSE = null;
	private static final Pattern PATTERN_OAUTH_TOKEN = null;

	@Override
	public void init() throws ServletException {
		OrcidProfiles.load(getServletContext());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String pathInfo = req.getPathInfo();
			if (PATTERN_PUBLIC_BIO.matcher(pathInfo).matches()) {
				new PublicBioAction(req, resp).doGet();
			} else if (PATTERN_GET_PROFILE.matcher(pathInfo).matches()) {
				new GetProfileAction(req, resp).doGet();
			} else if (PATTERN_OAUTH_AUTHORIZE.matcher(pathInfo).matches()) {
				new OauthAuthorizeAction(req, resp).doGet();
			} else if (PATTERN_AUTH_RESPONSE.matcher(pathInfo).matches()) {
				new AuthResponseAction(req, resp).doGet();
			} else {
				new BadRequestAction(req, resp).doGet();
			}
		} catch (Exception e) {
			new BadRequestAction(req, resp).exception(e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String pathInfo = req.getPathInfo();
			if (PATTERN_OAUTH_TOKEN.matcher(pathInfo).matches()) {
				new OauthTokenAction(req, resp).doPost();
			} else {
				new BadRequestAction(req, resp).doPost();
			}
		} catch (Exception e) {
			new BadRequestAction(req, resp).exception(e);
		}
	}

}

/**
 * TODO
 * 
 * <pre>
 *     
 * On doGet, parse the URL:
 *    GET /0000-0003-3479-6011/orcid-bio
 *    curl -H "Accept: application/orcid+xml" 
 *      'http://pub.sandbox-1.orcid.org/v1.1/0000-0001-7857-2795/orcid-bio' 
 *      -L -i
 *    if we have a bio for the ORCID, return it
 *        don't show any visibility="limited", OK?
 *    else 404
 * 
 *    GET /0000-0003-3479-6011/orcid-profile 
 *    Content-Type: application/vdn.orcid+xml
 *    Authorization: bearer 785e8e34-0f66-4c98-a138-c89bc8cb3886
 *    Same. Don't care what the auth code is.
 *    
 *    -------------------
 *    
 *    GET http://sandbox-1.orcid.org/oauth/authorize ?
 * 	      client_id=0000-0002-4639-029X & 
 * 	      scope=%2Forcid-profile%2Fread-limited &
 * 	      response_type=code & 
 * 	      redirect_uri=http%3A%2F%2Fjeb228-dev.library.cornell.edu%2Forcivo%2Fcallback & 
 * 	      state=1728933982
 *    If not logged in, show the login screen, with the parameters in hidden fields
 *        Else, redirect to /login with the orcid and parameters
 *    
 *    GET /login?orcid=xxxx-xxxx-xxxx-xxxx
 *        Record the log in, in the session. retrieve the auth parameters from the hidden fields, 
 *            and continue with the auth request
 *        Show the appropriate auth screen, with the scope in a hidden field
 *    
 *    GET /authRespond?scope&approve=true
 *        redirect to the callback
 *        on approval, 
 *           http://jeb228-dev.library.cornell.edu/orcivo/callback ? 
 *                code=Feb8OP & 
 *                state=1728933982
 *           where code is random. Store it with the parameters in the context.
 *        on disapproval,
 *           http://jeb228-dev.library.cornell.edu/orcivo/callback ? 
 *                error=access_denied &
 *                error_description=User+denied+access&state=372991735
 *    
 *    
 * On doPost, parse the URL:
 *    POST /oauth/token
 * 		List<NameValuePair> form = Form.form()
 * 				.add("client_id", context.getSetting(CLIENT_ID))
 * 				.add("client_secret", context.getSetting(CLIENT_SECRET))
 * 				.add("grant_type", "authorization_code")
 * 				.add("code", auth.getAuthorizationCode())
 * 				.add("redirect_uri", context.getCallbackUrl()).build();
 * 		Request request = Request.Post(context.getAccessTokenRequestUrl())
 * 				.addHeader("Accept", "application/json").bodyForm(form);
 *    Respond with 
 *    {
 *       "access_token":"785e8e34-0f66-4c98-a138-c89bc8cb3886",
 *       "token_type":"bearer",
 *       "refresh_token":"5ecda111-b6f9-4bd4-a21d-153962eabca9",
 *       "expires_in":628207503,
 *       "scope":"/orcid-profile/read-limited",
 *       "orcid":"0000-0003-3479-6011"
 *    }
 *    Where the ORCID is a truncation of the accessToken, which is a form of the auth_code.
 *    Scope is what was requested when the auth_code was created
 * 
 * </pre>
 */
