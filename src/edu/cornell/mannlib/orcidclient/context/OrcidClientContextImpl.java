/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient.context;

import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.CALLBACK_PATH;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.OAUTH_URL;
import static edu.cornell.mannlib.orcidclient.context.OrcidClientContext.Setting.WEBAPP_BASE_URL;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIUtils;

import edu.cornell.mannlib.orcidclient.OrcidClientException;
import edu.cornell.mannlib.orcidclient.actions.ActionManager;
import edu.cornell.mannlib.orcidclient.auth.AuthorizationManager;
import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidMessage;

/**
 * TODO
 */
public class OrcidClientContextImpl extends OrcidClientContext {
	private static final Log log = LogFactory
			.getLog(OrcidClientContextImpl.class);

	private final Map<Setting, String> settings;
	private final JAXBContext jaxbContext;

	private final String callbackUrl;
	private final String authCodeRequestUrl;
	private final String accessTokenRequestUrl;

	public OrcidClientContextImpl(Map<Setting, String> settings)
			throws OrcidClientException {
		for (Setting s : Setting.values()) {
			if (!settings.containsKey(s)) {
				throw new OrcidClientException("Setting " + s + " is required");
			}
		}
		this.settings = new EnumMap<>(settings);

		try {
			String packageName = OrcidMessage.class.getPackage().getName();
			jaxbContext = JAXBContext.newInstance(packageName);

			URI baseUri = new URI(getSetting(WEBAPP_BASE_URL));
			callbackUrl = URIUtils.resolve(baseUri, getSetting(CALLBACK_PATH))
					.toString();

			URI oauthUri = new URI(getSetting(OAUTH_URL));
			authCodeRequestUrl = URIUtils.resolve(oauthUri, "/authorize")
					.toString();
			accessTokenRequestUrl = URIUtils.resolve(oauthUri, "/token")
					.toString();
		} catch (JAXBException | URISyntaxException e) {
			throw new OrcidClientException(
					"Failed to create the OrcidClientContext", e);
		}
	}

	@Override
	public String getSetting(Setting key) {
		return settings.get(key);
	}

	@Override
	public String getCallbackUrl() {
		return callbackUrl;
	}

	@Override
	public String getAuthCodeRequestUrl() {
		return authCodeRequestUrl;
	}

	@Override
	public String getAccessTokenRequestUrl() {
		return accessTokenRequestUrl;
	}

	@Override
	public ActionManager getActionManager(HttpServletRequest req) {
		return new ActionManager(this, req);
	}

	@Override
	public AuthorizationManager getAuthorizationManager(HttpServletRequest req) {
		return new AuthorizationManager(this, req);
	}

	@Override
	public String marshall(OrcidMessage message) throws OrcidClientException {
		try {
			Marshaller m = jaxbContext.createMarshaller();
			m.setProperty("jaxb.formatted.output", Boolean.TRUE);

			StringWriter sw = new StringWriter();
			m.marshal(message, sw);
			log.debug("marshall message=" + message + "\n, string=" + sw);
			return sw.toString();
		} catch (PropertyException e) {
			throw new OrcidClientException("Failed to create the Marshaller", e);
		} catch (JAXBException e) {
			throw new OrcidClientException("Failed to marshall the message '"
					+ message + "'", e);
		}
	}

	@Override
	public OrcidMessage unmarshall(String xml) throws OrcidClientException {
		try {
			Unmarshaller u = jaxbContext.createUnmarshaller();

			StreamSource source = new StreamSource(new StringReader(xml));
			JAXBElement<OrcidMessage> doc = u.unmarshal(source,
					OrcidMessage.class);
			log.debug("unmarshall string=" + xml + "\n, message="
					+ doc.getValue());
			return doc.getValue();
		} catch (JAXBException e) {
			throw new OrcidClientException("Failed to unmarshall the message '"
					+ xml + "'", e);
		}
	}

	@Override
	public String toString() {
		return "OrcidClientContextImpl[settings=" + settings
				+ ", callbackUrl=" + callbackUrl + ", authCodeRequestUrl="
				+ authCodeRequestUrl + ", accessTokenRequestUrl="
				+ accessTokenRequestUrl + "]";
	}

}
