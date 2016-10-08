/*
 * @(#)XMLConverter.java $Revision: 13734 $ $Date: 2012-05-31 11:09:39 -0400 (Thu, 31 May 2012) $
 *
 * Copyright (c) 2004 Carnegie Mellon University.
 */
package edu.cmu.pact.SocketProxy;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.Utilities.trace;


/**
 * <p>Converts between Comm and XML tutor messages. Based on code written
 * by Gus Prevas. The methods in this class throws <tt>RuntimeException</tt>,
 * as they are invoked from within threads which cannot easily recover from
 * errors.</p>
 *
 * @version       $Revision: 13734 $ $Date: 2012-05-31 11:09:39 -0400 (Thu, 31 May 2012) $
 * @author        John A Rinderle
 * <a href="mailto:jar2@andrew.cmu.edu">(jar2@andrew.cmu.edu)</a>
 */
public final class XMLConverter {

    // =======================================================================
	// Public static methods
	// =======================================================================

	private static String cleanse(String rawMsg) {
		return rawMsg.replaceAll ("\n", "");
	}

	public static String xmlToString(Element msgElt) {
		return outputter.outputString(msgElt);
	}

	public static String xmlToComm (String rawMsg) {
		return xmlToCommObject(rawMsg).toString();
	}
	/**
	 * <p>Converts an XML message object to a Comm message without XML parsing.</p>
	 * @param     xmo      XML MessageObject
	 * @return    Comm message
	 */
	public static MessageObject xmlObjectToCommObject(edu.cmu.pact.ctat.MessageObject xmo) {

		String verb = xmo.getVerb();
		if (verb == null || verb.length() < 1) {
			trace.err("verb null or \"\" in XML MessageObject; using default "+
					edu.cmu.pact.ctat.MessageObject.DEFAULT_VERB+"; message:\n  "+xmo);
			verb = edu.cmu.pact.ctat.MessageObject.DEFAULT_VERB;
		}
		MessageObject mo = new MessageObject(verb);

		Vector pNames = new Vector(xmo.getPropertyNames());
		Vector pValues = new Vector(xmo.getPropertyValues());

		mo.addParameter("PROPERTYNAMES", pNames);
		mo.addParameter("PROPERTYVALUES", pValues);
		return mo;
	}

	/**
	 * <p>Converts an XML tutor message to a Comm tutor message.</p>
	 *
	 * @param     rawMsg      XML message, as a string
	 * @return    Comm message, as a string
	 * @throws    NullPointerException  if <tt>rawMsg</tt> is <tt>null</tt>
	 * @throws    RuntimeException  if an error occurs while parsing the XML message
	 */
	public static MessageObject xmlToCommObject(String rawMsg) {
		if (rawMsg == null) {
			throw (new NullPointerException("'rawMsg' cannot be null"));
		}

		// Parse the string into XML document
		Document msgDoc;

		try {
			Reader strIn = new StringReader(cleanse(rawMsg));
			SAXBuilder builder = new SAXBuilder(false);
			msgDoc = builder.build(strIn);
		} catch (Exception e) {
			throw (new RuntimeException("error parsing XML message", e));
		}

		//trace.out (5, "XMLConverter", "xml message = " + cleanse(rawMsg));

		// Locate the root element
		Element rootElmnt = msgDoc.getRootElement();

		// Parse the verb of the message
		Element verbElmnt = rootElmnt.getChild("verb");
		String verb = verbElmnt.getTextNormalize();
		MessageObject mo = new MessageObject(verb);

		// Parse the properties of the message
		Element propsElmnt = rootElmnt.getChild("properties");

		List pNames = new Vector();
		List pValues = new Vector();

		// Iterate over the message properties
		for (Iterator i = propsElmnt.getChildren().iterator(); i.hasNext();) {
			// Get the next message property
			Element pElement = (Element) i.next();

			// Parse the message property name
			String pName = pElement.getName();
			pNames.add(pName);

			// Parse the value(s) of the property
			Collection valueElts = pElement.getChildren("value");

			if ((valueElts == null) || valueElts.isEmpty()) {
				Object v = null;
				List otherElts = pElement.getChildren();
				if (otherElts != null && otherElts.size() > 0)
					v = buildValueFromElements(pName, otherElts);
				else
					v = buildValue(pName, pElement.getTextNormalize());
				pValues.add(v);
				if (v != null && MessageObject.TRANSACTION_ID.equalsIgnoreCase(pName))
					mo.setTransactionId(v.toString());
			} else {
				List values = new Vector();

				for (Iterator j = valueElts.iterator(); j.hasNext();) {
					Element valueElement = (Element) j.next();
					String valStr = valueElement.getTextNormalize();
					values.add(buildValue(pName, valStr));
				}

				pValues.add(values);
			}
		}

		// Set the message properties
		mo.addParameter("PROPERTYNAMES", (Vector) pNames);
		mo.addParameter("PROPERTYVALUES", (Vector) pValues);
		
		// Convert the message to a string
		return mo;
	}

	/**
	 * For converting embedded XML to strings.
	 */
	private static XMLOutputter outputter =
		new XMLOutputter(
				Format.getCompactFormat().setOmitDeclaration(true).setLineSeparator("").setIndent("")
		);

	/**
	 * Extract the contents of a nested property element.
	 * @param pName property name, for debugging
	 * @param otherElts nested element(s)
	 * @return List or scalar references to nested element(s) 
	 */
	private static Object buildValueFromElements(String pName, List otherElts) {
		if (otherElts.size() == 1)
			return otherElts.get(0);
		else
			return otherElts;
	}

	/**
	 * Convert a Comm message to a new-format message.
	 * @param mo
	 * @return the new message
	 */
	public static edu.cmu.pact.ctat.MessageObject commToNewMO(MessageObject mo) {
		edu.cmu.pact.ctat.MessageObject result = edu.cmu.pact.ctat.MessageObject.parse(commToXml(mo));
        Object object = null;
        try {
            object = mo.getParameter("OBJECT");
    		if (object != null)
    			result.setProperty("OBJECT", object, true);
        } catch (DorminException e) {
            if (trace.getDebugCode("sp")) trace.out("sp", "can't find object for message\n  "+mo);
        }
		return result;
	}
	
	/**
	 * <p>Converts an Comm tutor message to an XML tutor message.</p>
	 *
	 * @param     mo      Comm message as MessageObject
	 * @return    XML message, as a string
	 * @throws    NullPointerException  if <tt>rawMsg</tt> is <tt>null</tt>
	 * @throws    RuntimeException  if an error occurs while parsing the Comm message
	 */
	public static String commToXml(MessageObject mo) {
		if (mo == null) {
			throw (new NullPointerException("'mo' cannot be null"));
		}
		StringBuffer strBfr = new StringBuffer();

		// Generate XML from the message object
		strBfr.append("<message>");

		strBfr.append("<verb>");
		strBfr.append(escapeXMLChars(mo.extractVerb()));
		strBfr.append("</verb>");

		strBfr.append("<properties>");

		try {
			Vector  iValues = mo.extractListValue("PROPERTYNAMES");
			Vector  jValues = mo.extractListValue("PROPERTYVALUES");
			

			if (!iValues.isEmpty() && !jValues.isEmpty())
			{
				Iterator i = iValues.iterator();
				Iterator j = jValues.iterator();

			while (i.hasNext() && j.hasNext()) {
				String rawName = i.next().toString();
				Object rawValue = j.next();

				String xmlName = escapeElementName(rawName);
				strBfr.append("<").append(xmlName).append(">");

				if (rawValue instanceof Vector) {
					Vector v = (Vector) rawValue;

					for (Iterator k = v.iterator(); k.hasNext();) {
						strBfr.append("<value>");
						strBfr.append(escapeXMLChars(k.next().toString()));
						strBfr.append("</value>");
					}

				} else {
					strBfr.append(escapeXMLChars(rawValue.toString()));
				}

				strBfr.append("</").append(xmlName).append(">");
			 }
			}
		} catch (DorminException e) {
			throw (new RuntimeException("invalid comm message", e));
		}
		String trId = mo.getTransactionId(); 
		if (trId != null && trId.length() > 0) {
			strBfr.append("<").append(MessageObject.TRANSACTION_ID).append(">");
			strBfr.append(trId);
			strBfr.append("</").append(MessageObject.TRANSACTION_ID).append(">");
		}
		strBfr.append("</properties>").append("</message>");

		// Return XML message as a string
		return strBfr.toString();
	}


	// =======================================================================
	// Private static methods
	// =======================================================================

	/**
	 * <p>Gets an <tt>ObjectProxy</tt> instance.</p>
	 *
	 * @return    object proxy
	private static ObjectProxy getObjectProxy() {
		// Create an initialize universal tool proxy
		UniversalToolProxy utp = new UniversalToolProxy();
		// utp.setShowDebugInfo(true); //
		utp.init();
		return utp.getInterfaceProxy();
	}
	 */

	/**
	 * <p>Generates a property value from a name-value pair. This method
	 * assumes that its arguments are not <tt>null</tt>.</p>
	 *
	 * @param     name     name of the property
	 * @param     value    value of property, as a string
	 * @throws    NumberFormatException  if the value cannot be parsed into the correct type
	 */
	private static Object buildValue(String name, String valStr) {
		//assert (name != null);
		//assert (valStr != null);

		if ("Rows".equals(name) || "Columns".equals(name)) {
			return (new Integer(valStr));

		} else if ("Number".equals(name)) {
			return (new Integer(valStr));

		} else if ("true".equals(valStr) || "false".equals(valStr)) {
			return (new Boolean(valStr));

		} else {
			return valStr;
		}
	}

	/**
	 * <p>Escapes reserved characters for use in XML.</p>
	 *
	 * @param     str     the string to escape
	 * @return    the escaped string
	 * @throws    NullPointerException  if <tt>str</tt> is <tt>null</tt>
	 */
	private static String escapeXMLChars(String str) {
		if (str == null) {
			throw (new NullPointerException("'str' cannot be null"));
		}

		StringBuffer rslt = new StringBuffer();

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '&') {
				rslt.append("&amp;");
			} else if (str.charAt(i) == '<') {
				rslt.append("&lt;");
			} else if (str.charAt(i) == '>') {
				rslt.append("&gt;");
			} else {
				rslt.append(str.charAt(i));
			}
		}

		return rslt.toString();
	}

	/**
	 * <p>Formats XML element names.</p>
	 *
	 * @param     str     the string to format
	 * @return    the formatted string
	 * @throws    NullPointerException  if <tt>str</tt> is <tt>null</tt>
	 */
	private static String escapeElementName(String str) {
		String rslt = escapeXMLChars(str);
		rslt = rslt.replace('[', '_');
		rslt = rslt.replace(']', '_');
		rslt = rslt.replace(',', '_');
		rslt = rslt.replace(' ', '_');

		return rslt;
	}
}
