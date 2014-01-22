/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidMessage;

/**
 * TODO
 */
public class OrcidClientContext {
	private static volatile OrcidClientContext instance;

	public static synchronized OrcidClientContext getInstance()
			throws OrcidClientException {
		if (instance == null) {
			instance = new OrcidClientContext();
		}
		return instance;
	}

	private final JAXBContext jaxbContext;

	private OrcidClientContext() throws OrcidClientException {
		try {
			String packageName = OrcidMessage.class.getPackage().getName();
			jaxbContext = JAXBContext.newInstance(packageName);
		} catch (JAXBException e) {
			throw new OrcidClientException(
					"Failed to create the OrcidClientContext", e);
		}
	}
	
	public String getClientId() {
		return "0000-0002-4639-029X";
	}

	public String getApiBaseUrl() {
		return "http://api.sandbox-1.orcid.org/v1.0.23/";
	}

	public String getMessageVersion() {
		return "1.0.23";
	}

	public String marshall(OrcidMessage message) throws OrcidClientException {
		try {
			Marshaller m = jaxbContext.createMarshaller();
			m.setProperty("jaxb.formatted.output", Boolean.TRUE);

			StringWriter sw = new StringWriter();
			m.marshal(message, sw);
			return sw.toString();
		} catch (PropertyException e) {
			throw new OrcidClientException("Failed to create the Marshaller", e);
		} catch (JAXBException e) {
			throw new OrcidClientException("Failed to marshall the message '"
					+ message + "'", e);
		}
	}

	public OrcidMessage unmarshall(String xml) throws OrcidClientException {
		try {
			Unmarshaller u = jaxbContext.createUnmarshaller();

			StreamSource source = new StreamSource(new StringReader(xml));
			JAXBElement<OrcidMessage> doc = u.unmarshal(source,
					OrcidMessage.class);
			return doc.getValue();
		} catch (JAXBException e) {
			throw new OrcidClientException("Failed to unmarshall the message '"
					+ xml + "'", e);
		}
	}

}
