/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.actions;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIUtils;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting;
import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidMessage;

/**
 * <pre>
 * curl -H "Accept: application/orcid+xml" 
 *      'http://pub.sandbox-1.orcid.org/v1.1/0000-0001-7857-2795/orcid-bio' 
 *      -L -i
 * </pre>
 * 
 * @see http://support.orcid.org/knowledgebase/articles/
 *      132271-retrieving-data-with -the-public-api
 */
public class ReadPublicBioAction {
	private static final Log log = LogFactory.getLog(ReadPublicBioAction.class);

	private final OrcidClientContext occ;

	public ReadPublicBioAction() {
		this.occ = OrcidClientContext.getInstance();
	}

	public OrcidMessage execute(String orcid) throws OrcidClientException {
		try {
			URI baseUri = new URI(occ.getSetting(Setting.PUBLIC_API_BASE_URL));
			String requestUrl = URIUtils.resolve(baseUri, orcid + "/orcid-bio")
					.toString();
			Request request = Request.Get(requestUrl).addHeader("Content-Type",
					"application/orcid+xml");
			Response response = request.execute();
			Content content = response.returnContent();
			String string = content.asString();
			return occ.unmarshall(string);
		} catch (URISyntaxException e) {
			throw new OrcidClientException(
					"API_BASE_URL is not syntactically valid.", e);
		} catch (HttpResponseException e) {
			// Bad status code? Something funky.
			log.error("HttpResponse status code: " + e.getStatusCode());
			throw new OrcidClientException(
					"Failed to read profile. HTTP status code="
							+ e.getStatusCode(), e);
		} catch (IOException e) {
			throw new OrcidClientException("Failed to read profile", e);
		}
	}

}
