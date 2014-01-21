/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

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

}
