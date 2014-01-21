/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.actions;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

import edu.cornell.mannlib.orcidclient.OrcidClientContext;
import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.auth.AccessToken;
import edu.cornell.mannlib.orcidclient.orcidmessage.ExternalIdCommonName;
import edu.cornell.mannlib.orcidclient.orcidmessage.ExternalIdReference;
import edu.cornell.mannlib.orcidclient.orcidmessage.ExternalIdUrl;
import edu.cornell.mannlib.orcidclient.orcidmessage.ExternalIdentifier;
import edu.cornell.mannlib.orcidclient.orcidmessage.ExternalIdentifiers;
import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidBio;
import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidMessage;
import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidProfile;
import edu.cornell.mannlib.orcidclient.orcidmessage.Visibility;

/**
 * Example using Curl:
 * 
 * <pre>
 * curl -H 'Content-Type: application/orcid+xml' 
 *      -H 'Authorization: Bearer 5be2dfe5-b834-436e-8c10-7b3fcf65e1a2' 
 *      -d '@/Documents/XML/add_id.xml' 
 *      -X POST 'http://api.sandbox-1.orcid.org/v1.0.23/0000-0001-6259-6530/orcid-bio/external-identifiers'
 * </pre>
 * 
 * Data file to send:
 * 
 * <pre>
 * <orcid-message 
 *         xsi:schemaLocation="http://www.orcid.org/ns/orcid https://raw.github.com/ORCID/ORCID-Source/master/orcid-model/src/main/resources/orcid-message-1.0.23.xsd" 
 *         xmlns="http://www.orcid.org/ns/orcid">
 *     <message-version>1.0.23</message-version>
 *     <orcid-profile>
 *         <orcid-bio>
 *             <external-identifiers visibility="public">
 *                 <external-identifier>
 *                     <external-id-common-name>My ID system</external-id-common-name>
 *                     <external-id-reference>#1234-567-8</external-id-reference>
 *                     <external-id-url>http://www.myid.com/1234-567-8</external-id-url>
 *                 </external-identifier>
 *             </external-identifiers>
 *         </orcid-bio>
 *     </orcid-profile>
 * </orcid-message>
 * </pre>
 * 
 * @see http://support.orcid.org/knowledgebase/articles/186983-add-external-ids-
 *      technical-developer
 */
public class AddExternalId {
	private static final Log log = LogFactory.getLog(AddExternalId.class);

	private final OrcidClientContext occ;

	public AddExternalId() throws OrcidClientException {
		this.occ = OrcidClientContext.getInstance();
	}

	public OrcidMessage exec(String externalIdCommonName,
			String externalIdReference, String externalIdUrl,
			AccessToken accessToken) throws OrcidClientException {

		String requestUrl = occ.getApiBaseUrl() + accessToken.getOrcid()
				+ "/orcid-bio/external-identifiers";

		try {
			OrcidMessage outMessage = buildOutgoingMessage(
					externalIdCommonName, externalIdReference, externalIdUrl);
			String outString = occ.marshall(outMessage);
			log.debug("Outgoing string: " + outString);

			Request request = Request
					.Post(requestUrl)
					.addHeader("Content-Type", "application/orcid+xml")
					.addHeader(
							"Authorization",
							accessToken.getTokenType() + " "
									+ accessToken.getAccessToken())
					.bodyString(outString,
							ContentType.APPLICATION_FORM_URLENCODED);
			Response response = request.execute();
			Content content = response.returnContent();
			String string = content.asString();
			log.debug("Content from AddExternalID was: " + string);

			return occ.unmarshall(string);
		} catch (HttpResponseException e) {
			// Not authorized. Something funky.
			log.error("HttpResponse status code: " + e.getStatusCode(), e);
		} catch (IOException e) {
			log.error("problem in HTTP communication.", e);
		} catch (OrcidClientException e) {
			log.error("failed to parse the message.", e);
		}
		return null;

	}

	private OrcidMessage buildOutgoingMessage(String externalIdCommonName,
			String externalIdReference, String externalIdUrl) {
		ExternalIdCommonName eicn = new ExternalIdCommonName();
		eicn.setContent(externalIdCommonName);

		ExternalIdReference eir = new ExternalIdReference();
		eir.setContent(externalIdReference);

		ExternalIdUrl eiu = new ExternalIdUrl();
		eiu.setValue(externalIdUrl);

		ExternalIdentifier ei = new ExternalIdentifier();
		ei.setExternalIdCommonName(eicn);
		ei.setExternalIdReference(eir);
		ei.setExternalIdUrl(eiu);

		ExternalIdentifiers eis = new ExternalIdentifiers();
		eis.getExternalIdentifier().add(ei);
		eis.setVisibility(Visibility.PUBLIC);

		OrcidBio ob = new OrcidBio();
		ob.setExternalIdentifiers(eis);

		OrcidProfile op = new OrcidProfile();
		op.setOrcidBio(ob);

		OrcidMessage om = new OrcidMessage();
		om.setOrcidProfile(op);
		om.setMessageVersion(occ.getMessageVersion());

		return om;
	}

}
