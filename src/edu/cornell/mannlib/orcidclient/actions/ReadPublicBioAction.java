/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.actions;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

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
import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidId;
import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidMessage;

/**
 * <pre>
 * curl -H "Accept: application/orcid+xml" 
 *      'http://pub.sandbox-1.orcid.org/v1.1/0000-0001-7857-2795/orcid-bio' 
 *      -L -i
 * </pre>
 * 
 * @see http://support.orcid.org/knowledgebase/articles/
 *      132271-retrieving-data-with-the-public-api
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
			log.debug("Public Bio: " + string);
			OrcidMessage message = occ.unmarshall(string);
			if (message.getOrcidProfile().getOrcidIdentifier() == null) {
				saveFrom1_0_23(string, message);
			}
			return message;
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

	/**
	 * If the public bio is returned in message format 1.0.23, we need to parse
	 * some of it manually.
	 */
	private void saveFrom1_0_23(String string, OrcidMessage message) {
		String path = null;
		Matcher mPath = Pattern.compile("<orcid>(.*)</orcid>").matcher(string);
		if (mPath.find()) {
			path = mPath.group(1);
		}

		String uri = null;
		Matcher mUri = Pattern.compile("<orcid-id>(.*)</orcid-id>").matcher(
				string);
		if (mUri.find()) {
			uri = mUri.group(1);
		}

		OrcidId orcidId = new OrcidId();
		List<JAXBElement<String>> content = orcidId.getContent();
		content.add(new JAXBElement<String>(new QName(
				"http://www.orcid.org/ns/orcid", "path"), String.class, path));
		content.add(new JAXBElement<String>(new QName(
				"http://www.orcid.org/ns/orcid", "uri"), String.class, uri));
		message.getOrcidProfile().setOrcidIdentifier(orcidId);
		log.warn("Rescue from 1_0_23: path='" + path + "', uri='" + uri + "'");
	}

}
