/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.orcidclient;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import edu.cornell.mannlib.orcidclient.orcidmessage.OrcidMessage;

/**
 * TODO
 */
public class MessageParser {
	public OrcidMessage parse(String xml) throws OrcidMessageException {
		try {
			String packageName = OrcidMessage.class.getPackage().getName();
			JAXBContext jc = JAXBContext.newInstance(packageName);
			Unmarshaller u = jc.createUnmarshaller();
			StreamSource source = new StreamSource(new StringReader(xml));
			JAXBElement<OrcidMessage> doc = u.unmarshal(source, OrcidMessage.class);
			return doc.getValue();
		} catch (JAXBException e) {
			throw new OrcidMessageException("Failed to parse the response", e);
		}
	}
}
