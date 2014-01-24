/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.testwebapp;

import static edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus.State.PENDING;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CLIENT_ID;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CLIENT_SECRET;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import edu.cornell.mannlib.orcidclient.auth.AccessToken;
import edu.cornell.mannlib.orcidclient.auth.AccessTokenFormatException;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationManager;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationStatus;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;

/**
 * TODO
 */
public class CallbackController extends HttpServlet {
	private static final Log log = LogFactory.getLog(CallbackController.class);

	private OrcidClientContext occ;

	@Override
	public void init() throws ServletException {
		occ = OrcidClientContext.getInstance();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String token = req.getParameter("state");
		if (token == null) {
			fail(req, resp, "Request did not contain a 'state' parameter");
			return;
		}

		AuthorizationManager authManager = occ.getAuthorizationManager(req);
		AuthorizationStatus authStatus = authManager
				.getAuthorizationStatus(token);
		if (authStatus.getState() != PENDING) {
			fail(req, resp, "Request not pending: token=, status=" + authStatus);
			return;
		}

		String authCode = req.getParameter("code");
		if (authCode == null) {
			fail(req, resp, "No access code returned: " + dumpRequest(req));
		}

		try {
			authStatus.setSuccess(getAccessTokenFromAuthCode(authCode));
		} catch (Exception e) {
			log.error("Failed to get the Access Token", e);
			authStatus.setFailure(e.toString());
			fail(req, resp, "Failed to get the Access Token: " + e);
		}
	}

	private AccessToken getAccessTokenFromAuthCode(String authCode)
			throws ClientProtocolException, IOException,
			AccessTokenFormatException {

		String uri = occ.getAccessTokenRequestUrl();
		HttpPost postRequest = new PostRequestBuilder(uri)
				.header("Accept", "application/json")
				.add("client_id", occ.getSetting(CLIENT_ID))
				.add("client_secret", occ.getSetting(CLIENT_SECRET))
				.add("grant_type", "authorization_code").add("code", authCode)
				.add("redirect_uri", occ.getCallbackUrl()).build();

		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			CloseableHttpResponse response = httpClient.execute(postRequest);
			try {
				String content = EntityUtils.toString(response.getEntity());
				return new AccessToken(content);
			}finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			Throwable cause = e.getCause();
			if (cause instanceof AccessTokenFormatException) {
				throw (AccessTokenFormatException) cause;
			} else {
				throw e;
			}
		} finally {
			httpClient.close();
		}
	}

	private void fail(HttpServletRequest req, HttpServletResponse resp,
			String message) throws IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<html><head></head><body>");
		out.println("<h1>Callback Failure</h1>");
		out.println("<h2>" + message + "</h2>");
		out.println("</body></html");
	}

	private String dumpRequest(HttpServletRequest req) {
		@SuppressWarnings("unchecked")
		Map<String, String[]> rawMap = req.getParameterMap();
		Map<String, List<String>> prettyMap = new HashMap<>();
		for (String key : rawMap.keySet()) {
			prettyMap.put(key, Arrays.asList(rawMap.get(key)));
		}
		return "Request: " + req.getRequestURL() + ", parameters=" + prettyMap;
	}

	private static class AccessTokenResponseHandler implements
			ResponseHandler<AccessToken> {
		private int statusCode;
		private String reasonPhrase = "";
		private String content = "";

		@Override
		public AccessToken handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			StatusLine statusLine = response.getStatusLine();
			this.statusCode = statusLine.getStatusCode();
			this.reasonPhrase = statusLine.getReasonPhrase();

			if (statusCode >= 300) {
				throw new HttpResponseException(statusLine.getStatusCode(),
						statusLine.getReasonPhrase());
			}
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				throw new ClientProtocolException(
						"Response contains no content");
			}
			this.content = EntityUtils.toString(entity);
			try {
				return new AccessToken(this.content);
			} catch (AccessTokenFormatException e) {
				throw new ClientProtocolException(
						"Failed to parse the access token", e);
			}
		}

		public int getStatusCode() {
			return statusCode;
		}

		public String getReasonPhrase() {
			return reasonPhrase;
		}

		public String getContent() {
			return content;
		}

	}

	private static class PostRequestBuilder {
		private final HttpPost post;
		private final List<NameValuePair> parameters = new ArrayList<>();

		PostRequestBuilder(String url) {
			this.post = new HttpPost(url);
		}

		PostRequestBuilder add(String name, String value) {
			parameters.add(new BasicNameValuePair(name, value));
			return this;
		}

		PostRequestBuilder header(String name, String value) {
			post.addHeader(name, value);
			return this;
		}

		HttpPost build() {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters,
					Consts.UTF_8);
			post.setEntity(entity);
			return post;
		}
	}
}
